package org.magnos.steer.behavior;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.FieldOfView;
import org.magnos.steer.Steer;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerModifier;
import org.magnos.steer.SteerSet;
import org.magnos.steer.Vector;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.array.SpatialArray;
import org.magnos.steer.target.TargetLocal;
import org.magnos.steer.test.SteerSprite;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class SteerControllerImmediateExample extends SteerBasicExample
{
	
	public static void main( String[] args )
	{
		Game game = new SteerControllerImmediateExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerControllerImmediateExample" );
	}
	
	public Steer steerings;
	public float queryAlignment = 80f;
	public float queryCohesion = 140f;
	public float querySeparation = 40f;
	public float queryUser = 80f;
	public SpatialDatabase database;
	
	public SteerControllerImmediateExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		database = new SpatialArray( 32 );
		
		SteerSprite predator = newSprite( Color.red, 15, 250, 1000, new SteerSet(
			new SteerWander( 0, 100, 150, 80 )
		));
		predator.controller.immediate = true;
//		predator.controller.constraint = new ConstraintTurning( 0.1f );
		
		steerings = new SteerSet(
			new SteerAway( new TargetLocal( predator, 300 ) ),
			new SteerModifier( new SteerSeparation( database, querySeparation ), 3.0f ),
			new SteerCohesion( database, queryCohesion ),
			new SteerModifier( new SteerAlignment( database, queryAlignment, SpatialDatabase.ALL_GROUPS, SteerAlignment.DEFAULT_MAX_RESULTS, 1.5f, FieldOfView.FULL ), 1.0f ),
			new SteerModifier( new SteerWander( 0, 80, 80, 60 ), 0.4f )
		);

		for (int i = 0; i < 16; i++) 
		{
			SteerSprite lamb = newSprite( Color.green, 15, 300, 1000, steerings.isShared() ? steerings : steerings.clone() );
			lamb.position.set( SteerMath.randomFloat( width ), SteerMath.randomFloat( height ) );
			lamb.controller.immediate = true;
//			lamb.controller.constraint = new ConstraintTurning( 0.1f );
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
			
			database.intersects( mouse, queryUser, 16, SpatialDatabase.ALL_GROUPS, new SearchCallback() {
				public boolean onFound( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups ) {
					drawCircle( gr, Color.blue, entity.getPosition(), 8, false );
					drawLine( gr, Color.white, entity.getPosition(), queryOffset, false );
					return true;
				}
			});
		}
	}

}
