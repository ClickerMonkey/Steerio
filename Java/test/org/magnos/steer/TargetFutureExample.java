
package org.magnos.steer;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.behavior.SteerDrive;
import org.magnos.steer.behavior.SteerTo;
import org.magnos.steer.behavior.SteerWander;
import org.magnos.steer.target.TargetFuture;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class TargetFutureExample extends SteerBasicExample
{

	public static void main( String[] args )
	{
		Game game = new TargetFutureExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "TargetFutureExample" );
	}

	private TargetFuture future;
	
	public TargetFutureExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		SteerSprite sprite = newSprite( Color.blue, 15, 300, 1000, 
			new SteerWander( 0, 100, 150, 80 ) 
		);
		
		SteerSprite chaser = newSprite( Color.orange, 15, 280, 1000, new SteerSet( 1000,
			new SteerTo( future = new TargetFuture( sprite ) ),
			new SteerDrive( 0, 0, 0, 100 )
		));
		chaser.position.set( 50, 50 );
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );

		if (drawCircles)
		{
			drawCircle( gr, Color.orange, future.future, 8, false );
		}
	}

}
