package org.magnos.steer.behavior;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerModifier;
import org.magnos.steer.SteerSet;
import org.magnos.steer.filter.FilterView;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.array.SpatialArray;
import org.magnos.steer.target.TargetLocal;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.util.FieldOfView;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class SteerFlockingExample extends SteerBasicExample
{
	
	public static void main( String[] args )
	{
		Game game = new SteerFlockingExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerFlockingExample" );
	}
	
	public Steer<Vec2> steerings;
	public float queryAlignment = 80f;
	public float queryCohesion = 140f;
	public float querySeparation = 40f;
	public float queryUser = 80f;
	public SpatialDatabase<Vec2> database;
	
	public SteerFlockingExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		database = new SpatialArray<Vec2>( 32 );
		
		SteerSprite predator = newSprite( Color.red, 15, 250, new SteerSet<Vec2>(
			new SteerWander2( 1000, 0, 100, 150, 80 )
		));
		
		steerings = new SteerSet<Vec2>(
			new SteerAway<Vec2>( 1000, new TargetLocal<Vec2>( predator, 400 ) ),
			new SteerModifier<Vec2>( 1000, new SteerSeparation<Vec2>( 1000, database, querySeparation, Vec2.FACTORY ), 3.0f ),
			new SteerCohesion<Vec2>( 1000, database, queryCohesion, Vec2.FACTORY ),
			new SteerModifier<Vec2>( 1000, new SteerAlignment<Vec2>( 1000, database, queryAlignment, -1, 16, FilterView.fromDegrees( 270, FieldOfView.HALF, Vec2.class ) ), 1.0f ),
			new SteerModifier<Vec2>( 1000, new SteerWander2( 1000, 0, 80, 80, 60 ), 0.4f )
		);

		for (int i = 0; i < 16; i++) 
		{
			SteerSprite lamb = newSprite( Color.green, 15, 300, steerings.isShared() ? steerings : steerings.clone() );
			lamb.position.set( SteerMath.randomFloat( width ), SteerMath.randomFloat( height ) );
			database.add( lamb );
		}
	}

	@Override
	public void draw( GameState state, final Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );
		
		if (drawCircles)
		{
			drawCircle( gr, Color.yellow, mouse, queryUser, false );
			
			database.intersects( mouse, queryUser, 16, SpatialDatabase.ALL_GROUPS, new SearchCallback<Vec2>() {
				public boolean onFound( SpatialEntity<Vec2> entity, float overlap, int index, Vec2 queryOffset, float queryRadius, int queryMax, long queryGroups ) {
					drawCircle( gr, Color.blue, entity.getPosition(), 8, false );
					drawLine( gr, Color.white, entity.getPosition(), queryOffset, false );
					return true;
				}
			});
		}
	}

}
