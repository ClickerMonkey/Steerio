package org.magnos.steer.behavior;

import java.awt.Color;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSet;
import org.magnos.steer.Vector;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.array.SpatialArray;
import org.magnos.steer.test.SteerSprite;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.Scene;


public class SteerAvoidObstaclesExample extends SteerBasicExample
{
	
	public static void main( String[] args )
	{
		Game game = new SteerAvoidObstaclesExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerAvoidObstaclesExample" );
	}
	
	public SpatialDatabase database;
	
	public SteerAvoidObstaclesExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		database = new SpatialArray( 32 );
		
		for (int i = 0; i < 16; i++) 
		{
			SteerSprite lamb = newSprite( Color.green, 15, 200, 2000, new SteerSet(
				new SteerAvoidObstacles( database, 0.8f ),
				new SteerFixed( new Vector( SteerMath.randomFloat( -300, 300 ), SteerMath.randomFloat( -300, 300 ) ) )	
			));
			
			lamb.position.set( SteerMath.randomFloat( width ), SteerMath.randomFloat( height ) );
			
			database.add( lamb );
		}
	}

}
