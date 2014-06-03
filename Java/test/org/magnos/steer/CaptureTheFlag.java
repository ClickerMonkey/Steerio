
package org.magnos.steer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.magnos.steer.behavior.SteerAvoidObstacles;
import org.magnos.steer.behavior.SteerBasicExample;
import org.magnos.steer.behavior.SteerContainment;
import org.magnos.steer.behavior.SteerDrive;
import org.magnos.steer.behavior.SteerSeparation;
import org.magnos.steer.behavior.SteerTo;
import org.magnos.steer.filter.FilterView;
import org.magnos.steer.obstacle.Bounds;
import org.magnos.steer.obstacle.Plane;
import org.magnos.steer.spatial.SearchCallbackArray;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialEntityObstacle;
import org.magnos.steer.spatial.array.SpatialArray;
import org.magnos.steer.target.TargetClosest;
import org.magnos.steer.target.TargetLocal;
import org.magnos.steer.target.TargetWeakest;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.util.FieldOfView;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.core.EntityList;

/**
 * Defense:
 *     1. Go after nearby opponents
 *     2. Stay within X of your flag
 *     3. Wander
 * Offense:
 *     If you're on your side:
 *         If the opponent has the flag
 *             1. Attack capturer
 *         If your team has the flag
 *             1. Guard flag holder
 *             2. Attack opponents
 *         If you have the flag
 *             1. Go towards base
 *         If you DON'T have the flag
 *             1. Attack opponents
 *             2. Go towards opponent's flag
 *     If you're on the opponents side
 *         If the opponent has the flag
 *             1. Attack capturer
 *         If your team has the flag
 *             1. Guard flag holder
 *         If you have the flag
 *             1. Stay away from opponent
 *             2. Go towards base
 *         If you DON'T have the flag
 *             1. Stay away from opponent
 *             2. Go towards opponent's flag
 *             
 * Simplified Offense:
 *      ~ team_has_flag = if your team has the flag
 *      ~ opponent_has_flag = 
 *      ~ capturer = if you have the flag
 *      ~ our_side = if you're on your side
 *      ~ opponent_side = if you're on your opponent side
 *      ~ has_prisoners = if you have team mates in the opponents prison AND you're within X units of opponents prison
 *      
 *      1. Attack capturer              - opponent_has_flag AND !capturer
 *      2. Guard flag holder            - team_has_flag AND !capturer
 *      3. Stay away from opponent      - opponent_side AND !team_has_flag
 *      4. Free prisoners               - has_prisoners
 *      5. Go towards base              - capturer
 *      6. Attack opponents             - our_side AND !capturer
 *      7. Go towards opponent's flag   - !capturer
 */
public class CaptureTheFlag extends SteerBasicExample
{
    
    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;
    
    public static final int DEFENSE_SIZE = 1;
    public static final int OFFENSE_SIZE = 24;
    public static final int TEAM_SIZE = DEFENSE_SIZE + OFFENSE_SIZE;
    
    public static final float BASE_RADIUS = 10;
    public static final float PRISON_RADIUS = 4;
    public static final float PLAYER_RADIUS = 8;
    
    public static final float PLAYER_ACC_MAX = 1000;
    public static final float PLAYER_VEL_MAX = 200;
    
    public static final float QUERY_SIZE = 1000;
    public static final int QUERY_MAX = TEAM_SIZE * 2 + 4;
    public static final float FREE_PRISONER_DISTANCE_THRESHOLD = 20;
    public static final float DEFENSE_CIRCLE_RADIUS_MIN = 60;
    public static final float DEFENSE_CIRCLE_RADIUS_MAX = 120;
    
    public static final int GROUP_WALLS = 1;
    public static final int GROUP_TEAM1_CAPTURER = 2;
    public static final int GROUP_TEAM1_NONCAPTURER = 4;
    public static final int GROUP_TEAM2_CAPTURER = 8;
    public static final int GROUP_TEAM2_NONCAPTURER = 16;
    
	public static void main( String[] args )
	{
		Game game = new CaptureTheFlag( WIDTH, HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( WIDTH, HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "CaptureTheFlag" );
	}
	
	/**
	 *           B1 B2
	 *     P1     | |
	 *            | | 
	 * F1         | |          F2
	 *            | |
	 *            | |     P2
	 */
    
	private class Defense {
	    SteerSprite sprite;
	}
	
    private class Offense {
        SteerSprite sprite;
        SteerModifier<Vec2> attackCapturer;
        SteerModifier<Vec2> guardFlagHolder;
        SteerModifier<Vec2> stayAwayFromOpponent;
        SteerModifier<Vec2> freePrisoners;
        SteerModifier<Vec2> towardsBase;
        SteerModifier<Vec2> attack;
        SteerModifier<Vec2> towardsOpponentsFlag;
        SteerSet<Vec2> steerings;
        boolean hasEnemyFlag;
        boolean prisoner;
    }
    
	private class Team {
	    Vec2 base;
	    Vec2 prison;
	    Bounds<Vec2> side;
	    long flagNonCapturer;
	    long flagCapturer;
	    long flagPlayer;
	    Color color;
	    boolean hasEnemyFlag;
	    ArrayList<Defense> defenseList;
	    ArrayList<Offense> offenseList;
	    ArrayList<Offense> prisonList;
	    Offense capturer;
	}

	private SpatialDatabase<Vec2> database;
	private Team team1;
	private Team team2;
	
	public CaptureTheFlag(int w, int h)
	{
		super( w, h );
		
		this.wrapEntities = false;
	}
	
	@Override
	public void start( Scene scene )
	{
        team1 = new Team();
        team1.base = new Vec2( 50, HEIGHT / 2 );
        team1.prison = new Vec2( WIDTH / 4, 50 );
        team1.side = new Bounds<Vec2>( new Vec2(0, 0), new Vec2(WIDTH / 2 - 25, HEIGHT));
        team1.flagCapturer = GROUP_TEAM1_CAPTURER;
        team1.flagNonCapturer = GROUP_TEAM1_NONCAPTURER;
        team1.flagPlayer = team1.flagCapturer | team1.flagNonCapturer;
        team1.color = Color.red;
        
        team2 = new Team();
        team2.base = new Vec2( WIDTH - 50, HEIGHT / 2 );
        team2.prison = new Vec2( WIDTH * 3 / 4, HEIGHT - 50 );
        team2.side = new Bounds<Vec2>( new Vec2(WIDTH / 2 + 25, 0), new Vec2(WIDTH, HEIGHT));
        team2.flagCapturer = GROUP_TEAM2_CAPTURER;
        team2.flagNonCapturer = GROUP_TEAM2_NONCAPTURER;
        team2.flagPlayer = team2.flagCapturer | team2.flagNonCapturer;
        team2.color = Color.blue;

        resetGame();
	}
	
	public void resetGame()
	{
        resetDatabase();
        resetTeam( team1 );
        resetTeam( team2 );
        initializeTeam( team1, team2 );
        initializeTeam( team2, team1 );
	}
	
	public void resetDatabase()
	{
	    sprites = new EntityList<SteerSprite>();
	    
        database = new SpatialArray<Vec2>( QUERY_MAX );
        
        Plane<Vec2> sideL = new Plane<Vec2>( new Vec2(0, HEIGHT / 2), Vec2.RIGHT );
        Plane<Vec2> sideR = new Plane<Vec2>( new Vec2(WIDTH, HEIGHT / 2), Vec2.LEFT );
        Plane<Vec2> sideT = new Plane<Vec2>( new Vec2(WIDTH / 2, 0), Vec2.TOP );
        Plane<Vec2> sideB = new Plane<Vec2>( new Vec2(WIDTH / 2, HEIGHT), Vec2.BOTTOM );
        
        database.add( new SpatialEntityObstacle<Vec2>( sideL, new Vec2(), GROUP_WALLS, 0, true ) ); // left side
        database.add( new SpatialEntityObstacle<Vec2>( sideR, new Vec2(), GROUP_WALLS, 0, true ) ); // right side
        database.add( new SpatialEntityObstacle<Vec2>( sideT, new Vec2(), GROUP_WALLS, 0, true ) ); // top side
        database.add( new SpatialEntityObstacle<Vec2>( sideB, new Vec2(), GROUP_WALLS, 0, true ) ); // bottom side
	}
	
	public void resetTeam( Team team )
	{
	    team.hasEnemyFlag = false;
	    team.defenseList = new ArrayList<Defense>();
        team.offenseList = new ArrayList<Offense>();
        team.prisonList = new ArrayList<Offense>();
	}
	
	public void randomlyPlace( SteerSprite sprite, Team my )
	{
        sprite.position.x = SteerMath.randomFloat( my.side.min.x + PLAYER_RADIUS, my.side.max.x - PLAYER_RADIUS );
        sprite.position.y = SteerMath.randomFloat( my.side.min.y + PLAYER_RADIUS, my.side.max.y - PLAYER_RADIUS );
	}
	
	public void initializeTeam( Team my, Team their )
	{
	    FilterView<Vec2> filter = FilterView.fromDegrees( 270, FieldOfView.HALF );
	    
        Target<Vec2> targetCapturer         = new TargetClosest<Vec2>( database, null, QUERY_SIZE, my.flagCapturer, QUERY_MAX );
        Target<Vec2> targetOpponentCapturer = new TargetClosest<Vec2>( database, filter, QUERY_SIZE, their.flagCapturer, QUERY_MAX );
	    Target<Vec2> targetWeakest          = new TargetWeakest<Vec2>( database, filter, 80, 120, true, QUERY_MAX, their.flagPlayer, Vec2.FACTORY );
	    Target<Vec2> targetWeakestDefense   = new TargetWeakest<Vec2>( database, null, 0, 120, true, QUERY_MAX, their.flagPlayer, Vec2.FACTORY );
	    
	    Steer<Vec2> steerCapturerGuard      = new SteerSeparation<Vec2>( database, 100, my.flagCapturer, QUERY_MAX, null, Vec2.FACTORY);
	    Steer<Vec2> steerCapturer           = new SteerTo<Vec2>( targetCapturer );
        
	    Steer<Vec2> attackCapturer          = new SteerTo<Vec2>( targetOpponentCapturer );
	    Steer<Vec2> guardCapturer           = new SteerSet<Vec2>( PLAYER_ACC_MAX * 2, steerCapturerGuard, steerCapturer );
	    Steer<Vec2> stayAwayFromOpponent    = new SteerSeparation<Vec2>( database, 50, their.flagPlayer, QUERY_MAX, filter, Vec2.FACTORY );
	    Steer<Vec2> freePrisoners           = new SteerTo<Vec2>( their.prison );
	    Steer<Vec2> towardsBase             = new SteerTo<Vec2>( my.base );
	    Steer<Vec2> attack                  = new SteerTo<Vec2>( targetWeakest );
	    Steer<Vec2> towardsOpponentsFlag    = new SteerTo<Vec2>( their.base );
	    
	    Steer<Vec2> separation              = new SteerSeparation<Vec2>( database, PLAYER_RADIUS, my.flagPlayer, QUERY_MAX, filter, Vec2.FACTORY );
	    Steer<Vec2> stayInside              = new SteerAvoidObstacles<Vec2>( database, 40, 20, GROUP_WALLS, 4, Vec2.FACTORY );
	    
	    for (int i = 0; i < OFFENSE_SIZE; i++)
	    {
	        Offense off = new Offense();

            off.hasEnemyFlag = false;
            off.prisoner = false;
            
	        off.attackCapturer         = new SteerModifier<Vec2>( attackCapturer, 1.0f );
	        off.guardFlagHolder        = new SteerModifier<Vec2>( guardCapturer, 1.0f );
	        off.stayAwayFromOpponent   = new SteerModifier<Vec2>( stayAwayFromOpponent, 1.0f );
	        off.freePrisoners          = new SteerModifier<Vec2>( freePrisoners, 1.0f );
	        off.towardsBase            = new SteerModifier<Vec2>( towardsBase, 1.0f );
	        off.attack                 = new SteerModifier<Vec2>( attack, 1.0f );
	        off.towardsOpponentsFlag   = new SteerModifier<Vec2>( towardsOpponentsFlag, 1.0f );
	        
	        off.steerings = new SteerSet<Vec2>( PLAYER_ACC_MAX - 1, 
	            stayInside,
	            separation,   
	            off.attackCapturer,
	            off.guardFlagHolder,
	            off.stayAwayFromOpponent,
	            off.freePrisoners,
	            off.towardsBase,
	            off.attack,
	            off.towardsOpponentsFlag
	        );
	        
	        off.sprite = new SteerSprite( my.color, PLAYER_RADIUS, PLAYER_VEL_MAX, PLAYER_ACC_MAX, off.steerings );
	        off.sprite.groups = my.flagNonCapturer;
	        off.sprite.collisionGroups = their.flagPlayer;
	        off.sprite.drawWrapped = false;
	        randomlyPlace( off.sprite, my );
	        
	        my.offenseList.add( off );
	        
	        sprites.add( off.sprite );
	        database.add( off.sprite );
	    }
	    
	    // 1. Stay on our side
	    // 2. Attack opponent with our flag
	    // 3. Stay out of the way of our team capturing the flag
	    // 4. Stay within X of the base
	    // 5. Attack weakest enemies
	    // 6. Stop
	    for (int i = 0; i < DEFENSE_SIZE; i++)
	    {
	        Defense def = new Defense();
	        
	        def.sprite = new SteerSprite( my.color, PLAYER_RADIUS, PLAYER_VEL_MAX / 2, PLAYER_ACC_MAX / 3, 
	            new SteerSet<Vec2>( PLAYER_ACC_MAX / 3 - 1,
	                new SteerContainment<Vec2>( my.side, 30 ),
	                new SteerTo<Vec2>( targetOpponentCapturer ),
	                new SteerSeparation<Vec2>( database, 100, my.flagCapturer, QUERY_MAX, null, Vec2.FACTORY ),
	                new SteerTo<Vec2>( new TargetLocal<Vec2>( my.base, DEFENSE_CIRCLE_RADIUS_MIN, Float.MAX_VALUE ) ),
	                new SteerTo<Vec2>( targetWeakestDefense ),
	                new SteerDrive<Vec2>( 0, 0, 300 )
	            )
	        );
	        def.sprite.groups = my.flagNonCapturer;
	        def.sprite.collisionGroups = their.flagPlayer;
	        def.sprite.drawWrapped = false;
	        randomlyPlace( def.sprite, my );
	        
	        my.defenseList.add( def );
	        
	        sprites.add( def.sprite );
	        database.add( def.sprite );
	    }
	}
	
	public void updateOffense( Team ourTeam, Team enemy )
	{
	    /*
         *      ~ team_has_flag = if your team has the flag
         *      ~ opponent_has_flag = 
         *      ~ capturer = if you have the flag
         *      ~ our_side = if you're on your side
         *      ~ opponent_side = if you're on your opponent side
         *      ~ has_prisoners = if you have team mates in the opponents prison AND you're within X units of opponents prison
         *      
         *      1. Attack capturer              - opponent_has_flag AND !capturer
         *      2. Guard flag holder            - team_has_flag AND !capturer
         *      3. Stay away from opponent      - opponent_side AND !team_has_flag
         *      4. Free prisoners               - has_prisoners
         *      5. Go towards base              - capturer
         *      6. Attack opponents             - our_side AND !capturer
         *      7. Go towards opponent's flag   - !capturer
         */
	    
        for ( Offense unit : ourTeam.offenseList )
        {
            if (unit.prisoner)
            {
                continue;
            }
            
            final Vec2 opos = unit.sprite.position;
            final float oradius = unit.sprite.radius;
            
            boolean theirSide = enemy.side.isIntersecting( opos, oradius );
            boolean ourSide = ourTeam.side.isIntersecting( opos, oradius );
            
            SearchCallbackArray<Vec2> callback = new SearchCallbackArray<Vec2>( 1 );
            
            // I've tagged the enemy capturer
            if ( !unit.hasEnemyFlag && enemy.capturer != null && database.intersects( opos, oradius, 1, enemy.flagCapturer, callback ) > 0 )
            {
                stop( unit.sprite );
                tag( enemy.capturer, enemy, ourTeam, ourTeam.side.isIntersecting( enemy.capturer.sprite.position, enemy.capturer.sprite.radius ) );
            }
            // I'm on the enemy side
            else if ( theirSide )
            {
                // I was tagged by the enemy team
                if ( database.intersects( opos, oradius, 1, enemy.flagPlayer, callback ) > 0 )
                {
                    stop( callback.entity[0] );
                    tag( unit, ourTeam, enemy, true );
                    
                    continue;
                }
                // I have grabbed their flag
                else if ( enemy.base.distance( opos ) < BASE_RADIUS  && !unit.hasEnemyFlag && !ourTeam.hasEnemyFlag)
                {
                    grabFlag( unit, ourTeam );
                }
                // I have freed the prisoners
                else if ( enemy.prison.distance( opos ) < PRISON_RADIUS )
                {
                    for ( Offense bretheren : enemy.prisonList )
                    {
                        freePrisoner( bretheren, ourTeam );
                    }
                    
                    enemy.prisonList.clear();
                }
            }
            // I'm on my side
            else if ( ourSide )
            {
                // I have captured their flag
                if ( unit.hasEnemyFlag && !enemy.hasEnemyFlag && opos.distance( ourTeam.base ) < BASE_RADIUS )
                {
                    resetGame();
                    
                    return;
                }
            }
            
            unit.attackCapturer.enabled          = enemy.hasEnemyFlag && !unit.hasEnemyFlag;
            unit.guardFlagHolder.enabled         = ourTeam.hasEnemyFlag && !unit.hasEnemyFlag;
            unit.stayAwayFromOpponent.enabled    = unit.hasEnemyFlag || (theirSide && !ourTeam.hasEnemyFlag);
            unit.freePrisoners.enabled           = enemy.prisonList.size() > 0 && opos.distance( enemy.prison ) < FREE_PRISONER_DISTANCE_THRESHOLD;
            unit.towardsBase.enabled             = unit.hasEnemyFlag;
            unit.attack.enabled                  = ourSide && !unit.hasEnemyFlag;
            unit.towardsOpponentsFlag.enabled    = !unit.hasEnemyFlag && !ourTeam.hasEnemyFlag;
        }
	}
	
	private void stop( SpatialEntity<Vec2> entity )
	{
	    if (entity instanceof SteerSubject)
	    {
	        ((SteerSubject<Vec2>)entity).getVelocity().clear();
	    }
	}
	
	private void tag( Offense player, Team myTeam, Team enemy, boolean imprison )
	{
	    if (player.hasEnemyFlag)
        {
	        player.hasEnemyFlag = false;
	        player.sprite.groups = myTeam.flagNonCapturer;
	        myTeam.hasEnemyFlag = false;
	        myTeam.capturer = null;
        }
        
	    if (imprison)
	    {
	        player.prisoner = true;
	        player.sprite.groups = 0;
	        player.sprite.expire();
	        enemy.prisonList.add( player );   
	    }
	}
	
	private void grabFlag( Offense player, Team myTeam )
	{
	    player.hasEnemyFlag = true;
	    player.sprite.groups = myTeam.flagCapturer;
	    myTeam.hasEnemyFlag = true;
	    myTeam.capturer = player;
	}
	
	private void freePrisoner( Offense prisoner, Team myTeam )
	{
	    prisoner.prisoner = false;
	    prisoner.sprite.reset();
	    prisoner.sprite.groups = myTeam.flagNonCapturer;
	    
	    if (!sprites.contains( prisoner.sprite ))
	    {
	        sprites.add( prisoner.sprite );    
	        randomlyPlace( prisoner.sprite, myTeam );
	    }
	}
	
    @Override
    public void update( GameState state, Scene scene )
    {
        updateOffense( team1, team2 );
        updateOffense( team2, team1 );
        
        super.update( state, scene );
    }
    
	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
	    drawTeam( gr, team1, team2 );
	    drawTeam( gr, team2, team1 );
	    
		super.draw( state, gr, scene );
	}
	
    public void drawTeam( Graphics2D gr, Team team, Team enemy )
    {
        drawCircle( gr, team.color, team.base, BASE_RADIUS, false );
        drawCircle( gr, team.color, team.prison, PRISON_RADIUS, false );
        drawBounds( gr, team.color, new Bound2( team.side.min.x, team.side.min.y, team.side.max.x, team.side.max.y ), false );

        Vec2 flag = enemy.hasEnemyFlag ? enemy.capturer.sprite.position : team.base;

        fillCircle( gr, team.color, flag, PLAYER_RADIUS * 0.75f, false );

        String prisonSize = String.valueOf( team.prisonList.size() );
        float prisonSizeWidth = (float)gr.getFontMetrics().getStringBounds( prisonSize, gr ).getWidth();

        gr.drawString( prisonSize, team.prison.x - prisonSizeWidth * 0.5f, team.prison.y - 5 );

        for ( Defense def : team.defenseList )
        {
            fillCircle( gr, new Color( team.color.getRed(), team.color.getGreen(), team.color.getBlue(), 128 ), def.sprite.position, def.sprite.radius, false );
        }
    }
	
}
