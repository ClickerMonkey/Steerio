package org.magnos.steer.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import org.magnos.steer.behavior.SteerBasicExample;
import org.magnos.steer.obstacle.Segment;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.input.GameInput;


public class SegmentTest extends SteerBasicExample
{

    public static final int WIDTH = 512;
	public static final int HEIGHT = 512;
	
	public static void main( String[] args )
	{
		Game game = new SegmentTest( WIDTH, HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( WIDTH, HEIGHT, false, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SegmentTest" );
	}

	public boolean playing;
	public Segment<Vec2> segment1;
	public Segment<Vec2> segment2;

    public SegmentTest( int w, int h )
    {
        super( w, h );
    }

	@Override
	public void start( Scene scene )
	{
	    segment1 = new Segment<Vec2>( 
	      new Vec2( 100, 100 ),
	      new Vec2( 100, 300 )
	    );
	    
	    segment2 = new Segment<Vec2>(
	      new Vec2( 300, 100 ),
	      new Vec2( 300, 300 )
	    );
	    
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
            segment1.start.set( input.mouseX, input.mouseY );
        }
        if (input.keyDown[KeyEvent.VK_W])
        {
            segment1.end.set( input.mouseX, input.mouseY );
        }

        if (input.keyDown[KeyEvent.VK_A])
        {
            segment2.start.set( input.mouseX, input.mouseY );
        }
        if (input.keyDown[KeyEvent.VK_S])
        {
            segment2.end.set( input.mouseX, input.mouseY );
        }
	}

	@Override
	public void update( GameState state, Scene scene )
	{
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
        drawCircle( gr, Color.cyan, segment1.start, 5, false );
        drawCircle( gr, Color.cyan, segment1.end, 5, false );
        drawLine( gr, Color.blue, segment1.start, segment1.end, false );
        
        drawCircle( gr, Color.yellow, segment2.start, 5, false );
        drawCircle( gr, Color.yellow, segment2.end, 5, false );
        drawLine( gr, Color.orange, segment2.start, segment2.end, false );
        
        Vec2 normal = new Vec2();
        float distance = segment1.getDistanceAndNormal( segment2.start, segment2.end, normal );
        
        int textY = 0;
        gr.setColor( Color.white );
        gr.drawString( String.format( "distance: %.2f", distance ), 2, textY += 12 );
        gr.drawString( String.format( "normal: {%.2f, %.2f}", normal.x, normal.y ), 2, textY += 12 );
        
        /*
        Vec2 u = segment1.end.sub( segment1.start );
        Vec2 v = segment2.end.sub( segment2.start );
        Vec2 w = segment1.start.sub( segment2.start );
        float a = u.dot( u );
        float b = u.dot( v );
        float c = v.dot( v );
        float d = u.dot( w );
        float e = v.dot( w );
        float D = a * c - b * b;
        float sc, sN, sD = D;
        float tc, tN, tD = D;
        
        // parallel
        if (D < SteerMath.EPSILON) {
            sN = 0.0f;
            sD = 1.0f;
            tN = e;
            sD = c;
        } else {
            sN = (b * e - c * d);
            tN = (a * e - b * d);
            if (sN < 0.0f) {    
                sN = 0.0f;
                tN = e;
                tD = c;
            } else if (sN > sD) {
                sN = sD;
                tN = e + b;
                tD = c;
            }
        }
        
        if (tN < 0.0) { 
            tN = 0.0f;
            if (-d < 0.0f)
                sN = 0.0f;
            else if (-d > a)
                sN = sD;
            else {
                sN = -d;
                sD = a;
            }
        }  else if (tN > tD) {
            tN = tD;
            if ((-d + b) < 0.0f)
                sN = 0.0f;
            else if ((-d + b) > a)
                sN = sD;
            else {
                sN = (-d +  b);
                sD = a;
            }
        }
        
        sc = (Math.abs(sN) < SteerMath.EPSILON ? 0.0f : sN / sD);
        tc = (Math.abs(tN) < SteerMath.EPSILON ? 0.0f : tN / tD);

        Vec2 s = w.adds( u, sc );
        Vec2 t = v.mul( tc );
        Vec2 diff = s.sub( t );
        
        drawLine( gr, Color.white, s, t, false );

        gr.drawString( String.format( "distance: %.2f", diff.normalize() ), 2, textY += 12 );
        gr.drawString( String.format( "normal: {%.2f, %.2f}", diff.x, diff.y ), 2, textY += 12 );
        
        /*
        
        Vec2 outNormal = new Vec2();
        Vec2 a = segment1.start;
        Vec2 b = segment1.end.sub( segment1.start );
        Vec2 c = segment2.start;
        Vec2 e = segment2.end.sub( segment2.start );
        Vec2 u = a.sub( c );

        float ub = u.dot( b );
        float ee = e.dot( e );
        float ue = u.dot( e );
        float be = b.dot( e );
        float bb = b.dot( b );
        float denom = bb * ee - be * be;

        // parallel or coincident
        if ( denom == 0.0f )
        {
            result =  SteerMath.closest( a, b, c, outNormal ).subi( c ).normalize() - 0;
        }
        else
        {
            // skew
            float invdenom = 1.0f / denom;
            float s = (ub * ee - ue * be) * invdenom;
            float t = (ub * be - ue * bb) * invdenom;

            float sclamped = SteerMath.clamp( s, 0.0f, 1.0f );
            float tclamped = SteerMath.clamp( t, 0.0f, 1.0f );

            Vec2 ab = outNormal.interpolate( a, b, sclamped );
            Vec2 ce = outNormal.interpolate( c, e, tclamped );
            
            drawLine( gr, Color.white, ab, ce, false );
            
            result = outNormal.directi( ce, ab ).normalize() - 0;
        }
        */
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
