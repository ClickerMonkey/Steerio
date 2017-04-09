
package org.magnos.steer.behavior;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.SteerSet;
import org.magnos.steer.target.TargetFuture;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class SteerPursuitExample extends SteerBasicExample
{

	public static void main( String[] args )
	{
		Game game = new SteerPursuitExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerPursuitExample" );
	}
	
	private SteerSprite predator;
	private TargetFuture<Vec2> future;
	private SteerSprite sprite;

	public SteerPursuitExample( int w, int h )
	{
		super( w, h );
	}

	@Override
	public void start( Scene scene )
	{
		sprite = newSprite( Color.blue, 15, 300, new SteerWander2( 1000, 0, 100, 150, 80 ) );

		predator = newSprite( Color.orange, 15, 200, new SteerSet<Vec2>( 1000,
			new SteerTo<Vec2>( 1000, future = new TargetFuture<Vec2>( sprite, Vec2.FACTORY ) ),
			new SteerDrive<Vec2>( 1000, 0, 0, 100 )
		));
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );

		if (drawCircles)
		{
			drawCircle( gr, Color.red, future.getTarget( predator ).getPosition(), 8, true );	
		}
	}

}
