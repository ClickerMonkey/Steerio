package org.magnos.steer.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import org.magnos.steer.BaseSteerSubject;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.input.GameInput;


public class GetDistanceAndNormalTest implements Game
{

	public static final int WIDTH = 512;
	public static final int HEIGHT = 512;
	
	public static void main( String[] args )
	{
		Game game = new GetDistanceAndNormalTest();
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( WIDTH, HEIGHT, false, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "getDistanceAndNormal Test" );
	}

	private static final Ellipse2D.Float circle = new Ellipse2D.Float();
	private static final Line2D.Float line = new Line2D.Float();
	
	public boolean playing;
	BaseSteerSubject<Vec2> a, b;

	@Override
	public void start( Scene scene )
	{
	    a = new BaseSteerSubject<Vec2>( Vec2.FACTORY, 2, 1000 );
        a.position.set( 100, 100 );
        a.velocity.set( 200, 200 );
        
        b = new BaseSteerSubject<Vec2>( Vec2.FACTORY, 2, 1000 );
        b.position.set( 400, 300 );
        b.velocity.set( 0, -100 );
	    
		playing = true;
	}

	@Override
	public void input( GameInput input )
	{
		if (input.keyDown[KeyEvent.VK_ESCAPE])
		{
			playing = false;
		}

        if (input.keyDown[KeyEvent.VK_Q])
        {
            a.position.set( input.mouseX, input.mouseY );
        }
        if (input.keyDown[KeyEvent.VK_W])
        {
            a.velocity.set( input.mouseX, input.mouseY ).subi( a.position );
        }

        if (input.keyDown[KeyEvent.VK_A])
        {
            b.position.set( input.mouseX, input.mouseY );
        }
        if (input.keyDown[KeyEvent.VK_S])
        {
            b.velocity.set( input.mouseX, input.mouseY ).subi( b.position );
        }
	}

	@Override
	public void update( GameState state, Scene scene )
	{
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
        gr.setColor( Color.orange );
        line.setLine( a.position.x, a.position.y, a.position.x + a.velocity.x, a.position.y + a.velocity.y );
        gr.draw( line );
        gr.drawString( "obstacle", a.position.x, a.position.y );
        drawCircle( gr, a.position, 5, Color.orange );
        
        gr.setColor( Color.blue );
        line.setLine( b.position.x, b.position.y, b.position.x + b.velocity.x, b.position.y + b.velocity.y );
        gr.draw( line );
        gr.drawString( "subject", b.position.x, b.position.y );
        drawCircle( gr, b.position, 5, Color.blue );
		
        Vec2 normal = new Vec2();
        float distance = a.getDistanceAndNormal( b.position, b.position.add( b.velocity ), normal );
        
        int textY = 0;
        gr.setColor( Color.white );
        gr.drawString( String.format( "distance: %.2f", distance ), 2, textY += 12 );
        gr.drawString( String.format( "normal: {%.2f, %.2f}", normal.x, normal.y ), 2, textY += 12 );
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
