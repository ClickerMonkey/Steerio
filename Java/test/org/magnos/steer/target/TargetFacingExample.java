
package org.magnos.steer.target;

import java.awt.Color;

import org.magnos.steer.SteerSet;
import org.magnos.steer.behavior.SteerAway;
import org.magnos.steer.behavior.SteerBasicExample;
import org.magnos.steer.behavior.SteerDrive;
import org.magnos.steer.behavior.SteerWander2;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.vec.Vec2;

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
			new SteerWander2( 0, 100, 150, 80 )
		);

		newSprite( Color.orange, 15, 300, 1000, new SteerSet<Vec2>(
			new SteerAway<Vec2>( new TargetFacing<Vec2>( sprite, true ) ),
			new SteerDrive<Vec2>( 0, 0, 100 )
		));

		newSprite( Color.green, 15, 300, 1000, new SteerSet<Vec2>(
			new SteerAway<Vec2>( new TargetFacing<Vec2>( sprite, false ) ),
			new SteerDrive<Vec2>( 0, 0, 100 )
		));
	}

}
