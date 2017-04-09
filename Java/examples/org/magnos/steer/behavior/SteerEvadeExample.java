
package org.magnos.steer.behavior;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.SteerSet;
import org.magnos.steer.filter.FilterInFront;
import org.magnos.steer.obstacle.Bounds;
import org.magnos.steer.target.TargetFiltered;
import org.magnos.steer.target.TargetFuture;
import org.magnos.steer.target.TargetLocal;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.vec.Vec2;

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
	
	private TargetFuture<Vec2> future;
	private TargetLocal<Vec2> local;
	private SteerSprite scared;
	private SteerSprite sprite;
	
	public SteerEvadeExample( int w, int h )
	{
		super( w, h );
	}

	@Override
	public void start( Scene scene )
	{	    
		sprite = newSprite( Color.blue, 15, 200, new SteerWander2( 1000, 0, 100, 150, 80 ) );
		
		future = new TargetFuture<Vec2>( sprite, Vec2.FACTORY );
		local = new TargetLocal<Vec2>( future, 200 );
		
		scared = newSprite( Color.orange, 15, 300, new SteerSet<Vec2>( 1000,
		    new SteerContainment<Vec2>( 1000, Bounds.fromMinMax( Vec2.ZERO, new Vec2( width, height ) ), 20 ),
		    new SteerAway<Vec2>( 1000, new TargetFiltered<Vec2>( local, FilterInFront.FRONT ) ),
			new SteerDrive<Vec2>( 1000, 0, 0, 100 )
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
