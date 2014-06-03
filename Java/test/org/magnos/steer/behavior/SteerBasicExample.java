
package org.magnos.steer.behavior;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import org.magnos.steer.Path;
import org.magnos.steer.Steer;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.core.EntityList;
import com.gameprogblog.engine.input.GameInput;


public abstract class SteerBasicExample implements Game
{

	public static final int DEFAULT_WIDTH = 640;
	public static final int DEFAULT_HEIGHT = 480;

	private static final int EDGE_L = 1 << 0;
	private static final int EDGE_R = 1 << 1;
	private static final int EDGE_T = 1 << 2;
	private static final int EDGE_B = 1 << 3;

	private static final Line2D.Float line = new Line2D.Float();
	private static final Ellipse2D.Float ellipse = new Ellipse2D.Float();
	private static final Rectangle2D.Float rect = new Rectangle2D.Float();

	public static float DRAW_FORCE_MIN = 10.0f;
	public static float DRAW_FORCE_MAX = 100.0f;
	public static float DRAW_FORCE_SCALE = 0.01f;

	public Vec2 mouse = new Vec2();
	public EntityList<SteerSprite> sprites = new EntityList<SteerSprite>();
	public boolean playing;
	public int width;
	public int height;
	public boolean drawForces = true;
	public boolean drawCircles = true;
	public boolean wrapEntities = true;

	public SteerBasicExample( int w, int h )
	{
		width = w;
		height = h;
		playing = true;
	}

	public SteerSprite newSprite( Color color, float radius, float velocityMax, float accelerationMax, Steer<Vec2> steer )
	{
		SteerSprite s = new SteerSprite( color, radius, velocityMax, accelerationMax, steer );
		s.position.set( width / 2, height / 2 );
		sprites.add( s );
		return s;
	}

	@Override
	public void input( GameInput input )
	{
		if (input.keyDown[KeyEvent.VK_ESCAPE])
		{
			playing = false;
		}

		mouse.set( input.mouseX, input.mouseY );
		
		if (input.keyUp[KeyEvent.VK_F1])
		{
			drawForces = !drawForces;
		}
		
		if (input.keyUp[KeyEvent.VK_F2])
		{
			drawCircles = !drawCircles;
		}
	}

	@Override
	public void update( GameState state, Scene scene )
	{
		sprites.update( state, scene );
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
	    if (wrapEntities)
	    {
	        for (int i = 0; i < sprites.size(); i++)
	        {
	            wrapVector( gr, sprites.get( i ).getPosition() );
	        }
	    }

		sprites.draw( state, gr, scene );

		if (drawForces)
		{
			for (int i = 0; i < sprites.size(); i++)
			{
				SteerSprite s = sprites.get( i );
				Steer<Vec2> f = s.controller.force;

				Vec2 force = new Vec2();
				f.getForce( state.seconds, s, force );

				drawForce( gr, Color.white, s.position, force, wrapEntities );
			}
		}
	}

	public static void drawLine( Graphics2D gr, Color color, Vec2 s, Vec2 e, boolean wrap )
	{
		line.setLine( s.x, s.y, e.x, e.y );

		gr.setColor( color );
		drawShape( gr, line, wrap, getEdgesFromLine( gr, s.x, s.y, e.x, e.y ) );
	}

	public static void drawVector( Graphics2D gr, Color color, Vec2 origin, Vec2 dir, float scale, boolean wrap )
	{
		line.setLine( origin.x, origin.y, origin.x + dir.x * scale, origin.y + dir.y * scale );

		gr.setColor( color );
		drawShape( gr, line, wrap, getEdgesFromLine( gr, line.x1, line.y1, line.x2, line.y2 ) );
	}

	public static void drawBounds( Graphics2D gr, Color color, Bound2 b, boolean wrap )
	{
		rect.setFrameFromDiagonal( b.left, b.top, b.right, b.bottom );

		gr.setColor( color );
		drawShape( gr, rect, wrap, getEdgesFromLine( gr, b.left, b.top, b.right, b.bottom ) );
	}

	public static void drawTarget( Graphics2D gr, Color color, Vec2 v, float radius, boolean wrap )
	{
		int edges = getEdgesFromLine( gr, v.x - radius, v.y, v.x + radius, v.y ) |
			getEdgesFromLine( gr, v.x, v.y - radius, v.x, v.y + radius );

		gr.setColor( color );

		line.setLine( v.x - radius, v.y, v.x + radius, v.y );
		drawShape( gr, line, wrap, edges );

		line.setLine( v.x, v.y - radius, v.x, v.y + radius );
		drawShape( gr, line, wrap, edges );
	}

	public static void drawForce( Graphics2D gr, Color color, Vec2 offset, Vec2 force, boolean wrap )
	{
		Vec2 n = force.mul( DRAW_FORCE_SCALE ).clamp( DRAW_FORCE_MIN, DRAW_FORCE_MAX );

		line.setLine( offset.x, offset.y, offset.x + n.x, offset.y + n.y );

		gr.setColor( color );
		drawShape( gr, line, wrap, getEdgesFromLine( gr, line.x1, line.y1, line.x2, line.y2 ) );
	}

    public static void drawCircle( Graphics2D gr, Color color, Vec2 offset, float radius, boolean wrap )
    {
        ellipse.setFrame( offset.x - radius, offset.y - radius, radius * 2, radius * 2 );

        gr.setColor( color );
        drawShape( gr, ellipse, wrap, getEdges( gr, offset, radius, radius ) );
    }

    public static void fillCircle( Graphics2D gr, Color color, Vec2 offset, float radius, boolean wrap )
    {
        ellipse.setFrame( offset.x - radius, offset.y - radius, radius * 2, radius * 2 );

        gr.setColor( color );
        fillShape( gr, ellipse, wrap, getEdges( gr, offset, radius, radius ) );
    }

	public static void drawPath( Graphics2D gr, Color color, Vec2[] path, boolean loop, boolean wrap )
	{
		int edges = 0;
		Path2D.Float p = new Path2D.Float();

		for (int i = 0; i < path.length; i++)
		{
			Vec2 v = path[i];
			if (i == 0)
			{
				p.moveTo( v.x, v.y );
			}
			else
			{
				p.lineTo( v.x, v.y );
			}
			edges |= getEdges( gr, v, 0, 0 );
		}

		if (loop)
		{
			p.closePath();
		}

		gr.setColor( color );
		drawShape( gr, p, wrap, edges );
	}

	public static void drawPath( Graphics2D gr, Color color, Path<Vec2> path, int segments, boolean wrap )
	{
		int edges = 0;
		Path2D.Float p = new Path2D.Float();
		Vec2 v = new Vec2();

		path.set( v, 0.0f );
		edges |= getEdges( gr, v, 0, 0 );
		p.moveTo( v.x, v.y );

		for (int i = 1; i <= segments; i++)
		{
			path.set( v, (float)i / segments );
			edges |= getEdges( gr, v, 0, 0 );
			p.lineTo( v.x, v.y );
		}

		gr.setColor( color );
		drawShape( gr, p, wrap, edges );
	}

	public static int getEdgesFromLine( Graphics2D gr, float x0, float y0, float x1, float y1 )
	{
		return getEdges( gr, (x0 + x1) * 0.5f, (y0 + y1) * 0.5f, Math.abs( x1 - x0 ) * 0.5f, Math.abs( y1 - y0 ) * 0.5f );
	}

	public static int getEdges( Graphics2D gr, Vec2 p, float hw, float hh )
	{
		return getEdges( gr, p.x, p.y, hw, hh );
	}

	public static int getEdges( Graphics2D gr, float px, float py, float hw, float hh )
	{
		final Rectangle bounds = gr.getDeviceConfiguration().getBounds();

		int edges = 0;

		if (px < bounds.getMinX() + hw)
		{
			edges |= EDGE_L;
		}
		if (px > bounds.getMaxX() - hw)
		{
			edges |= EDGE_R;
		}

		if (py < bounds.getMinY() + hh)
		{
			edges |= EDGE_T;
		}
		if (py > bounds.getMaxY() - hh)
		{
			edges |= EDGE_B;
		}

		return edges;
	}

    public static void drawShape( Graphics2D gr, Shape shape, boolean wrap, int edges )
    {
        gr.draw( shape );

        if (!wrap)
        {
            return;
        }

        final Rectangle bounds = gr.getDeviceConfiguration().getBounds();

        final int[] masks = {
            EDGE_L, EDGE_R, EDGE_T, EDGE_B,
            EDGE_L | EDGE_T, EDGE_L | EDGE_B,
            EDGE_R | EDGE_T, EDGE_R | EDGE_B
        };

        final int w = bounds.width;
        final int h = bounds.height;

        final int[][] translate = {
            { w, 0 }, { -w, 0 }, { 0, h }, { 0, -h },
            { w, h }, { w, -h }, { -w, h }, { -w, -h }
        };

        for (int i = 0; i < masks.length; i++)
        {
            if ((masks[i] & edges) == masks[i])
            {
                AffineTransform t = gr.getTransform();
                gr.translate( translate[i][0], translate[i][1] );
                gr.draw( shape );
                gr.setTransform( t );
            }
        }
    }

    public static void fillShape( Graphics2D gr, Shape shape, boolean wrap, int edges )
    {
        gr.fill( shape );

        if (!wrap)
        {
            return;
        }

        final Rectangle bounds = gr.getDeviceConfiguration().getBounds();

        final int[] masks = {
            EDGE_L, EDGE_R, EDGE_T, EDGE_B,
            EDGE_L | EDGE_T, EDGE_L | EDGE_B,
            EDGE_R | EDGE_T, EDGE_R | EDGE_B
        };

        final int w = bounds.width;
        final int h = bounds.height;

        final int[][] translate = {
            { w, 0 }, { -w, 0 }, { 0, h }, { 0, -h },
            { w, h }, { w, -h }, { -w, h }, { -w, -h }
        };

        for (int i = 0; i < masks.length; i++)
        {
            if ((masks[i] & edges) == masks[i])
            {
                AffineTransform t = gr.getTransform();
                gr.translate( translate[i][0], translate[i][1] );
                gr.fill( shape );
                gr.setTransform( t );
            }
        }
    }

	public static void wrapVector( Graphics2D gr, Vec2 v )
	{
		final Rectangle bounds = gr.getDeviceConfiguration().getBounds();

		while (v.x < bounds.getMinX())
			v.x += bounds.getWidth();
		while (v.x > bounds.getMaxX())
			v.x -= bounds.getWidth();
		while (v.y < bounds.getMinY())
			v.y += bounds.getHeight();
		while (v.y > bounds.getMaxY())
			v.y -= bounds.getHeight();
	}

	@Override
	public void destroy()
	{
		sprites.expire();
	}

	@Override
	public boolean isPlaying()
	{
		return playing;
	}

}
