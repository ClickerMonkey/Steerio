
package org.magnos.steer;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.behavior.SteerTo;
import org.magnos.steer.behavior.SteerWander;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.array.SpatialArray;
import org.magnos.steer.target.TargetClosest;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class TargetClosestExample extends SteerBasicExample
{
	
	public static void main( String[] args )
	{
		Game game = new TargetClosestExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "TargetClosestExample" );
	}

	private TargetClosest closest;
	private SpatialDatabase database;

	public TargetClosestExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		database = new SpatialArray( 32 );
		
		newSprite( Color.red, 15, 190, 500, new SteerTo( 
			closest = new TargetClosest( database, 400, 1L )
		));

		for (int i = 0; i < 16; i++)
		{
			SteerSprite lamb = newSprite( Color.white, 10, 200, 1000, new SteerWander( 0, 100, 150, 80 ) );
			lamb.position.set( SteerMath.randomFloat( width ), SteerMath.randomFloat( height ) );
			database.add( lamb );
		}
	}


	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );

		Vector a = new Vector(), b = new Vector();
		Vector v = closest.closest[0].getPosition();
		
		drawCircle( gr, Color.red, v, 20, false );
		drawLine( gr, Color.red, a.set( v.x, v.y - 20 ), b.set( v.x, v.y + 20), false );
		drawLine( gr, Color.red, a.set( v.x - 20, v.y ), b.set( v.x + 20, v.y), false );
	}
	
}
