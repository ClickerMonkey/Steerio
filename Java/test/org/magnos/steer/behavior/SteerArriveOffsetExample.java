
package org.magnos.steer.behavior;

import java.awt.Color;

import org.magnos.steer.target.TargetOffset;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.Scene;


public class SteerArriveOffsetExample extends SteerBasicExample
{

	public static void main( String[] args )
	{
		Game game = new SteerArriveOffsetExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerArriveOffsetExample" );
	}

	private Color[] colors = { Color.orange, Color.yellow, Color.green, Color.blue, Color.pink };

	public SteerArriveOffsetExample(int w, int h)
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		SteerSprite sprite = newSprite( Color.blue, 15, 300, 1000,
			new SteerArrive<Vec2>( mouse, 100, 0, false )
		);
		
		for (int i = 0; i < colors.length; i++)
		{
			newSprite( colors[i], 15, 300, 1000,
				new SteerArrive<Vec2>( new TargetOffset<Vec2>( sprite, new Vec2( -60 - i * 60, 0 ), true ), 100, 0 )
			);
		}
	}
	
}
