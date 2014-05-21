
package org.magnos.steer.behavior;

import java.awt.Color;
import java.awt.event.KeyEvent;

import org.magnos.steer.behavior.SteerDrive2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.input.GameInput;


public class SteerDriveExample extends SteerBasicExample
{

	public static void main( String[] args )
	{
		Game game = new SteerDriveExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerDriveExample" );
	}

	private SteerDrive2 drive;

	public SteerDriveExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		newSprite( Color.blue, 15, 300, 1000,  
			drive = new SteerDrive2( 600, 600, 1000, 100, true )
		);
	}

	@Override
	public void input( GameInput input )
	{
		super.input( input );
		
		drive.thrusting = input.keyDown[ KeyEvent.VK_UP ];
		drive.braking = input.keyDown[ KeyEvent.VK_DOWN ];
		drive.turnRight = input.keyDown[ KeyEvent.VK_RIGHT ];
		drive.turnLeft = input.keyDown[ KeyEvent.VK_LEFT ];
	}
	
}
