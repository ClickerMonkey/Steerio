
package org.magnos.steer.filter;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.BaseSteerSubject;
import org.magnos.steer.SteerSet;
import org.magnos.steer.behavior.SteerArrive;
import org.magnos.steer.behavior.SteerBasicExample;
import org.magnos.steer.behavior.SteerDrive;
import org.magnos.steer.target.TargetFilteredSubject;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class FilterInFrontExample extends SteerBasicExample
{

	public static void main( String[] args )
	{
		Game game = new FilterInFrontExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "FilterInFrontExample" );
	}
	
	private SteerArrive<Vec2> arrive;
	private SteerSprite sprite;
	private BaseSteerSubject<Vec2> mouseSubject;
	
	public FilterInFrontExample(int w, int h)
	{
		super( w, h );
	}

	@Override
	public void start( Scene scene )
	{
	    mouseSubject = new BaseSteerSubject<Vec2>( Vec2.FACTORY, 0, 0 );
	    mouseSubject.position.set( mouse );
	    
		sprite = newSprite( Color.blue, 15, 300, 1000, new SteerSet<Vec2>( 
			arrive = new SteerArrive<Vec2>( new TargetFilteredSubject<Vec2>( mouseSubject, new FilterInFront<Vec2>() ), 100, 0, false ),
			new SteerDrive<Vec2>( 0, 0, 100 )
		));
	}

    @Override
    public void update( GameState state, Scene scene )
    {
        mouseSubject.velocity.set( mouse ).subi( mouseSubject.position );
        mouseSubject.position.set( mouse );
        
        super.update( state, scene );
    }
	
	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		super.draw( state, gr, scene );

		if ( drawCircles )
		{
			drawCircle( gr, Color.lightGray, sprite.position, arrive.caution, false );
		}
	}

}
