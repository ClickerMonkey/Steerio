
package org.magnos.steer.target;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSet;
import org.magnos.steer.behavior.SteerBasicExample;
import org.magnos.steer.behavior.SteerDrive;
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


public class TargetAverageExample extends SteerBasicExample
{
	
	public static void main( String[] args )
	{
		Game game = new TargetAverageExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "TargetAverageExample" );
	}

	private TargetAverage<Vec2> average;
	private SpatialDatabase<Vec2> database;

	public TargetAverageExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		database = new SpatialArray<Vec2>( 32 );
		
		newSprite( Color.red, 15, 190, new SteerSet<Vec2>( 500,
			new SteerTo<Vec2>( 500, average = new TargetAverage<Vec2>( database, null, 100, 200, false, 32, SpatialDatabase.ALL_GROUPS, Vec2.FACTORY ) ),
			new SteerDrive<Vec2>( 500, 0, 0, 100 )
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

		drawCircle( gr, Color.red, average.average, 5, false );
		drawCircle( gr, Color.orange, average.queryPosition, average.queryRadius, false );
	}
	
}
