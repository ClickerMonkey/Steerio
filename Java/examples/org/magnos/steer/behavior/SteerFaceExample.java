
package org.magnos.steer.behavior;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.constraint.ConstraintTurning;
import org.magnos.steer.target.TargetLocal;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class SteerFaceExample extends SteerBasicExample
{
	
	public static void main( String[] args )
	{
		Game game = new SteerFaceExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerFaceExample" );
	}

	private SteerSprite sprite;
	private TargetLocal<Vec2> targetLocal;

	public SteerFaceExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		sprite = newSprite( Color.blue, 15, 300, 
			new SteerFace<Vec2>( 1000, targetLocal = new TargetLocal<Vec2>( mouse, 200 ), false )
		);
		sprite.controller.constraint = new ConstraintTurning<Vec2>( 1.5f );
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );

		if (drawCircles)
		{
			drawCircle( gr, Color.gray, sprite.position, targetLocal.maximum, false);	
		}
	}

}
