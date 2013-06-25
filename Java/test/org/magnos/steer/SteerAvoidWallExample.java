
package org.magnos.steer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import org.magnos.steer.behavior.SteerAvoidWall;
import org.magnos.steer.behavior.SteerWander;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntityWall;
import org.magnos.steer.spatial.array.SpatialArray;

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

	private List<SpatialEntityWall> walls;
	private SpatialDatabase database;

	public SteerAvoidWallExample( int w, int h )
	{
		super( w, h );
	}

	@Override
	public void start( Scene scene )
	{
		walls = new ArrayList<SpatialEntityWall>();
		walls.add( new SpatialEntityWall( 24, 24, width - 24, 24, GROUP_WALL, GROUP_WALL ) );
		walls.add( new SpatialEntityWall( width - 24, 24, width - 24, height - 24, GROUP_WALL, GROUP_WALL ) );
		walls.add( new SpatialEntityWall( width - 24, height - 24, 24, height - 24, GROUP_WALL, GROUP_WALL ) );
		walls.add( new SpatialEntityWall( 24, height - 24, 24, 24, GROUP_WALL, GROUP_WALL ) );
		
		database = new SpatialArray( 16 );
		
		for (SpatialEntityWall w : walls)
		{
			database.add( w );
		}
		
		newSprite( Color.blue, 15, 300, 1000, new SteerSet( 
			new SteerAvoidWall( database, 20, GROUP_WALL, 4 ),
			new SteerWander( 0, 100, 150, 80 )
		));
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );

		for (SpatialEntityWall w : walls)
		{
			drawLine( gr, Color.white, w.getStart(), w.getEnd(), false );
		}
	}

}
