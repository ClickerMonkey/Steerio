
package org.magnos.steer.behavior;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.Vector;
import org.magnos.steer.behavior.SteerWander;
import org.magnos.steer.test.SteerSprite;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class SteerWanderExample extends SteerBasicExample
{

	public static void main( String[] args )
	{
		Game game = new SteerWanderExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerWanderExample" );
	}
	
	private SteerSprite sprite;
	private SteerWander wander;

	public SteerWanderExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		wander = new SteerWander( 0, 100, 150, 80 );
		sprite = newSprite( Color.blue, 15, 300, 1000, wander );
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );
		
		if (drawCircles)
		{
			Vector center = new Vector();
			center.set( sprite.position );
			center.addsi( sprite.direction, wander.distance );

			Vector offset = new Vector();
			offset.angle( wander.theta, wander.radius );
			offset.addi( center );

			drawCircle( gr, Color.red, new Vector( center ), wander.radius, false );
			drawLine( gr, Color.yellow, center, offset, false );
			drawLine( gr, Color.gray, sprite.position, offset, false );
			drawLine( gr, Color.orange, center, sprite.position, false );
		}
	}

}
