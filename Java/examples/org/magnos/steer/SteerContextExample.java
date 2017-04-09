package org.magnos.steer;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.behavior.SteerBasicExample;
import org.magnos.steer.behavior.SteerCohesion;
import org.magnos.steer.behavior.SteerSeparation;
import org.magnos.steer.obstacle.Sphere;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.array.SpatialArray;
import org.magnos.steer.test.SteerSprite;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class SteerContextExample extends SteerBasicExample
{

    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;
    
    public static void main( String[] args )
    {
        Game game = new SteerContextExample( WIDTH, HEIGHT );
        GameLoop loop = new GameLoopVariable( 0.1f );
        GameScreen screen = new GameScreen( WIDTH, HEIGHT, true, loop, game );
        screen.setBackground( Color.black );
        GameScreen.showWindow( screen, "SteerContextExample" );
    }

    public final long GROUP_TARGET0 = 1 << 0;
    public final long GROUP_TARGET1 = 1 << 1;
    public final long GROUP_ENEMY   = 1 << 2;
    
    public SpatialDatabase<Vec2> database;
    public Steer<Vec2> steerToTarget0;
    public Steer<Vec2> steerToTarget1;
    public Steer<Vec2> steerAwayEnemy;
    public SteerSprite sprite;
    public SpatialEntity<Vec2> sphere0;
    public SpatialEntity<Vec2> sphere1;
    public SpatialEntity<Vec2> sphere2;
    
    public SteerContextExample( int w, int h )
    {
        super( w, h );
    }

    @Override
    public void start( Scene scene )
    {
        database = new SpatialArray<Vec2>( 16 );

        database.add( sphere0 = new Sphere<Vec2>( new Vec2( WIDTH * 0.2f, HEIGHT * 0.5f ), 20 ).withGroups( GROUP_TARGET0 ).withFixed( true ) );
        database.add( sphere1 = new Sphere<Vec2>( new Vec2( WIDTH * 0.8f, HEIGHT * 0.5f ), 20 ).withGroups( GROUP_TARGET1 ).withFixed( true ) );
        database.add( sphere2 = new Sphere<Vec2>( new Vec2( WIDTH * 0.7f, HEIGHT * 0.5f ), 15 ).withGroups( GROUP_ENEMY ).withFixed( true ) );

        steerToTarget0 = new SteerCohesion<Vec2>( database, Vec2.FACTORY ).withFilterGroups( GROUP_TARGET0 ).withDropOff( WIDTH, 0 ).withRange( 0, 1000 );
        steerToTarget1 = new SteerCohesion<Vec2>( database, Vec2.FACTORY ).withFilterGroups( GROUP_TARGET1 ).withDropOff( WIDTH, 0 ).withRange( 0, 1000 );
        steerAwayEnemy = new SteerSeparation<Vec2>( database, Vec2.FACTORY ).withFilterGroups( GROUP_ENEMY ).withDropOff( WIDTH, 0 ).withRange( 0, 1000 );
        boolean[] good = { true, true, false };
        
        sprite = newSprite( Color.blue, 10, 200, new SteerContext<Vec2>( good, steerToTarget0, steerToTarget1, steerAwayEnemy ) );
        sprite.position.set( WIDTH * 0.6f, HEIGHT * 0.5f );
    }
    
    @Override
    public void update( GameState state, Scene scene )
    {
        super.update( state, scene );
        
        database.refresh();
    }
    
    @Override
    public void draw( GameState state, Graphics2D gr, Scene scene )
    {
        super.draw( state, gr, scene );
        
        for ( SpatialEntity<Vec2> entity : database )
        {            
            drawObstacle( gr, entity, entity.getSpatialGroups() == GROUP_ENEMY ? Color.red : Color.green );
        }
        /*

        drawSteerForce( gr, steerToTarget0, sprite, Color.green, sphere0.getPosition(), false );
        drawSteerForce( gr, steerToTarget1, sprite, Color.green, sphere1.getPosition(), false );
        drawSteerForce( gr, steerAwayEnemy, sprite, Color.red, sphere2.getPosition(), false );
        */
    }

}
