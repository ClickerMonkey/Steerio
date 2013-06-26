package org.magnos.steer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.magnos.steer.spatial.CollisionCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.array.SpatialArray;
import org.magnos.steer.spatial.grid.SpatialGrid;
import org.magnos.steer.spatial.grid.SpatialGridCell;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.core.Entity;
import com.gameprogblog.engine.core.EntityList;
import com.gameprogblog.engine.input.GameInput;


public class SpatialDatabaseExample implements Game, CollisionCallback
{

	public static void main( String[] args )
	{
		Game game = new SpatialDatabaseExample();
		GameLoop loop = new GameLoopVariable( 0.1f );
		GameScreen screen = new GameScreen( WIDTH, HEIGHT, false, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "SpatialDatabaseExample" );
	}
	
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final float RADIUS_MIN = 0.5f;
	public static final float RADIUS_MAX = 4.0f;
	public static final long GROUP_MIN = 0;
	public static final long GROUP_MAX = 15;
	public static final float VELOCITY_MAX = 100.0f;
	
	public EntityList<BouncyBall> balls;
	public SpatialDatabase database;
	public int ballCount;
	public int knn;
	public SpatialEntity[] knnNeighbors;
	public float[] knnOverlap;
	public boolean playing;
	public Vector mouse = new Vector();
	
	public int statUniqueCollisions;
	public int statMutualCount;
	public int statOnesideCount;
	public int statTotalCollisions;
	public long statCollisionStartNanos;
	public long statCollisionEndNanos;
	public double statCollisionSeconds;
	
	public long statKnnStartNanos;
	public long statKnnEndNanos;
	public double statKnnSeconds;
	public int statKnnFound;
	public float statKnnMax;
	
	@Override
	public void start( Scene scene )
	{
		ballCount = 512;
		
		knn = 32;
		knnNeighbors = new SpatialEntity[ knn ];
		knnOverlap = new float[ knn ];
		
		balls = new EntityList<BouncyBall>();
		database = new SpatialArray( ballCount );
		
		fill();
		
		playing = true;
	}
	
	private void fill()
	{
		while (balls.size() < ballCount)
		{
			float radius = SteerMath.randomFloat( RADIUS_MIN, RADIUS_MAX );
			long spatialGroups = SteerMath.randomLong( GROUP_MIN, GROUP_MAX );
			long spatialCollisionGroups = SteerMath.randomLong( GROUP_MIN, GROUP_MAX );
			boolean dynamic = SteerMath.randomInt( 10 ) != 0;
			
			BouncyBall ball = new BouncyBall( radius, spatialGroups, spatialCollisionGroups, dynamic );
			ball.position.x = SteerMath.randomFloat( radius, WIDTH - radius );
			ball.position.y = SteerMath.randomFloat( radius, HEIGHT - radius );
			ball.velocity.x = SteerMath.randomFloat( -VELOCITY_MAX, VELOCITY_MAX );
			ball.velocity.y = SteerMath.randomFloat( -VELOCITY_MAX, VELOCITY_MAX );
			
			balls.add( ball );
			database.add( ball );
		}
		
		int tooMany = balls.size() - ballCount;
		
		for (int i = 0; i < tooMany; i++)
		{
			balls.get( i ).expire();
		}
	}

	@Override
	public void input( GameInput input )
	{
		if (input.keyDown[KeyEvent.VK_ESCAPE])
		{
			playing = false;
		}
		
		mouse.set( input.mouseX, input.mouseY );
	}

	@Override
	public void update( GameState state, Scene scene )
	{
		balls.update( state, scene );
		
		database.refresh();
		
		statCollisionStartNanos = System.nanoTime();
		statUniqueCollisions = database.handleCollisions( this );
		statCollisionEndNanos = System.nanoTime();
		statCollisionSeconds = (statCollisionEndNanos - statCollisionStartNanos) * 0.000000001;
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		if ( database instanceof SpatialGrid )
		{
			SpatialGrid grid = (SpatialGrid)database;
			Rectangle2D.Float rect = new Rectangle2D.Float();
			
			gr.setColor( Color.lightGray );
			for (int y = 0; y < grid.height; y++)
			{
				for (int x = 0; x < grid.width; x++)
				{
					SpatialGridCell cell = grid.cells[y][x];
					rect.setFrameFromDiagonal( cell.l, cell.t, cell.r, cell.b );
					gr.draw( rect );
				}
			}
		}
		
		gr.setColor( Color.green );
		balls.draw( state, gr, scene );

		statKnnStartNanos = System.nanoTime();
		statKnnFound = database.knn( mouse, knn, SpatialDatabase.ALL_GROUPS, knnNeighbors, knnOverlap );
		statKnnEndNanos = System.nanoTime();
		statKnnSeconds = (statKnnEndNanos - statKnnStartNanos) * 0.000000001;
		
		gr.setColor( Color.darkGray );
		Line2D.Float line = new Line2D.Float();
		line.x1 = mouse.x;
		line.y1 = mouse.y;
		statKnnMax = 0.0f;
		
		for (int i = 0; i < statKnnFound; i++)
		{
			SpatialEntity se = knnNeighbors[i];
			statKnnMax = Math.max( statKnnMax, knnOverlap[i] );
			
			line.x2 = se.getPosition().x;
			line.y2 = se.getPosition().y;
			gr.draw( line );
		}
		
		int textY = 4;
		gr.setColor( Color.white );
		gr.drawString( String.format("Unique: %d", statUniqueCollisions), 10, textY += 16 );
		gr.drawString( String.format("Total: %d", statTotalCollisions), 10, textY += 16 );
		gr.drawString( String.format("Mutual: %d", statMutualCount), 10, textY += 16 );
		gr.drawString( String.format("One-sided: %d", statOnesideCount), 10, textY += 16 );
		gr.drawString( String.format("Collision Elapsed: %.9f", statCollisionSeconds), 10, textY += 16 );
		gr.drawString( String.format("Collision Per-second: %d", (long)(1.0 / (statCollisionSeconds / ballCount))), 10, textY += 16 );
		gr.drawString( String.format("KNN Elapsed: %.9f", statKnnSeconds), 10, textY += 16 );
		gr.drawString( String.format("KNN Found: %d", statKnnFound), 10, textY += 16 );
		gr.drawString( String.format("KNN Max: %.2f", statKnnMax), 10, textY += 16 );
	}

	@Override
	public void destroy()
	{
		balls.expire();
	}

	@Override
	public boolean isPlaying()
	{
		return playing;
	}
	
	@Override
	public void onCollisionStart()
	{
		statMutualCount = 0;
		statOnesideCount = 0;
		statTotalCollisions = 0;
	}

	@Override
	public void onCollision( SpatialEntity entity, SpatialEntity collidedWith, float overlap, int index, boolean second )
	{
		if (second)
		{
			statMutualCount++;
			statOnesideCount--;
		}
		else
		{
			statOnesideCount++;
		}
		
		statTotalCollisions++;
	}

	@Override
	public void onCollisionEnd()
	{
		
	}

	public static final Ellipse2D.Float ellipse = new Ellipse2D.Float();
	
	public class BouncyBall implements SpatialEntity, Entity
	{
		public final Vector position = new Vector();
		public final Vector velocity = new Vector();
		public final float radius;
		public final long spatialGroups;
		public final long spatialCollisionGroups;
		public boolean inert;
		public boolean dynamic;
		
		public BouncyBall(float radius, long spatialGroups, long spatialCollisionGroups, boolean dynamic)
		{
			this.radius = radius;
			this.spatialGroups = spatialGroups;
			this.spatialCollisionGroups = spatialCollisionGroups;
			this.inert = false;
			this.dynamic = dynamic;
		}
		
		@Override
		public Vector getPosition()
		{
			return position;
		}

		@Override
		public float getRadius()
		{
			return radius;
		}

		@Override
		public long getSpatialGroups()
		{
			return spatialGroups;
		}

		@Override
		public long getSpatialCollisionGroups()
		{
			return spatialCollisionGroups;
		}

		@Override
		public boolean isStatic()
		{
			return !dynamic;
		}

		@Override
		public boolean isInert()
		{
			return inert;
		}

		@Override
		public void update( GameState state, Scene scene )
		{
			if ( dynamic )
			{
				position.addsi( velocity, state.seconds );

				if ( position.x - radius < 0 && velocity.x < 0)
				{
					position.x = radius;
					velocity.x = -velocity.x;
				}
				if ( WIDTH - (position.x + radius) < 0 && velocity.x > 0 )
				{
					position.x = WIDTH - radius;
					velocity.x = -velocity.x;
				}
				if ( position.y - radius < 0 && velocity.y < 0)
				{
					position.y = radius;
					velocity.y = -velocity.y;
				}
				if ( HEIGHT - (position.y + radius) < 0 && velocity.y > 0 )
				{
					position.y = HEIGHT - radius;
					velocity.y = -velocity.y;
				}
			}
		}

		@Override
		public void draw( GameState state, Graphics2D gr, Scene scene )
		{
			ellipse.setFrame( position.x - radius, position.y - radius, radius * 2, radius * 2 );
			
			gr.draw( ellipse );
		}

		@Override
		public boolean isExpired()
		{
			return isInert();
		}

		@Override
		public void expire()
		{
			inert = true;
		}

		@Override
		public void onExpire()
		{
			
		}
		
	}

}
