
package org.magnos.steer.target;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.behavior.SteerBasicExample;
import org.magnos.steer.behavior.SteerTo;
import org.magnos.steer.behavior.SteerWander2;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.array.SpatialArray;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class TargetWeakestExample extends SteerBasicExample
{
	
	public static void main( String[] args )
	{
		Game game = new TargetWeakestExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "TargetWeakestExample" );
	}

	private SteerSubject<Vec2> subject;
	private TargetWeakest<Vec2> weakest;
	private SpatialDatabase<Vec2> database;

	public TargetWeakestExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		database = new SpatialArray<Vec2>( 32 );
		
		subject = newSprite( Color.red, 15, 190,
			new SteerTo<Vec2>( 500, weakest = new TargetWeakest<Vec2>( database, null, 100, 200, false, 32, SpatialDatabase.ALL_GROUPS, Vec2.FACTORY ) 
		));

		for (int i = 0; i < 16; i++)
		{
			SteerSprite lamb = newSprite( Color.white, 10, 200, new SteerWander2( 1000, 0, 100, 150, 80 ) );
			lamb.position.set( SteerMath.randomFloat( width ), SteerMath.randomFloat( height ) );
			database.add( lamb );
		}
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );

		if ( weakest.weakest != null )
		{
			drawCircle( gr, Color.yellow, weakest.weakest.getPosition(), 5, false );	
		}
		
		Vec2 target = weakest.getTarget( subject );
		
		if ( target != null )
		{
			drawCircle( gr, Color.red, target, 5, false );
		}
		
		drawCircle( gr, Color.orange, weakest.queryPosition, weakest.queryRadius, false );
	}
	
}
