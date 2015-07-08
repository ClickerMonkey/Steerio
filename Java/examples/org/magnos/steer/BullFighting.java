package org.magnos.steer;

import java.awt.Color;

import org.magnos.steer.behavior.SteerBasicExample;
import org.magnos.steer.behavior.SteerTo;
import org.magnos.steer.behavior.SteerWander2;
import org.magnos.steer.constraint.ConstraintTurning;
import org.magnos.steer.filter.FilterViewVector;
import org.magnos.steer.target.TargetFiltered;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.util.FieldOfView;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.Scene;


public class BullFighting extends SteerBasicExample
{

    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;
    
    public static void main( String[] args )
    {
        Game game = new BullFighting( WIDTH, HEIGHT );
        GameLoop loop = new GameLoopVariable( 0.1f );
        GameScreen screen = new GameScreen( WIDTH, HEIGHT, true, loop, game );
        screen.setBackground( Color.black );
        GameScreen.showWindow( screen, "BullFighting" );
    }
    
    SteerSprite bull;
    SteerSprite fighter;
    
    public BullFighting( int w, int h )
    {
        super( w, h );
    }

    @Override
    public void start( Scene scene )
    {
        fighter = newSprite( Color.RED, 10, 100, new SteerWander2( 400, 0, 100, 50, 80 ) );
        
        bull = newSprite( Color.WHITE, 10, 100, new SteerFirst<Vec2>( 400,
           new SteerTo<Vec2>( 400, new TargetFiltered<Vec2>( fighter, FilterViewVector.fromDegrees( 20, 400, FieldOfView.PARTIAL, Vec2.class ) ) ),
           new SteerWander2( 400, 0, 100, 50, 80 )
        ));
        bull.controller.constraint = new ConstraintTurning<Vec2>( 0.8f );
        bull.position.set( 50, 50 );
    }

}
