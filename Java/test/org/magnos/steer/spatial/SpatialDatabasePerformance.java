package org.magnos.steer.spatial;

import java.util.ArrayList;
import java.util.List;

import org.magnos.steer.SteerMath;
import org.magnos.steer.Vector;
import org.magnos.steer.spatial.sap.SpatialSweepAndPrune;
import org.magnos.steer.test.Timer;


public class SpatialDatabasePerformance
{
	
	public static void main(String[] args)
	{
		SpatialDatabasePerformance p = new SpatialDatabasePerformance();
		
//		SpatialDatabase db = new SpatialGrid( 64, 64, 200, 200, 0, 0 );
//		SpatialDatabase db = new SpatialArray( 10000 );
//		SpatialDatabase db = new SpatialQuadTree( 0, 0, 12800, 12800, 16, 10 );
//		SpatialDatabase db = new SpatialDualTree( 0, 0, 12800, 12800, 16, 10 );
		SpatialDatabase db = new SpatialSweepAndPrune();
		
		List<BouncyBall> balls = p.createBalls( 10000, 0, 12800, 0, 12800, 100, 0.5f, 2.0f, 0, 15, 95 );
		
		p.testSeveral( db, balls, 0.01f, 100, 10, 1000 );
	}

	public void testSeveral( SpatialDatabase database, List<BouncyBall> balls, float elapsed, int iterations, int warmupIterations, int warmupRefreshes)
	{
		Timer refreshTimer = new Timer();
		Timer collisionTimer = new Timer();
		
		for (int i = 0; i < warmupIterations; i++)
		{
			refreshTimer.reset();
			collisionTimer.reset();
			
			database.clear();
			
			testSingle( database, balls, elapsed, warmupRefreshes, iterations, refreshTimer, collisionTimer );
		}
		
		System.out.println( "Refresh Statistics" );
		refreshTimer.print();
		System.out.println( "Collision Statistics" );
		collisionTimer.print();
	}
	
	public void testSingle( SpatialDatabase database, List<BouncyBall> balls, float elapsed, int warmupRefreshes, int iterations, Timer refreshTimer, Timer collisionTimer )
	{
		for ( BouncyBall ball : balls )
		{
			database.add( ball );
		}
		
		for ( int i = 0; i < warmupRefreshes; i++ )
		{
			database.refresh();
		}
		
		CollisionCallback callback = new EmptyCollisionCallback();
		
		for (int i = 0; i < iterations; i++)
		{
			for (BouncyBall ball : balls)
			{
				ball.update( elapsed );
			}

			refreshTimer.start();
			database.refresh();
			refreshTimer.stop();
			
			collisionTimer.start();
			database.handleCollisions( callback );
			collisionTimer.stop();
		}
	}
	
	public List<BouncyBall> createBalls( int count, float minX, float maxX, float minY, float maxY, float maxVelocity, float minRadius, float maxRadius, long minGroups, long maxGroups, int percentStatic )
	{
		List<BouncyBall> balls = new ArrayList<BouncyBall>();
		Vector min = new Vector( minX, minY );
		Vector max = new Vector( maxX, maxY );
		
		for (int i = 0; i < count; i++)
		{
			float radius = SteerMath.randomFloat( minRadius, maxRadius );
			long spatialGroups = SteerMath.randomLong( minGroups, maxGroups );
			long spatialCollisionGroups = SteerMath.randomLong( minGroups, maxGroups );
			boolean dynamic = SteerMath.randomInt( 100 ) > percentStatic;
			
			BouncyBall b = new BouncyBall( radius, spatialGroups, spatialCollisionGroups, dynamic, min, max );
			b.position.x = SteerMath.randomFloat( minX, maxX );
			b.position.y = SteerMath.randomFloat( minY, maxY );
			b.velocity.x = SteerMath.randomFloat( -maxVelocity, maxVelocity );
			b.velocity.y = SteerMath.randomFloat( -maxVelocity, maxVelocity );
			
			balls.add( b );
		}
		
		return balls;
	}
	
	public class BouncyBall implements SpatialEntity
	{
		public final Vector position = new Vector();
		public final Vector velocity = new Vector();
		public final Vector min, max;
		public final float radius;
		public final long spatialGroups;
		public final long spatialCollisionGroups;
		public boolean inert;
		public boolean dynamic;
		
		public BouncyBall(float radius, long spatialGroups, long spatialCollisionGroups, boolean dynamic, Vector min, Vector max)
		{
			this.radius = radius;
			this.spatialGroups = spatialGroups;
			this.spatialCollisionGroups = spatialCollisionGroups;
			this.inert = false;
			this.dynamic = dynamic;
			this.min = min;
			this.max = max;
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

		public void update( float elapsed )
		{
			if ( dynamic )
			{
				position.addsi( velocity, elapsed );

				if ( position.x - radius < min.x && velocity.x < 0)
				{
					position.x = min.x + radius;
					velocity.x = -velocity.x;
				}
				if ( position.x + radius > max.x && velocity.x > 0 )
				{
					position.x = max.x - radius;
					velocity.x = -velocity.x;
				}
				if ( position.y - radius < min.y && velocity.y < 0)
				{
					position.y = min.y + radius;
					velocity.y = -velocity.y;
				}
				if ( position.y + radius > max.y && velocity.y > 0 )
				{
					position.y = max.y - radius;
					velocity.y = -velocity.y;
				}
			}
		}
	}
	
	public class EmptyCollisionCallback implements CollisionCallback
	{
		@Override
		public void onCollisionStart()
		{
			
		}

		@Override
		public void onCollision( SpatialEntity entity, SpatialEntity collidedWith, float overlap, int index, boolean second )
		{
			
		}

		@Override
		public void onCollisionEnd()
		{
			
		}
	}
	
}
