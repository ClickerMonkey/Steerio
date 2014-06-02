
package org.magnos.steer.constraint;

import java.awt.Color;
import java.awt.event.KeyEvent;

import org.magnos.steer.behavior.SteerBasicExample;
import org.magnos.steer.behavior.SteerDrive;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.input.GameInput;


public class ConstraintZeroVelocityThresholdExample extends SteerBasicExample
{

	public static void main( String[] args )
	{
		Game game = new ConstraintZeroVelocityThresholdExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "ContraintZeroVelocityThresholdExample" );
	}

	private SteerDrive<Vec2> drive;

	public ConstraintZeroVelocityThresholdExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		SteerSprite sprite = newSprite( Color.blue, 15, 300, 1000,  
            drive = new SteerDrive<Vec2>( 600, 600, 100, true, new Vec2(0, 1000), new Vec2(0, -1000) )
		);
		
		sprite.controller.constraint = new ConstraintZeroVelocityThreshold<Vec2>( 50.0f );
	}

	@Override
	public void input( GameInput input )
	{
		super.input( input );
		
		drive.thrusting = input.keyDown[ KeyEvent.VK_UP ];
		drive.braking = input.keyDown[ KeyEvent.VK_DOWN ];
		drive.turn[0] = input.keyDown[ KeyEvent.VK_RIGHT ];
		drive.turn[1] = input.keyDown[ KeyEvent.VK_LEFT ];
	}
	
}
