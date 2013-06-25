
package org.magnos.steer;

import java.awt.Color;

import org.magnos.steer.behavior.SteerAway;
import org.magnos.steer.behavior.SteerDrive;
import org.magnos.steer.behavior.SteerWander;
import org.magnos.steer.target.TargetFacing;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.Scene;


public class TargetFacingExample extends SteerBasicExample
{
	
	public static void main( String[] args )
	{
		Game game = new TargetFacingExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "TargetFacingExample" );
	}

	public TargetFacingExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		SteerSprite sprite = newSprite( Color.blue, 15, 300, 1000,  
			new SteerWander( 0, 100, 150, 80 )
		);

		newSprite( Color.orange, 15, 300, 1000, new SteerSet(
			new SteerAway( new TargetFacing( sprite, true ) ),
			new SteerDrive( 0, 0, 0, 100 )
		));

		newSprite( Color.green, 15, 300, 1000, new SteerSet(
			new SteerAway( new TargetFacing( sprite, false ) ),
			new SteerDrive( 0, 0, 0, 100 )
		));
	}

}
