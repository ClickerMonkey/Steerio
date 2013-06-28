package org.magnos.steer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

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
	public Vector origin;
	public Vector direction;
	public Vector target;
	public Vector fov;
	public Vector circlePos;
	public float circleRadius;
	public boolean circleEntirely;

	@Override
	public void start( Scene scene )
	{
		origin = new Vector( WIDTH / 2, HEIGHT / 2 );
		target = new Vector( WIDTH / 4, HEIGHT / 4 );
		circlePos = new Vector( WIDTH / 3, HEIGHT / 2 );
		circleRadius = 50.0f;
		circleEntirely = false;
		direction = new Vector( 1.0f, 0.0f );
		fov = new Vector().angle( SteerMath.PI * 0.25f, 1.0f );
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
		
		Vector upper = new Vector( fov.x, fov.y );
		Vector lower = new Vector( fov.x, -fov.y );
		
		Vector upperRotated = upper.rotate( direction );
		Vector lowerRotated = lower.rotate( direction );

		Vector towards = target.sub( origin );
		float ut = upperRotated.cross( towards );
		float lt = lowerRotated.cross( towards );
		
		boolean view = (ut <= 0 && lt >= 0 ) || (fov.x < 0.0f && ((ut >= 0 && lt >= 0) || (ut <= 0 && lt <= 0)));
		
		boolean circleInView = SteerMath.isCircleInView( origin, direction, fov, circlePos, circleRadius, circleEntirely );
		
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

		gr.setColor( Color.white );
		gr.drawString( String.format( "fov: {%.2f,%.2f}", fov.x, fov.y ), 2, 12 );
		gr.drawString( String.format( "origin.distanceSq(target): %.2f", origin.distanceSq( target ) ), 2, 24 );
		gr.drawString( String.format( "direction.dot(towards): %.2f", direction.dot(towards) ), 2, 36 );
		gr.drawString( String.format( "in view: %s", view ), 2, 48 );
		gr.drawString( String.format( "upperRotated.cross( towards ): %.2f", upperRotated.cross( towards ) ), 2, 60 );
		gr.drawString( String.format( "lowerRotated.cross( towards ): %.2f", lowerRotated.cross( towards ) ), 2, 72 );
		gr.drawString( String.format( "isCircleInView(entirely=%s): %s", circleEntirely, circleInView ), 2, 84 );
	}
	
	private void drawCircle( Graphics2D gr, Vector v, float size, Color color )
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
