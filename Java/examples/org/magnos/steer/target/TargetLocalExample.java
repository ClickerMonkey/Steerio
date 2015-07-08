package org.magnos.steer.target;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.SteerSet;
import org.magnos.steer.behavior.SteerBasicExample;
import org.magnos.steer.behavior.SteerDrive;
import org.magnos.steer.behavior.SteerTo;
import org.magnos.steer.behavior.SteerWander2;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;

public class TargetLocalExample extends SteerBasicExample
{
	
	public static void main( String[] args )
	{
		Game game = new TargetLocalExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "TargetLocalExample" );
	}
	
	private SteerSprite chaser;
	private TargetLocal<Vec2> local;

	public TargetLocalExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		SteerSprite sprite = newSprite( Color.blue, 15, 300, 
			new SteerWander2( 1000, 0, 100, 150, 80 )
		);
		
		chaser = newSprite( Color.orange, 15, 300, new SteerSet<Vec2>( 1000,
			new SteerTo<Vec2>( 1000, local = new TargetLocal<Vec2>( sprite, 100 ) ),
			new SteerDrive<Vec2>( 1000, 0, 0, 100 )
		));
		chaser.position.set( 100, 100 );
	}
	
	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );

		if (drawCircles)
		{
			drawCircle( gr, Color.orange, chaser.position, local.maximum, false );
		}
	}
	
}
