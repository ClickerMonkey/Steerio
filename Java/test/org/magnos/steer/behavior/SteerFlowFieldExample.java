
package org.magnos.steer.behavior;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.SteerMath;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class SteerFlowFieldExample extends SteerBasicExample
{

	public static void main( String[] args )
	{
		Game game = new SteerFlowFieldExample( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( DEFAULT_WIDTH, DEFAULT_HEIGHT, true, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SteerFlowFieldExample" );
	}

	private SteerFlowField2 flow;

	public SteerFlowFieldExample( int w, int h )
	{
		super( w, h );
	}
	
	@Override
	public void start( Scene scene )
	{
		Vec2[][] field = generateSineField( 32, 24, 0.1f, 0.0f, 0.2f, 0.2f );
		
		newSprite( Color.blue, 15, 300, 1000,  
			flow = new SteerFlowField2( 50.0f, new Vec2( 0, 0 ), new Vec2( width, height ), field )
		);
	}
	
	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		final Vec2 s = new Vec2();
		final Vec2 e = new Vec2();
		final Vec2 p = new Vec2();
		final Vec2 q = new Vec2();
		final Color colorVector = new Color( 80, 80, 80 );
		
		for (int y = 0; y < flow.cellsHigh; y++)
		{
			for (int x = 0; x < flow.cellsWide; x++)
			{
				// origin of vector
				s.x = x * flow.cellWidth + flow.fieldMin.x;
				s.y = y * flow.cellHeight + flow.fieldMin.y;
				// end of vector
				e.set( 1, 0 ).rotatei( flow.field[y][x] ).muli( 10.0f ).addi( s );
				// top arrow of vector
				p.angle( (float)Math.toRadians( 140 ), 3.0f ).rotatei( flow.field[y][x] ).addi( e );
				// bottom arrow of vector
				q.angle( (float)Math.toRadians( 220 ), 3.0f ).rotatei( flow.field[y][x] ).addi( e );
				
				drawLine( gr, colorVector, s, e, false );
				drawLine( gr, colorVector, p, e, false );
				drawLine( gr, colorVector, q, e, false );
			}
		}
		
		drawCircle( gr, Color.red, flow.lookaheadPoint, 5.0f, false );

		super.draw( state, gr, scene );
	}

	private Vec2[][] generateSineField(int rows, int columns, float xradians, float xoffset, float yradians, float yoffset)
	{
		Vec2[][] field = new Vec2[rows][columns];
		
		for (int y = 0; y < rows; y++)
		{
			for (int x = 0; x < columns; x++)
			{
				Vec2 v = new Vec2();
				v.x = SteerMath.sin( y * yradians + yoffset );
				v.y = SteerMath.sin( x * xradians + xoffset );
				v.normali();
				field[y][x] = v;
			}
		}
		
		return field;
	}
	
}
