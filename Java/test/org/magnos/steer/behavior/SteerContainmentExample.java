
package org.magnos.steer.behavior;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import org.magnos.steer.SteerSet;
import org.magnos.steer.obstacle.Bounds;
import org.magnos.steer.obstacle.Segment;
import org.magnos.steer.obstacle.Sphere;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.input.GameInput;


public class SteerContainmentExample extends SteerBasicExample
{

	public static void main( String[] args )
	{
		Game game = new SteerContainmentExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerContainmentExample" );
	}
	
	private Sphere<Vec2> sphere;
	private Bounds<Vec2> bounds;
	private Segment<Vec2> capsule;
	private SteerContainment<Vec2> containment;
	
	public SteerContainmentExample( int w, int h )
	{
		super( w, h );
		
		this.wrapEntities = false;
	}
	
	@Override
    public void input( GameInput input )
    {
	    super.input( input );

        if (input.keyDown[KeyEvent.VK_1])
        {
            containment.obstacle = sphere;
        }
        else if (input.keyDown[KeyEvent.VK_2])
        {
            containment.obstacle = bounds;
        }
        else if (input.keyDown[KeyEvent.VK_3])
        {
            containment.obstacle = capsule;
        }
    }
	
	@Override
	public void start( Scene scene )
	{
		sphere = new Sphere<Vec2>( new Vec2( DEFAULT_WIDTH / 2, DEFAULT_HEIGHT / 2), DEFAULT_HEIGHT / 2 - 50 );
		bounds = Bounds.fromMinMax( new Vec2( 100, 100 ), new Vec2( DEFAULT_WIDTH - 100, DEFAULT_HEIGHT - 100 ) );
		capsule = new Segment<Vec2>( new Vec2( 100, 100 ), new Vec2( DEFAULT_WIDTH - 100, DEFAULT_HEIGHT - 100 ), 80 );
		
		newSprite( Color.blue, 15, 300, 4000, new SteerSet<Vec2>( 4000,
		    containment = new SteerContainment<Vec2>( sphere, 30 ),
		    new SteerWander2( 0, 100, 100, 200 )
		));
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );
		
		if (containment.obstacle == sphere) {
		    drawCircle( gr, Color.red, sphere.position, sphere.radius, false );    
		}
		else if (containment.obstacle == bounds) {
		    drawBounds( gr, Color.red, new Bound2( bounds.min.x, bounds.min.y, bounds.max.x, bounds.max.y ), false );
		}
		else if (containment.obstacle == capsule) {
		    drawLine( gr, Color.red, capsule.start, capsule.end, false );
		}
	}

}
