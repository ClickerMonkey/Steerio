
package org.magnos.steer.behavior;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import org.magnos.steer.SteerSet;
import org.magnos.steer.obstacle.Segment;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntityObstacle;
import org.magnos.steer.spatial.array.SpatialArray;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class SteerAvoidWallExample extends SteerBasicExample
{

	public static long GROUP_WALL = 1L << 0;
	
	public static void main( String[] args )
	{
		Game game = new SteerAvoidWallExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerAvoidExample" );
	}

	private List<Segment<Vec2>> walls;
	private SpatialDatabase<Vec2> database;
	private SteerAvoidObstacles<Vec2> obstacles;
	private SteerSprite sprite;

	public SteerAvoidWallExample( int w, int h )
	{
		super( w, h );
	}

	@Override
	public void start( Scene scene )
	{
		walls = new ArrayList<Segment<Vec2>>();
		walls.add( new Segment<Vec2>( new Vec2( 24, 24 ), new Vec2( width - 24, 24 ) ) );
		walls.add( new Segment<Vec2>( new Vec2( width - 24, 24 ), new Vec2( width - 24, height - 24 ) ) );
		walls.add( new Segment<Vec2>( new Vec2( width - 24, height - 24 ), new Vec2( 24, height - 24 ) ) );
		walls.add( new Segment<Vec2>( new Vec2( 24, height - 24 ), new Vec2( 24, 24 ) ) );
		
		database = new SpatialArray<Vec2>( 16 );
		
		for (Segment<Vec2> w : walls)
		{
			database.add( new SpatialEntityObstacle<Vec2>( w, w.getPosition( new Vec2() ), GROUP_WALL, GROUP_WALL ) );
		}
		
		sprite = newSprite( Color.blue, 15, 300, 1000, new SteerSet<Vec2>( 
			obstacles = new SteerAvoidObstacles<Vec2>( database, 80.0f, 5.0f, Vec2.FACTORY ),
			new SteerWander2( 0, 100, 150, 80 )
		));
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );

		for (Segment<Vec2> w : walls)
		{
			drawLine( gr, Color.white, w.start, w.end, false );
		}
		
		drawLine( gr, Color.blue, sprite.getPosition(), obstacles.lookaheadPoint, false );
	}

}
