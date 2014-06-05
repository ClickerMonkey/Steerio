
package org.magnos.steer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.magnos.steer.behavior.SteerAvoidObstacles;
import org.magnos.steer.behavior.SteerBasicExample;
import org.magnos.steer.behavior.SteerContainment;
import org.magnos.steer.behavior.SteerDrive;
import org.magnos.steer.behavior.SteerSeparation;
import org.magnos.steer.behavior.SteerTo;
import org.magnos.steer.constraint.ConstraintTurning;
import org.magnos.steer.filter.FilterView;
import org.magnos.steer.obstacle.Bounds;
import org.magnos.steer.obstacle.Plane;
import org.magnos.steer.obstacle.Segment;
import org.magnos.steer.obstacle.Sphere;
import org.magnos.steer.spatial.SearchCallbackArray;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialEntityObstacle;
import org.magnos.steer.spatial.array.SpatialArray;
import org.magnos.steer.target.TargetClosest;
import org.magnos.steer.target.TargetInterpose;
import org.magnos.steer.target.TargetLocal;
import org.magnos.steer.target.TargetRelative;
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
import com.gameprogblog.engine.input.GameInput;

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
    
    public static final int DEFENSE_SIZE = 2;
    public static final int OFFENSE_SIZE = 12;
    public static final int TEAM_SIZE = DEFENSE_SIZE + OFFENSE_SIZE;
    public static final int OBSTACLE_COUNT = 10;
    
    public static final float BASE_RADIUS = 10;
    public static final float PRISON_RADIUS = 4;
    public static final float PLAYER_RADIUS = 8;
    
    public static final float PLAYER_ACC_MIN = 800;
    public static final float PLAYER_ACC_MAX = 1000;
    public static final float PLAYER_VEL_MIN = 125;
    public static final float PLAYER_VEL_MAX = 175;
    
    public static final float QUERY_SIZE = 1000;
    public static final int QUERY_MAX = TEAM_SIZE * 2 + 4;
    public static final float FREE_PRISONER_DISTANCE_THRESHOLD_MAX = 200;
    public static final long FREE_PRISONER_INTERVAL = 750;
    public static final float DEFENSE_CIRCLE_RADIUS_MIN = 60;
    public static final float DEFENSE_CIRCLE_RADIUS_MAX = 120;
    
    public static final int GROUP_OBSTACLES = 1;
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
    
	private class Unit {
	    SteerSprite sprite;
	    int tags;
	}
	
	private class Defense extends Unit {
	    
	}
	
    private class Offense extends Unit {
        SteerModifier<Vec2> attackCapturer;
        SteerModifier<Vec2> guardFlagHolder;
        SteerModifier<Vec2> stayAwayFromOpponent;
        SteerModifier<Vec2> freePrisoners;
        SteerModifier<Vec2> towardsBase;
        SteerModifier<Vec2> attack;
        SteerModifier<Vec2> towardsOpponentsFlag;
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
	    Stack<Offense> prisonList;
	    Offense capturer;
	    int wins;
	    long lastFree;
	    TargetRelative<Vec2> targetCapturer;
	}

	private List<Obstacle<Vec2>> obstacles;
	private SpatialDatabase<Vec2> database;
	private Team team1;
	private Team team2;
	private boolean paused;
	
	private Unit player;
	private Steer<Vec2> playerSteerings;
	private Steer<Vec2> playerToMouse;
	
	public CaptureTheFlag(int w, int h)
	{
		super( w, h );
		
		this.wrapEntities = false;
	}
	
	@Override
	public void start( Scene scene )
	{
	    playerToMouse = new SteerTo<Vec2>( mouse );
	    
	    // Orange Team, left side
        team1 = new Team();
        team1.base = new Vec2( 50, HEIGHT / 2 );
        team1.prison = new Vec2( WIDTH / 4, 50 );
        team1.side = Bounds.fromMinMax( new Vec2(0, 0), new Vec2(WIDTH / 2 - 50, HEIGHT));
        team1.flagCapturer = GROUP_TEAM1_CAPTURER;
        team1.flagNonCapturer = GROUP_TEAM1_NONCAPTURER;
        team1.flagPlayer = team1.flagCapturer | team1.flagNonCapturer;
        team1.color = new Color( 255, 69, 0 );
        
        // Blue Team, right side
        team2 = new Team();
        team2.base = new Vec2( WIDTH - 50, HEIGHT / 2 );
        team2.prison = new Vec2( WIDTH * 3 / 4, HEIGHT - 50 );
        team2.side = Bounds.fromMinMax( new Vec2(WIDTH / 2 + 50, 0), new Vec2(WIDTH, HEIGHT));
        team2.flagCapturer = GROUP_TEAM2_CAPTURER;
        team2.flagNonCapturer = GROUP_TEAM2_NONCAPTURER;
        team2.flagPlayer = team2.flagCapturer | team2.flagNonCapturer;
        team2.color = new Color( 69, 69, 255 );

        resetGame();
	}
	
	// Resets entire simulation
	public void resetGame()
	{
	    uncontrol();
        resetDatabase();
        resetTeam( team1 );
        resetTeam( team2 );
        initializeTeam( team1, team2 );
        initializeTeam( team2, team1 );
	}
	
	// Resets spatial database, boundaries, and obstacles.
	public void resetDatabase()
	{
	    sprites = new EntityList<SteerSprite>();
	    
        database = new SpatialArray<Vec2>( QUERY_MAX );
        obstacles = new ArrayList<Obstacle<Vec2>>();
        
        // World Boundaries
        Plane<Vec2> sideL = new Plane<Vec2>( new Vec2(0, HEIGHT / 2), Vec2.RIGHT );
        Plane<Vec2> sideR = new Plane<Vec2>( new Vec2(WIDTH, HEIGHT / 2), Vec2.LEFT );
        Plane<Vec2> sideT = new Plane<Vec2>( new Vec2(WIDTH / 2, 0), Vec2.TOP );
        Plane<Vec2> sideB = new Plane<Vec2>( new Vec2(WIDTH / 2, HEIGHT), Vec2.BOTTOM );
        
        database.add( new SpatialEntityObstacle<Vec2>( sideL, new Vec2(), GROUP_OBSTACLES, 0, true ) ); // left side
        database.add( new SpatialEntityObstacle<Vec2>( sideR, new Vec2(), GROUP_OBSTACLES, 0, true ) ); // right side
        database.add( new SpatialEntityObstacle<Vec2>( sideT, new Vec2(), GROUP_OBSTACLES, 0, true ) ); // top side
        database.add( new SpatialEntityObstacle<Vec2>( sideB, new Vec2(), GROUP_OBSTACLES, 0, true ) ); // bottom side
        
        // Random Obstacles
        while ( obstacles.size() < OBSTACLE_COUNT )
        {
            Obstacle<Vec2> obs = randomObstacle();
            
            if (!isInterfering( obs ))
            {
                database.add( new SpatialEntityObstacle<Vec2>( obs, new Vec2(), GROUP_OBSTACLES, 0, true ) );
                obstacles.add( obs );
            }
        }
	}
	
	// Clears the teams units and flag
	public void resetTeam( Team team )
	{
	    team.hasEnemyFlag = false;
	    team.defenseList = new ArrayList<Defense>();
        team.offenseList = new ArrayList<Offense>();
        team.prisonList = new Stack<Offense>();
	}
	
	// Randomly places the sprite on the map
	public void randomlyPlace( SteerSprite sprite, Team my )
	{
        sprite.position.x = SteerMath.randomFloat( my.side.min.x + PLAYER_RADIUS, my.side.max.x - PLAYER_RADIUS );
        sprite.position.y = SteerMath.randomFloat( my.side.min.y + PLAYER_RADIUS, my.side.max.y - PLAYER_RADIUS );
	}
	
	// Generate a random obstacle
	public Obstacle<Vec2> randomObstacle()
	{
	    switch (SteerMath.randomInt( 2 ))
	    {
	    case 0:
	        Sphere<Vec2> sphere = new Sphere<Vec2>();
	        sphere.radius = SteerMath.randomFloat( 5, 20 );
	        sphere.position = new Vec2( SteerMath.randomFloat( WIDTH ), SteerMath.randomFloat( HEIGHT ) );
	        return sphere;
	    case 1:
	        Vec2 center = new Vec2( SteerMath.randomFloat( WIDTH ), SteerMath.randomFloat( HEIGHT ) );
	        Vec2 size = new Vec2( SteerMath.randomFloat( 4, 10 ), SteerMath.randomFloat( 4, 10 ) );
	        return Bounds.fromCenter( center, size );
	    case 2:
	        Vec2 start = new Vec2( SteerMath.randomFloat( WIDTH ), SteerMath.randomFloat( HEIGHT ) );
	        Vec2 end = new Vec2().angle( SteerMath.randomFloat( SteerMath.PI2 ), SteerMath.randomFloat( 10, 50 ) ).addi( start );
	        return new Segment<Vec2>( start, end, SteerMath.randomFloat( 2f, 5f ) );
	    }
	    return null;
	}
	
	// True if the obstacle might interfere with a base or prison
	public boolean isInterfering( Obstacle<Vec2> obs )
	{
	    return isInterfering( obs, team1.base, BASE_RADIUS * 2 ) ||
	           isInterfering( obs, team2.base, BASE_RADIUS * 2 ) ||
	           isInterfering( obs, team1.prison, 40 ) || 
	           isInterfering( obs, team2.prison, 40 );
	}
	
	// True if the obstacle is intersecting with the given circle
	public boolean isInterfering( Obstacle<Vec2> obs, Vec2 point, float radius )
	{
	    float radiusSum = obs.getRadius() + radius;
	    
	    return obs.getPosition( new Vec2() ).distanceSq( point ) < radiusSum * radiusSum;
	}
	
	// Builds the offense and defense for the given team, against the opponent
	public void initializeTeam( Team my, Team their )
	{
	    FilterView<Vec2> filter = FilterView.fromDegrees( 270, FieldOfView.HALF );
	    
        Target<Vec2> targetCapturer         = new TargetClosest<Vec2>( database, null, QUERY_SIZE, my.flagCapturer, QUERY_MAX );
        Target<Vec2> targetOpponentCapturer = new TargetClosest<Vec2>( database, filter, QUERY_SIZE, their.flagCapturer, QUERY_MAX );
	    Target<Vec2> targetWeakest          = new TargetWeakest<Vec2>( database, filter, 80, 120, true, QUERY_MAX, their.flagPlayer, Vec2.FACTORY );
	    Target<Vec2> targetWeakestDefense   = new TargetWeakest<Vec2>( database, null, 0, 120, true, QUERY_MAX, their.flagPlayer, Vec2.FACTORY );
	    Target<Vec2> targetOpponentDefense  = new TargetClosest<Vec2>( database, null, QUERY_SIZE, their.flagCapturer, QUERY_MAX );
	    
	    my.targetCapturer = new TargetRelative<Vec2>( null, targetWeakestDefense );
	    
	    Steer<Vec2> steerCapturerGuard      = new SteerSeparation<Vec2>( database, 100, my.flagCapturer, QUERY_MAX, null, Vec2.FACTORY);
	    Steer<Vec2> steerCapturer           = new SteerTo<Vec2>( new TargetInterpose<Vec2>( targetCapturer, my.targetCapturer, 0.5f, Vec2.FACTORY ) );
        
	    Steer<Vec2> attackCapturer          = new SteerTo<Vec2>( targetOpponentCapturer );
	    Steer<Vec2> guardCapturer           = new SteerSet<Vec2>( steerCapturerGuard, steerCapturer );
	    Steer<Vec2> stayAwayFromOpponent    = new SteerSeparation<Vec2>( database, 50, their.flagPlayer, QUERY_MAX, filter, Vec2.FACTORY );
	    Steer<Vec2> freePrisoners           = new SteerTo<Vec2>( their.prison );
	    Steer<Vec2> towardsBase             = new SteerTo<Vec2>( my.base );
	    Steer<Vec2> attack                  = new SteerTo<Vec2>( targetWeakest );
	    Steer<Vec2> towardsOpponentsFlag    = new SteerTo<Vec2>( their.base );
	    
	    Steer<Vec2> separation              = new SteerSeparation<Vec2>( database, PLAYER_RADIUS, my.flagPlayer, QUERY_MAX, filter, Vec2.FACTORY );
	    Steer<Vec2> stayInside              = new SteerAvoidObstacles<Vec2>( database, 40, 20, GROUP_OBSTACLES, 4, Vec2.FACTORY );
	    
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
	        
	        float offvelocity      = SteerMath.randomFloat( PLAYER_VEL_MIN, PLAYER_VEL_MAX );
	        float offacceleration  = SteerMath.randomFloat( PLAYER_ACC_MIN, PLAYER_ACC_MAX );
	        
	        off.sprite = new SteerSprite( my.color, PLAYER_RADIUS, 
	            offvelocity, 
	            offacceleration, 
	            new SteerSet<Vec2>( PLAYER_ACC_MAX - 1, 
	                stayInside,
	                separation,
	                off.attackCapturer,
	                off.guardFlagHolder,
	                off.stayAwayFromOpponent,
                    off.freePrisoners,
	                off.towardsBase,
	                off.attack,
	                off.towardsOpponentsFlag
	            )
	        );
	        off.sprite.groups = my.flagNonCapturer;
	        off.sprite.collisionGroups = their.flagPlayer;
	        off.sprite.controller.constraint = new ConstraintTurning<Vec2>( 20.0f );
	        off.sprite.drawWrapped = false;
	        off.sprite.attach( off );
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
	        // Toggle defenders between base and prison
	        Vec2 defend = i % 2 == 0 ? my.base : my.prison;
	        
	        Defense def = new Defense();
	        
	        def.sprite = new SteerSprite( my.color, PLAYER_RADIUS, PLAYER_VEL_MAX / 2, PLAYER_ACC_MAX / 3, 
	            new SteerSet<Vec2>( PLAYER_ACC_MAX / 3 - 1,
	                stayInside,
	                new SteerContainment<Vec2>( my.side, 30 ),
	                new SteerTo<Vec2>( targetOpponentDefense ),
	                new SteerSeparation<Vec2>( database, 100, my.flagCapturer, QUERY_MAX, null, Vec2.FACTORY ),
	                new SteerTo<Vec2>( new TargetLocal<Vec2>( defend, DEFENSE_CIRCLE_RADIUS_MIN, Float.MAX_VALUE ) ),
	                new SteerTo<Vec2>( targetWeakestDefense ),
	                new SteerDrive<Vec2>( 0, 0, 300 )
	            )
	        );
	        def.sprite.groups = my.flagNonCapturer;
	        def.sprite.collisionGroups = their.flagPlayer;
	        def.sprite.drawWrapped = false;
	        def.sprite.attach( def );
	        randomlyPlace( def.sprite, my );
	        
	        my.defenseList.add( def );
	        
	        sprites.add( def.sprite );
	        database.add( def.sprite );
	    }
	}
	
	// Updates the teams offense based on who has the flag, prisoners, and what side they're on.
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
                // Imprison them if they're on our side.
                boolean imprison = ourTeam.side.isIntersecting( enemy.capturer.sprite.position, enemy.capturer.sprite.radius );
                
                tagger( unit.sprite, ourTeam );
                tag( enemy.capturer, enemy, ourTeam, imprison );
            }
            // I'm on the enemy side
            else if ( theirSide )
            {
                // I was tagged by the enemy team
                if ( database.intersects( opos, oradius, 1, enemy.flagPlayer, callback ) > 0 )
                {
                    tagger( callback.entity[0], enemy );
                    tag( unit, ourTeam, enemy, true );
                    
                    continue;
                }
                // I have grabbed their flag
                else if ( enemy.base.distance( opos ) < BASE_RADIUS  && !unit.hasEnemyFlag && !ourTeam.hasEnemyFlag)
                {
                    grabFlag( unit, ourTeam );
                }
                // I have freed the prisoners
                else if ( enemy.prison.distance( opos ) < PRISON_RADIUS && enemy.prisonList.size() > 0 )
                {
                    if ( (System.currentTimeMillis() - ourTeam.lastFree) > FREE_PRISONER_INTERVAL )
                    {
                        freePrisoner( enemy.prisonList.pop(), ourTeam );
                        ourTeam.lastFree = System.currentTimeMillis();
                    }
                }
            }
            // I'm on my side
            else if ( ourSide )
            {
                // I have captured their flag
                if ( unit.hasEnemyFlag && !enemy.hasEnemyFlag && opos.distance( ourTeam.base ) < BASE_RADIUS )
                {
                    ourTeam.wins++;
                    
                    resetGame();
                    
                    return;
                }
            }
            
            // freeing prisoners becomes more attractive as more are imprisoned, up to a maximum distance.
            float prisonerThreshold =  enemy.prisonList.size() * FREE_PRISONER_DISTANCE_THRESHOLD_MAX / OFFENSE_SIZE;
            
            // Enable behaviors based on the simulations current state
            unit.attackCapturer.enabled          = !unit.hasEnemyFlag && enemy.hasEnemyFlag;
            unit.guardFlagHolder.enabled         = !unit.hasEnemyFlag && ourTeam.hasEnemyFlag;
            unit.stayAwayFromOpponent.enabled    =  unit.hasEnemyFlag || theirSide;
            unit.freePrisoners.enabled           = !unit.hasEnemyFlag && opos.distanceSq( enemy.prison ) < prisonerThreshold * prisonerThreshold && enemy.prisonList.size() > 2;
            unit.towardsBase.enabled             =  unit.hasEnemyFlag;
            unit.attack.enabled                  = !unit.hasEnemyFlag && ourSide;
            unit.towardsOpponentsFlag.enabled    = !unit.hasEnemyFlag && !ourTeam.hasEnemyFlag;
        }
	}

	// Stops the tagger and gives them a point.
    private void tagger( SpatialEntity<Vec2> player, Team myTeam )
    {
        Unit unit = player.attachment();
        unit.sprite.velocity.clear();
        unit.tags++;
    }

    // Tags the player and potentially takes a flag from them and imprisons them.
    private void tag( Offense player, Team myTeam, Team enemy, boolean imprison )
    {
        if ( player.hasEnemyFlag )
        {
            player.hasEnemyFlag = false;
            player.sprite.groups = myTeam.flagNonCapturer;
            myTeam.hasEnemyFlag = false;
            myTeam.capturer = null;
        }

        if ( imprison )
        {
            player.prisoner = true;
            player.sprite.groups = 0;
            player.sprite.expire();
            enemy.prisonList.add( player );
        }
    }

    // Grab the enemy flag
    private void grabFlag( Offense player, Team myTeam )
    {
        player.hasEnemyFlag = true;
        player.sprite.groups = myTeam.flagCapturer;
        myTeam.hasEnemyFlag = true;
        myTeam.capturer = player;
        myTeam.targetCapturer.relativeTo = player.sprite;
    }

    // Free the given prisoner
    private void freePrisoner( Offense prisoner, Team myTeam )
    {
        prisoner.prisoner = false;
        prisoner.sprite.reset();
        prisoner.sprite.groups = myTeam.flagNonCapturer;

        if ( !sprites.contains( prisoner.sprite ) )
        {
            sprites.add( prisoner.sprite );
            randomlyPlace( prisoner.sprite, myTeam );
        }
    }

	@Override
	public void input( GameInput input ) 
	{
	    super.input( input );
	    
        if ( input.keyUp[KeyEvent.VK_R] )
        {
            resetGame();
        }
        
        if ( input.keyUp[KeyEvent.VK_P] )
        {
            paused = !paused;
        }
        
        // Take control of a unit (left mouse)
        if ( input.mouseDown[1] )
        {
            SpatialEntity<Vec2>[] nearest = new SpatialEntity[1];
            float[] distance = new float[1];
            int found = database.knn( mouse, 1, team1.flagPlayer | team2.flagPlayer, nearest, distance );

            if ( found > 0 && distance[0] < 0 )
            {
                uncontrol();
                
                player = nearest[0].attachment();
                playerSteerings = player.sprite.controller.force;
                player.sprite.controller.force = playerToMouse;
            }
        }
        // Release control of the unit (right mouse)
        else if ( input.mouseDown[3] && player != null )
        {
            uncontrol();
        }
	}

    // Release control of the unit
	private void uncontrol()
	{
	    if (player != null)
	    {
	        player.sprite.controller.force = playerSteerings;
	        player = null;
	        playerSteerings = null;   
	    }
	}
	
    @Override
    public void update( GameState state, Scene scene )
    {
        if ( !paused )
        {
            if (player instanceof Offense && ((Offense)player).prisoner)
            {
                uncontrol();
            }
            
            updateOffense( team1, team2 );
            updateOffense( team2, team1 );   
            
            database.refresh();
            
            super.update( state, scene );
        }
    }
    
    @Override
    public void draw( GameState state, Graphics2D gr, Scene scene )
    {
        for ( Obstacle<Vec2> obs : obstacles )
        {
            drawObstacle( gr, obs );
        }

        drawTeam( gr, team1, team2 );
        drawTeam( gr, team2, team1 );

        super.draw( state, gr, scene );
        
        if ( player != null )
        {
            fillCircle( gr, Color.white, player.sprite.position, player.sprite.radius, false );
            
            super.draw( state, gr, scene );

            Vec2 L = player.sprite.direction.rotate(+2.35f ).muli( 1000 ).addi( player.sprite.position );
            Vec2 R = player.sprite.direction.rotate(-2.35f ).muli( 1000 ).addi( player.sprite.position );
            
            Path2D.Float blind = new Path2D.Float();
            blind.moveTo( player.sprite.position.x, player.sprite.position.y );
            blind.lineTo( L.x, L.y );
            blind.lineTo( R.x, R.y );
            blind.closePath();
            gr.setPaint( Color.black );
            gr.fill( blind );
        }
        else
        {
            super.draw( state, gr, scene );
        }
    }
	
    // Draws all team markers (base, prison, counters, flag, defenders)
    public void drawTeam( Graphics2D gr, Team team, Team enemy )
    {
        drawCircle( gr, team.color, team.base, BASE_RADIUS, false );
        drawCircle( gr, team.color, team.prison, PRISON_RADIUS, false );
        drawCircle( gr, team.color, team.prison, PRISON_RADIUS * 1.5f, false );
        drawCircle( gr, team.color, team.prison, PRISON_RADIUS * 0.5f, false );
        drawBounds( gr, team.color, new Bound2( team.side.min.x, team.side.min.y, team.side.max.x, team.side.max.y ), false );

        Vec2 flag = enemy.hasEnemyFlag ? enemy.capturer.sprite.position : team.base;

        fillCircle( gr, team.color, flag, PLAYER_RADIUS * 0.75f, false );
        drawLine( gr, team.color, flag, flag.adds( Vec2.BOTTOM, 25 ), false );
        drawBounds( gr, team.color, new Bound2( flag.x, flag.y - 25, flag.x + 15, flag.y - 15 ), false );

        drawText( gr, team.prisonList.size(), team.color, team.prison, 0.5f, 1.5f );
        drawText( gr, team.wins, team.color, team.side.center, 0.5f, 0.5f );

        for ( Defense def : team.defenseList )
        {
            fillCircle( gr, new Color( team.color.getRed(), team.color.getGreen(), team.color.getBlue(), 128 ), def.sprite.position, def.sprite.radius, false );

            if ( def.tags > 0 )
            {
                drawText( gr, def.tags, team.color, def.sprite.position, 0.5f, 1.7f );
            }
        }

        for ( Offense off : team.offenseList )
        {
            if ( !off.prisoner && off.tags > 0 )
            {
                drawText( gr, off.tags, team.color, off.sprite.position, 0.5f, 1.7f );
            }
        }
    }

    // Draws an obstacle.
    public void drawObstacle( Graphics2D gr, Obstacle<Vec2> obs )
    {
        if ( obs instanceof Sphere )
        {
            Sphere<Vec2> sphere = (Sphere<Vec2>)obs;
            drawCircle( gr, Color.white, sphere.position, sphere.radius, false );
        }
        if ( obs instanceof Segment )
        {
            Segment<Vec2> segment = (Segment<Vec2>)obs;
            drawLine( gr, Color.white, segment.start, segment.end, false );
        }
        if ( obs instanceof Bounds )
        {
            Bounds<Vec2> bounds = (Bounds<Vec2>)obs;
            drawBounds( gr, Color.white, new Bound2( bounds.min.x, bounds.min.y, bounds.max.x, bounds.max.y ), false );
        }
    }

}
