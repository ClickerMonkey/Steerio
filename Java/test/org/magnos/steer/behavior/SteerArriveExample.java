
package org.magnos.steer.behavior;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.behavior.SteerArrive;
import org.magnos.steer.test.SteerSprite;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class SteerArriveExample extends SteerBasicExample
{

	public static void main( String[] args )
	{
		Game game = new SteerArriveExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerArriveExample" );
	}
	
	private SteerArrive arrive;
	private SteerSprite sprite;
	
	public SteerArriveExample(int w, int h)
	{
		super( w, h );
	}

	@Override
	public void start( Scene scene )
	{
		sprite = newSprite( Color.blue, 15, 300, 1000, 
			arrive = new SteerArrive( mouse, 100, 0, false )
		);
	}
	
	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );

		if ( drawCircles )
		{
			drawCircle( gr, Color.lightGray, sprite.position, arrive.caution, false );
		}
	}

}
