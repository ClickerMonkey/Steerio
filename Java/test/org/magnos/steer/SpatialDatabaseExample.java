package org.magnos.steer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.Queue;

import org.magnos.steer.spatial.CollisionCallback;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.array.SpatialArray;
import org.magnos.steer.spatial.dual.SpatialDualNode;
import org.magnos.steer.spatial.dual.SpatialDualTree;
import org.magnos.steer.spatial.grid.SpatialGrid;
import org.magnos.steer.spatial.grid.SpatialGridCell;
import org.magnos.steer.spatial.quad.SpatialQuadNode;
import org.magnos.steer.spatial.quad.SpatialQuadTree;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.core.Entity;
import com.gameprogblog.engine.core.EntityList;
import com.gameprogblog.engine.input.GameInput;


public class SpatialDatabaseExample implements Game, CollisionCallback, SearchCallback
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
	public static final int GRID_SIZE = 32;
	public static final float RADIUS_MIN = 0.5f;
	public static final float RADIUS_MAX = 4.0f;
	public static final long GROUP_MIN = 0;
	public static final long GROUP_MAX = 15;
	public static final float VELOCITY_MAX = 100.0f;
	public static final float CONTAIN_RADIUS = 100.0f;
	public static final float INTERSECT_RADIUS = 100.0f;
	
	public static final Ellipse2D.Float ellipse = new Ellipse2D.Float();
	public static final Line2D.Float line = new Line2D.Float();
	public static final Rectangle2D.Float rect = new Rectangle2D.Float();
	
	public EntityList<BouncyBall> balls;
	public SpatialDatabase database;
	public int ballCount;
	public int knn;
	public SpatialEntity[] knnNeighbors;
	public float[] knnOverlap;
	public boolean playing;
	public Vector mouse = new Vector();
	
	public boolean viewDatabase = true;
	public boolean viewCollision = true;
	public boolean viewKnn = false;
	public boolean viewHelp = false;
	public boolean viewBalls = true;
	public boolean viewAccuracy = true;
	public boolean viewContains = false;
	public boolean viewIntersections = false;
	
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

	public long statContainsStartNanos;
	public long statContainsEndNanos;
	public double statContainsSeconds;
	public int statContainsCount;

	public long statIntersectStartNanos;
	public long statIntersectEndNanos;
	public double statIntersectSeconds;
	public int statIntersectCount;
	
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
	
	private void rebuildDatabase(SpatialDatabase newDatabase)
	{
		database = newDatabase;
		
		for (int i = 0; i < balls.size(); i++)
		{
			database.add( balls.get(i) );
		}
		
		System.out.println( "SpatialDatabase changed to " + newDatabase.getClass().getSimpleName() );
	}

	@Override
	public void input( GameInput input )
	{
		if (input.keyDown[KeyEvent.VK_ESCAPE])
		{
			playing = false;
		}

		if (input.keyUp[KeyEvent.VK_H])
		{
			viewHelp = !viewHelp;
		}
		if (input.keyUp[KeyEvent.VK_F1])
		{
			viewDatabase = !viewDatabase;
		}
		if (input.keyUp[KeyEvent.VK_F2])
		{
			viewCollision = !viewCollision;
		}
		if (input.keyUp[KeyEvent.VK_F3])
		{
			viewKnn = !viewKnn;
		}
		if (input.keyUp[KeyEvent.VK_F4])
		{
			viewBalls = !viewBalls;
		}
		if (input.keyUp[KeyEvent.VK_F5])
		{
			viewAccuracy = !viewAccuracy;
		}
		if (input.keyUp[KeyEvent.VK_F6])
		{
			viewIntersections = !viewIntersections;
		}
		if (input.keyUp[KeyEvent.VK_F7])
		{
			viewContains = !viewContains;
		}
		
		if (input.keyUp[KeyEvent.VK_1])
		{
			rebuildDatabase( new SpatialArray( ballCount ) );
		}
		
		if (input.keyUp[KeyEvent.VK_2])
		{
			rebuildDatabase( new SpatialGrid( WIDTH / GRID_SIZE, HEIGHT / GRID_SIZE, GRID_SIZE, GRID_SIZE, 0, 0 ) );
		}
		
		if (input.keyUp[KeyEvent.VK_3])
		{
			rebuildDatabase( new SpatialQuadTree( 0, 0, WIDTH, HEIGHT, 8, 30 ) );
		}
		
		if (input.keyUp[KeyEvent.VK_4])
		{
			rebuildDatabase( new SpatialDualTree( 0, 0, WIDTH, HEIGHT, 8, 30 ) );
		}
		
		if (input.keyUp[KeyEvent.VK_UP])
		{
			ballCount <<= 1;
			fill();
		}
		if (input.keyUp[KeyEvent.VK_DOWN] && (ballCount >> 1) > 0)
		{
			ballCount >>= 1;
			fill();
		}
		
		mouse.set( input.mouseX, input.mouseY );
	}

	@Override
	public void update( GameState state, Scene scene )
	{
		balls.update( state, scene );
		
		database.refresh();
		
		if ( viewCollision )
		{
			statCollisionStartNanos = System.nanoTime();
			statUniqueCollisions = database.handleCollisions( this );
			statCollisionEndNanos = System.nanoTime();
			statCollisionSeconds = (statCollisionEndNanos - statCollisionStartNanos) * 0.000000001;	
		}
	}

	@Override
	public void draw( GameState state, final Graphics2D gr, Scene scene )
	{
		if ( viewDatabase )
		{
			gr.setColor( Color.lightGray );
			
			if ( database instanceof SpatialGrid )
			{
				SpatialGrid grid = (SpatialGrid)database;
				
				for (int y = 0; y < grid.height; y++)
				{
					for (int x = 0; x < grid.width; x++)
					{
						SpatialGridCell cell = grid.cells[y][x];
						
						rect.setFrameFromDiagonal( cell.l, cell.t, cell.r, cell.b );
						gr.draw( rect );
						
						if ( cell.lookbackX != 0 || cell.lookbackY != 0 )
						{
							gr.drawString( String.format("{%d,%d}", cell.lookbackX, cell.lookbackY ), cell.l + 2, cell.t + 14 );	
						}
					}
				}
			}
			if ( database instanceof SpatialQuadTree )
			{
				SpatialQuadTree quad = (SpatialQuadTree)database;
				
				Queue<SpatialQuadNode> nodes = new ArrayDeque<SpatialQuadNode>();
				nodes.add( quad.root );
				
				while (!nodes.isEmpty())
				{
					SpatialQuadNode n = nodes.poll();

					rect.setFrameFromDiagonal( n.l, n.t, n.r, n.b );
					gr.draw( rect );
					gr.drawString( String.format("%d", n.size), (n.l + n.r) * 0.5f + 2, (n.t + n.b) * 0.5f + 14 );
					
					if ( n.isBranch() )
					{
						nodes.add( n.children[0] );
						nodes.add( n.children[1] );
						nodes.add( n.children[2] );
						nodes.add( n.children[3] );
					}
				}
			}
			if ( database instanceof SpatialDualTree )
			{
				SpatialDualTree dual = (SpatialDualTree)database;
				
				Queue<SpatialDualNode> nodes = new ArrayDeque<SpatialDualNode>();
				nodes.add( dual.root );
				
				while (!nodes.isEmpty())
				{
					SpatialDualNode n = nodes.poll();

					rect.setFrameFromDiagonal( n.l, n.t, n.r, n.b );
					gr.draw( rect );
					gr.drawString( String.format("%d", n.size), (n.l + n.r) * 0.5f + 2, (n.t + n.b) * 0.5f + 14 );
					
					if ( n.isBranch() )
					{
						nodes.add( n.tl );
						nodes.add( n.br );
					}
				}
			}
		}
		
		if ( viewBalls )
		{
			gr.setColor( Color.green );
			balls.draw( state, gr, scene );
		}

		if ( viewKnn )
		{
			statKnnStartNanos = System.nanoTime();
			statKnnFound = database.knn( mouse, knn, SpatialDatabase.ALL_GROUPS, knnNeighbors, knnOverlap );
			statKnnEndNanos = System.nanoTime();
			statKnnSeconds = (statKnnEndNanos - statKnnStartNanos) * 0.000000001;
			
			gr.setColor( Color.darkGray );
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
		}
		
		if ( viewContains )
		{
			gr.setColor( Color.darkGray );
			line.x1 = mouse.x;
			line.y1 = mouse.y;
			
			ellipse.setFrameFromDiagonal( mouse.x - CONTAIN_RADIUS, mouse.y - CONTAIN_RADIUS, mouse.x + CONTAIN_RADIUS, mouse.y + CONTAIN_RADIUS );
			gr.draw( ellipse );
			
			statContainsStartNanos = System.nanoTime();
			statContainsCount = database.contains( mouse, CONTAIN_RADIUS, Integer.MAX_VALUE, SpatialDatabase.ALL_GROUPS, new SearchCallback() {
				public boolean onFound( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups ) {
					line.x2 = entity.getPosition().x;
					line.y2 = entity.getPosition().y;
					gr.draw( line );
					return true;
				}
			});
			statContainsEndNanos = System.nanoTime();
			statContainsSeconds = (statContainsEndNanos - statContainsStartNanos) * 0.000000001;
		}
		
		if ( viewIntersections )
		{
			gr.setColor( Color.darkGray );
			line.x1 = mouse.x;
			line.y1 = mouse.y;
			
			ellipse.setFrameFromDiagonal( mouse.x - INTERSECT_RADIUS, mouse.y - INTERSECT_RADIUS, mouse.x + INTERSECT_RADIUS, mouse.y + INTERSECT_RADIUS );
			gr.draw( ellipse );
			
			statIntersectStartNanos = System.nanoTime();
			statIntersectCount = database.intersects( mouse, INTERSECT_RADIUS, Integer.MAX_VALUE, SpatialDatabase.ALL_GROUPS, new SearchCallback() {
				public boolean onFound( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups ) {
					line.x2 = entity.getPosition().x;
					line.y2 = entity.getPosition().y;
					gr.draw( line );
					return true;
				}
			});
			statIntersectEndNanos = System.nanoTime();
			statIntersectSeconds = (statIntersectEndNanos - statIntersectStartNanos) * 0.000000001;
		}
		
		int textY = 4;
		gr.setColor( Color.white );
		
		gr.drawString( "Help [H]", 10, textY += 16 );
		
		if ( viewHelp )
		{
			gr.drawString( "View Database Debugging [F1]", 10, textY += 16 );
			gr.drawString( "View Collision Stats [F2]", 10, textY += 16 );
			gr.drawString( "View KNN Stats [F3]", 10, textY += 16 );
			gr.drawString( "View Balls [F4]", 10, textY += 16 );
			gr.drawString( "View Accuracy [F5]", 10, textY += 16 );
			gr.drawString( "View Intersections [F6]", 10, textY += 16 );
			gr.drawString( "View Contains [F7]", 10, textY += 16 );
		}
		
		gr.drawString( String.format("Balls [UP/DOWN]: %d", ballCount), 10, textY += 16 );
		gr.drawString( String.format("Database [1-4]: %s", database.getClass().getSimpleName()), 10, textY += 16 );
		
		if ( viewCollision )
		{
			gr.drawString( String.format("Unique: %d", statUniqueCollisions), 10, textY += 16 );
			gr.drawString( String.format("Total: %d", statTotalCollisions), 10, textY += 16 );
			gr.drawString( String.format("Mutual: %d", statMutualCount), 10, textY += 16 );
			gr.drawString( String.format("One-sided: %d", statOnesideCount), 10, textY += 16 );
			gr.drawString( String.format("Collision Elapsed: %.9f", statCollisionSeconds), 10, textY += 16 );
			gr.drawString( String.format("Collision Per-second: %d", (long)(1.0 / (statCollisionSeconds / ballCount))), 10, textY += 16 );	
			
			// Compare performance and accuracy against SpatialArray (brute-force)
			if ( viewAccuracy && !(database instanceof SpatialArray) )
			{
				SpatialArray array = new SpatialArray( balls.size() );
				
				for (int i = 0; i < balls.size(); i++)
				{
					array.add( balls.get(i) );
				}
				
				int unique = statUniqueCollisions;
				int total = statTotalCollisions;
				int mutual = statMutualCount;
				int onesided = statOnesideCount;
				
				long startTime = System.nanoTime();
				array.handleCollisions( this );
				long endTime = System.nanoTime();
				double elapsed = (endTime - startTime) * 0.000000001;
				
				StringBuilder mismatches = new StringBuilder();

				if ( unique != statUniqueCollisions )
				{
					mismatches.append("unique(A=").append( statUniqueCollisions ).append( ",E=" ).append( unique ).append( ") ");
				}
				if ( total != statTotalCollisions )
				{
					mismatches.append("total(A=").append( statTotalCollisions ).append( ",E=" ).append( total ).append( ") ");
				}
				if ( mutual != statMutualCount )
				{
					mismatches.append("mutual(A=").append( statMutualCount ).append( ",E=" ).append( mutual ).append( ") ");
				}
				if ( onesided != statOnesideCount )
				{
					mismatches.append("onesided(A=").append( statOnesideCount ).append( ",E=" ).append( onesided ).append( ") ");
				}
				
				gr.drawString( String.format( "Mismatches: %s", mismatches ), 10, textY += 16 );
				gr.drawString( String.format( "%.2f times faster than brute-force ", elapsed / statCollisionSeconds), 10, textY += 16 );
			}
		}
		
		if ( viewKnn )
		{
			gr.drawString( String.format("KNN Elapsed: %.9f", statKnnSeconds), 10, textY += 16 );
			gr.drawString( String.format("KNN Found: %d", statKnnFound), 10, textY += 16 );
			gr.drawString( String.format("KNN Max: %.2f", statKnnMax), 10, textY += 16 );
			
			// Compare performance and accuracy against SpatialArray (brute-force)
			if ( viewAccuracy && !(database instanceof SpatialArray) )
			{
				SpatialArray array = new SpatialArray( balls.size() );
				
				for (int i = 0; i < balls.size(); i++)
				{
					array.add( balls.get(i) );
				}
				
				SpatialEntity[] neighbors = new SpatialEntity[knn];
				float[] overlap = new float[knn];
				
				long startTime = System.nanoTime();
				int found = database.knn( mouse, knn, SpatialDatabase.ALL_GROUPS, neighbors, overlap );
				long endTime = System.nanoTime();
				double elapsed = (endTime - startTime) * 0.000000001;
				
				StringBuilder mismatches = new StringBuilder();
				
				if ( found != statKnnFound ) 
				{
					mismatches.append("found(A=").append( statKnnFound ).append( ",E=" ).append( found ).append( ") ");
				}
				else
				{
					int mismatchCount = 0;
					
					for (int i = 0; i < found; i++)
					{
						if ( neighbors[i] != knnNeighbors[i] )
						{
							mismatchCount++;
						}
						if ( overlap[i] != knnOverlap[i] )
						{
							mismatchCount++;
						}
					}
					
					if (mismatchCount > 0)
					{
						mismatches.append("inequals(").append( mismatchCount ).append( ") " );
					}
				}

				gr.drawString( String.format( "Mismatches: %s", mismatches ), 10, textY += 16 );
				gr.drawString( String.format( "%.2f times faster than brute-force ", elapsed / statKnnSeconds), 10, textY += 16 );
			}
		}
		
		if ( viewIntersections )
		{
			gr.drawString( String.format("Intersection Elapsed: %.9f", statIntersectSeconds), 10, textY += 16 );
			gr.drawString( String.format("Intersection Found: %d", statIntersectCount), 10, textY += 16 );
			
			// Compare performance and accuracy against SpatialArray (brute-force)
			if ( viewAccuracy && !(database instanceof SpatialArray) )
			{
				SpatialArray array = new SpatialArray( balls.size() );
				
				for (int i = 0; i < balls.size(); i++)
				{
					array.add( balls.get(i) );
				}
				
				long startTime = System.nanoTime();
				int intersectCount = array.intersects( mouse, INTERSECT_RADIUS, Integer.MAX_VALUE, SpatialDatabase.ALL_GROUPS, new SearchCallback() {
					public boolean onFound( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups ) {
						return true;
					}
				});
				long endTime = System.nanoTime();
				double elapsed = (endTime - startTime) * 0.000000001;
				
				StringBuilder mismatches = new StringBuilder();

				if ( intersectCount != statIntersectCount )
				{
					mismatches.append("total(A=").append( statIntersectCount ).append( ",E=" ).append( intersectCount ).append( ") ");
				}

				gr.drawString( String.format( "Mismatches: %s", mismatches ), 10, textY += 16 );
				gr.drawString( String.format( "%.2f times faster than brute-force ", elapsed / statIntersectSeconds), 10, textY += 16 );
			}
		}
		
		if ( viewContains )
		{
			gr.drawString( String.format("Contains Elapsed: %.9f", statContainsSeconds), 10, textY += 16 );
			gr.drawString( String.format("Contains Found: %d", statContainsCount), 10, textY += 16 );
			
			// Compare performance and accuracy against SpatialArray (brute-force)
			if ( viewAccuracy && !(database instanceof SpatialArray) )
			{
				SpatialArray array = new SpatialArray( balls.size() );
				
				for (int i = 0; i < balls.size(); i++)
				{
					array.add( balls.get(i) );
				}
				
				long startTime = System.nanoTime();
				int containCount = array.contains( mouse, CONTAIN_RADIUS, Integer.MAX_VALUE, SpatialDatabase.ALL_GROUPS, new SearchCallback() {
					public boolean onFound( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups ) {
						return true;
					}
				});
				long endTime = System.nanoTime();
				double elapsed = (endTime - startTime) * 0.000000001;
				
				StringBuilder mismatches = new StringBuilder();

				if ( containCount != statContainsCount )
				{
					mismatches.append("total(A=").append( statContainsCount ).append( ",E=" ).append( containCount ).append( ") ");
				}

				gr.drawString( String.format( "Mismatches: %s", mismatches ), 10, textY += 16 );
				gr.drawString( String.format( "%.2f times faster than brute-force ", elapsed / statContainsSeconds), 10, textY += 16 );
			}
		}
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

	@Override
	public boolean onFound( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
		// TODO Auto-generated method stub
		return false;
	}

}
