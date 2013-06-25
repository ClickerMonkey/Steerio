
package org.magnos.steer;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.behavior.SteerAway;
import org.magnos.steer.behavior.SteerDrive;
import org.magnos.steer.behavior.SteerWander;
import org.magnos.steer.target.TargetFuture;
import org.magnos.steer.target.TargetLocal;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class SteerEvadeExample extends SteerBasicExample
{

	public static void main( String[] args )
	{
		Game game = new SteerEvadeExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerEvadeExample" );
	}
	
	private TargetFuture future;
	private TargetLocal local;
	private SteerSprite scared;
	private SteerSprite sprite;
	
	public SteerEvadeExample( int w, int h )
	{
		super( w, h );
	}

	@Override
	public void start( Scene scene )
	{
		sprite = newSprite( Color.blue, 15, 300, 1000, new SteerWander( 0, 100, 150, 80 ) );

		future = new TargetFuture( sprite );
		local = new TargetLocal( future, 400 );
		
		scared = newSprite( Color.orange, 15, 300, 1000, new SteerSet(
			new SteerAway( local ),
			new SteerDrive( 0, 0, 0, 100, true )
		));
	}
	
	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );

		if (drawCircles)
		{
			drawCircle( gr, Color.red, future.future, 10.0f, true );
			drawCircle( gr, Color.yellow, scared.position, local.maximum, false );
		}
	}

}
