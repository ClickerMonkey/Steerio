
package org.magnos.steer;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.behavior.SteerAvoid;
import org.magnos.steer.behavior.SteerPath;
import org.magnos.steer.contraints.ConstraintTurning;
import org.magnos.steer.path.LinearPath;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class SteerPathExample extends SteerBasicExample
{

	public static void main( String[] args )
	{
		Game game = new SteerPathExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerPathExample" );
	}
	
	private LinearPath path = new LinearPath(
		new Vector( 100, 100 ),
		new Vector( 500, 100 ),
		new Vector( 400, 300 ),
		new Vector( 200, 200 ),
		new Vector( 100, 400 ),
		new Vector( 600, 400 )
	);
	
	private SteerPath steerRed;
	private SteerSprite sprite;
	
	public SteerPathExample( int w, int h )
	{
		super( w, h );
	}

	@Override
	public void start( Scene scene )
	{
		steerRed = new SteerPath( path, 0.01f, 0.05f, 15, 40, 60, 1, true, false );
		
		sprite = newSprite( Color.blue, 15, 300, 1000, steerRed );
		sprite.position.set( 100, 110 );

		SteerSprite quicker = newSprite( Color.orange, 15, 300, 10000, new SteerSet(
			new SteerPath( path, 0.01f, 0.025f, 0, 10, 20, 1, true, false ),
			new SteerAvoid( sprite, 100, true )
		));
		quicker.controller.constraint = new ConstraintTurning( 1.8f );
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );
		
		drawPath( gr, Color.darkGray, path.points, false, false );
		
		if (drawCircles)
		{
			drawCircle( gr, Color.red, steerRed.future, 6, true );
			drawCircle( gr, Color.white, steerRed.target, 6, true );
			drawCircle( gr, Color.gray, steerRed.ahead, 6, true );
			drawLine( gr, Color.magenta, steerRed.future, steerRed.target, true );
			
			Vector next = new Vector();

			path.set( next, SteerMath.clamp( steerRed.delta + (steerRed.lookahead * steerRed.direction), 0, 1 ) );
			drawCircle( gr, Color.blue, next, 6, true );

			path.set( next, SteerMath.clamp( steerRed.delta + (steerRed.granularity * steerRed.direction), 0, 1 ) );
			drawCircle( gr, Color.magenta.darker(), next, 6, true );
		}
	}

}
