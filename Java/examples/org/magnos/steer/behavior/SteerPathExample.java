
package org.magnos.steer.behavior;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSet;
import org.magnos.steer.constraint.ConstraintTurning;
import org.magnos.steer.filter.FilterAnd;
import org.magnos.steer.filter.FilterInFront;
import org.magnos.steer.filter.FilterProximity;
import org.magnos.steer.path.LinearPath;
import org.magnos.steer.target.TargetFiltered;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.vec.Vec2;

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
	
	private LinearPath<Vec2> path = new LinearPath<Vec2>(
		new Vec2( 100, 100 ),
		new Vec2( 500, 100 ),
		new Vec2( 400, 300 ),
		new Vec2( 200, 200 ),
		new Vec2( 100, 400 ),
		new Vec2( 600, 400 )
	);
	
	private SteerPath<Vec2> steerRed;
	private SteerSprite sprite;
	
	public SteerPathExample( int w, int h )
	{
		super( w, h );
	}

	@Override
	public void start( Scene scene )
	{
		steerRed = new SteerPath<Vec2>( 1000, path, 0.01f, 0.05f, 15, 40, 60, 1, true, false, Vec2.FACTORY );
		
		sprite = newSprite( Color.blue, 15, 300, steerRed );
		sprite.position.set( 100, 110 );

		SteerSprite quicker = newSprite( Color.orange, 15, 300, new SteerSet<Vec2>( 10000,
			new SteerPath<Vec2>( 10000, path, 0.01f, 0.025f, 0, 10, 20, 1, true, false, Vec2.FACTORY ),
			new SteerAway<Vec2>( 10000,
			    new TargetFiltered<Vec2>( sprite, 
			        new FilterAnd<Vec2>(
			            new FilterInFront<Vec2>(), 
			            new FilterProximity<Vec2>(100) 
			        ) 
			    ), true ) 
		));
		quicker.controller.constraint = new ConstraintTurning<Vec2>( 10.0f );
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
			
			Vec2 next = new Vec2();

			path.set( next, SteerMath.clamp( steerRed.delta + (steerRed.lookahead * steerRed.direction), 0, 1 ) );
			drawCircle( gr, Color.blue, next, 6, true );

			path.set( next, SteerMath.clamp( steerRed.delta + (steerRed.granularity * steerRed.direction), 0, 1 ) );
			drawCircle( gr, Color.magenta.darker(), next, 6, true );
		}
	}

}
