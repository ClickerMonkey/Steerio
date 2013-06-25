
package org.magnos.steer;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.behavior.SteerTo;
import org.magnos.steer.behavior.SteerWander;
import org.magnos.steer.target.TargetOffset;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class TargetOffsetExample extends SteerBasicExample
{
	
	public static void main( String[] args )
	{
		Game game = new TargetOffsetExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "TargetOffsetExample" );
	}

	private TargetOffset targetRelative;
	private TargetOffset targetAbsolute;

	public TargetOffsetExample( int w, int h )
	{
		super( w, h );
	}

	@Override
	public void start( Scene scene )
	{
		SteerSprite sprite = newSprite( Color.blue, 15, 200, 1000, 
			new SteerWander( 0, 100, 150, 80 )
		);

		SteerSprite offsetRelative = newSprite( Color.green, 15, 300, 1000, 
			new SteerTo( targetRelative = new TargetOffset( sprite, new Vector( -50, 0 ), true ) ) 
		);
		offsetRelative.position.set( 50, 50 );

		SteerSprite offsetAbsolute = newSprite( Color.orange, 15, 300, 1000, 
			new SteerTo( targetAbsolute = new TargetOffset( sprite, new Vector( -50, 0 ), false ) ) 
		);
		offsetAbsolute.position.set( 50, 50 );
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );

		if (drawCircles)
		{
			drawCircle( gr, Color.green, targetRelative.actual, 20, false );
			drawCircle( gr, Color.orange, targetAbsolute.actual, 20, false );
		}
	}

}
