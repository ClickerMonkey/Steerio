package org.magnos.steer.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import org.magnos.steer.SteerMath;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.input.GameInput;


public class FovTest implements Game
{

	public static final int WIDTH = 512;
	public static final int HEIGHT = 512;
	
	public static void main( String[] args )
	{
		Game game = new FovTest();
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( WIDTH, HEIGHT, false, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "Field-of-view Test" );
	}

	private static final Ellipse2D.Float circle = new Ellipse2D.Float();
	private static final Line2D.Float line = new Line2D.Float();
	
	public boolean playing;
	public Vec2 origin;
	public Vec2 direction;
	public Vec2 target;
	public Vec2 fov;
	public float fovAngle;
	public Vec2 circlePos;
	public float circleRadius;
	public boolean circleEntirely;

	@Override
	public void start( Scene scene )
	{
		origin = new Vec2( WIDTH / 2, HEIGHT / 2 );
		target = new Vec2( WIDTH / 4, HEIGHT / 4 );
		circlePos = new Vec2( WIDTH / 3, HEIGHT / 2 );
		circleRadius = 50.0f;
		circleEntirely = false;
		direction = new Vec2( 1.0f, 0.0f );
		fov = Vec2.fromAngle( fovAngle = SteerMath.PI * 0.25f );
		playing = true;
	}

	@Override
	public void input( GameInput input )
	{
		if (input.keyDown[KeyEvent.VK_ESCAPE])
		{
			playing = false;
		}
		
		if (input.keyDown[KeyEvent.VK_T])
		{
			target.set( input.mouseX, input.mouseY );
		}
		if (input.keyDown[KeyEvent.VK_O])
		{
			origin.set( input.mouseX, input.mouseY );
		}
		if (input.keyDown[KeyEvent.VK_C])
		{
			circlePos.set( input.mouseX, input.mouseY );
		}
		if (input.keyDown[KeyEvent.VK_D])
		{
			direction.set( input.mouseX, input.mouseY ).subi( origin ).normali();
		}
		if (input.keyDown[KeyEvent.VK_F])
		{
			fov.set( input.mouseX, input.mouseY ).subi( origin ).normali();
			fovAngle = fov.angle();
		}
		if (input.keyUp[KeyEvent.VK_E])
		{
			circleEntirely = !circleEntirely;
		}
	}

	@Override
	public void update( GameState state, Scene scene )
	{
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		float radius = 200.0f;
		
		Vec2 upper = new Vec2( fov.x, fov.y );
		Vec2 lower = new Vec2( fov.x, -fov.y );
		
		Vec2 upperRotated = upper.rotate( direction );
		Vec2 lowerRotated = lower.rotate( direction );

		/*
		Vec2 towards = target.sub( origin );
		float ut = upperRotated.cross( towards );
		float lt = lowerRotated.cross( towards );
		
		boolean view = (ut <= 0 && lt >= 0 ) || (fov.x < 0.0f && ((ut >= 0 && lt >= 0) || (ut <= 0 && lt <= 0)));
		
		boolean circleInView = SteerMath.isCircleInView( origin, direction, fov, circlePos, circleRadius, circleEntirely );
		*/
		
		gr.setColor( Color.orange );
		line.setLine( origin.x, origin.y, origin.x + upperRotated.x * radius, origin.y + upperRotated.y * radius );
		gr.draw( line );
		line.setLine( origin.x, origin.y, origin.x + lowerRotated.x * radius, origin.y + lowerRotated.y * radius );
		gr.draw( line );
		
		drawCircle( gr, upperRotated.mul( radius ).addi( origin ), 5, Color.orange );
		drawCircle( gr, direction.mul( radius ).addi( origin ), 5, Color.gray );
		drawCircle( gr, origin, 5, Color.white );
		drawCircle( gr, target, 5, Color.red );
		drawCircle( gr, circlePos, circleRadius, Color.yellow );

		/*
		gr.drawString( String.format( "fov: {%.2f,%.2f}", fov.x, fov.y ), 2, 12 );
		gr.drawString( String.format( "origin.distanceSq(target): %.2f", origin.distanceSq( target ) ), 2, 24 );
		gr.drawString( String.format( "direction.dot(towards): %.2f", direction.dot(towards) ), 2, 36 );
		gr.drawString( String.format( "in view: %s", view ), 2, 48 );
		gr.drawString( String.format( "upperRotated.cross( towards ): %.2f", upperRotated.cross( towards ) ), 2, 60 );
		gr.drawString( String.format( "lowerRotated.cross( towards ): %.2f", lowerRotated.cross( towards ) ), 2, 72 );
		gr.drawString( String.format( "isCircleInView(entirely=%s): %s", circleEntirely, circleInView ), 2, 84 );
		*/
		
		Vec2 _V = circlePos.sub( origin );
		double _a = _V.dot( direction );                  // how far along the direction the sphere's center is
		double _b = _a * Math.tan( fovAngle );            // radius of the cone at _a
		double _c = Math.sqrt( _V.lengthSq() - _a * _a ); // distance from center of sphere to axis of the cone
		double _d = _c - _b;                              // distance from center of sphere to the surface of the cone
		double _e = _d * Math.cos( fovAngle );            // shortest distance from center of the sphere to the surface of the cone
		
		boolean _inview = false;
		
		if ( _e >= circleRadius ) {
		    // cull
		} else if ( _e <= -circleRadius ) {
		    // totally
		    _inview = true;
		} else {
		    // partially
		    if (!circleEntirely) {
		        _inview = true;
		    }
		}
		
		Vec2 farAlong = SteerMath.closest( origin, direction.mul( radius ).addi( origin ), circlePos, new Vec2() );
        line.setLine( origin.x, origin.y, farAlong.x, farAlong.y );
        gr.draw( line );
        line.setLine( farAlong.x + direction.y * _a, farAlong.y - direction.x * _a, farAlong.x - direction.y * _a, farAlong.y + direction.x * _a );
        gr.draw( line );
        line.setLine( farAlong.x, farAlong.y, circlePos.x, circlePos.y );
        gr.setColor( Color.blue );
        gr.draw( line );

        int textY = 0;
        gr.setColor( Color.white );
        gr.drawString( String.format( "how far along: %.2f (expected=%.2f)", _a, farAlong.distance( origin ) ), 2, textY += 12 );
        gr.drawString( String.format( "radius of cone: %.2f", _b ), 2, textY += 12 );
        gr.drawString( String.format( "circle->axis of cone: %.2f (expected=%.2f)", _c, farAlong.distance( circlePos ) ), 2, textY += 12 );
        gr.drawString( String.format( "circle->surface of cone: %.2f", _d ), 2, textY += 12 );
        gr.drawString( String.format( "shortest distance: %.2f", _e ), 2, textY += 12 );
        gr.drawString( String.format( "inview(entirely=%s): %s", circleEntirely, _inview ), 2, textY += 12 );
        gr.drawString( String.format( "fov: {%.2f,%.2f}: %.2f (%.2f)", fov.x, fov.y, fovAngle, Math.toDegrees( fovAngle ) ), 2, textY += 12 );
        gr.drawString( String.format( "isCircleInView: %s", SteerMath.isCircleInView( origin, direction, Math.tan(fovAngle), Math.cos(fovAngle), circlePos, circleRadius, circleEntirely ) ), 2, textY += 12 );
	}
	
	private void drawCircle( Graphics2D gr, Vec2 v, float size, Color color )
	{
		circle.setFrame( v.x - size, v.y - size, size * 2, size * 2 );
		
		gr.setColor( color );
		gr.draw( circle );
	}
	
	@Override
	public void destroy()
	{

	}

	@Override
	public boolean isPlaying()
	{
		return playing;
	}

}
