
package org.magnos.steer.target;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.SteerMath;
import org.magnos.steer.behavior.SteerArrive;
import org.magnos.steer.behavior.SteerBasicExample;
import org.magnos.steer.behavior.SteerWander2;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class TargetInLineExample extends SteerBasicExample
{

	public static void main( String[] args )
	{
		Game game = new TargetInLineExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "TargetInLineExample" );
	}
	
	private TargetInLine<Vec2> interpose;

	public TargetInLineExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		SteerSprite sprite0 = newSprite( Color.white, 15, 300, 1000,
			new SteerWander2( 0, 100, 150, 80 )
		);
		sprite0.position.addi( SteerMath.randomFloat( 100 ) );
		
		SteerSprite sprite1 = newSprite( Color.lightGray, 15, 300, 1000,
			new SteerWander2( 0, 100, 150, 80 )
		);
		sprite1.position.addi( SteerMath.randomFloat( 100 ) );
		
		newSprite( Color.blue, 15, 300, 1000,  
			new SteerArrive<Vec2>( interpose = new TargetInLine<Vec2>( sprite0, sprite1, Vec2.FACTORY ), 100, 0 )
		);
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );

		if (drawCircles)
		{
			drawCircle( gr, Color.orange, interpose.closest, 8, false );
		}
	}

}
