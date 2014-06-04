package org.magnos.steer.spatial;

import java.util.ArrayList;
import java.util.List;

import org.magnos.steer.SteerMath;
import org.magnos.steer.spatial.sap.SpatialSweepAndPrune;
import org.magnos.steer.test.Timer;
import org.magnos.steer.vec.Vec2;


public class SpatialDatabasePerformance
{
	
	public static void main(String[] args)
	{
		SpatialDatabasePerformance p = new SpatialDatabasePerformance();
		
//		SpatialDatabase db = new SpatialGrid( 64, 64, 200, 200, 0, 0 );
//		SpatialDatabase db = new SpatialArray( 10000 );
//		SpatialDatabase db = new SpatialQuadTree( 0, 0, 12800, 12800, 16, 10 );
//		SpatialDatabase db = new SpatialDualTree( 0, 0, 12800, 12800, 16, 10 );
		SpatialDatabase<Vec2> db = new SpatialSweepAndPrune();
		
		List<BouncyBall> balls = p.createBalls( 10000, 0, 12800, 0, 12800, 100, 0.5f, 2.0f, 0, 15, 95 );
		
		p.testSeveral( db, balls, 0.01f, 100, 10, 1000 );
	}

	public void testSeveral( SpatialDatabase<Vec2> database, List<BouncyBall> balls, float elapsed, int iterations, int warmupIterations, int warmupRefreshes)
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
	
	public void testSingle( SpatialDatabase<Vec2> database, List<BouncyBall> balls, float elapsed, int warmupRefreshes, int iterations, Timer refreshTimer, Timer collisionTimer )
	{
		for ( BouncyBall ball : balls )
		{
			database.add( ball );
		}
		
		for ( int i = 0; i < warmupRefreshes; i++ )
		{
			database.refresh();
		}
		
		CollisionCallback<Vec2> callback = new EmptyCollisionCallback();
		
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
		Vec2 min = new Vec2( minX, minY );
		Vec2 max = new Vec2( maxX, maxY );
		
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
	
	public class BouncyBall implements SpatialEntity<Vec2>
	{
		public final Vec2 position = new Vec2();
		public final Vec2 velocity = new Vec2();
		public final Vec2 min, max;
		public final float radius;
		public final long spatialGroups;
		public final long spatialCollisionGroups;
		public boolean inert;
		public boolean dynamic;
		
		public BouncyBall(float radius, long spatialGroups, long spatialCollisionGroups, boolean dynamic, Vec2 min, Vec2 max)
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
		public Vec2 getPosition()
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

        @Override
        public float getDistanceAndNormal( Vec2 origin, Vec2 lookahead, Vec2 outNormal )
        {
            return 0;
        }

        @Override
        public Vec2 getPosition( Vec2 out )
        {
            return out.set( position );
        }

        @Override
        public void attach( Object attachment )
        {
            
        }

        @Override
        public <T> T attachment()
        {
            return null;
        }

        @Override
        public <T> T attachment( Class<T> type )
        {
            return null;
        }
	}
	
	public class EmptyCollisionCallback implements CollisionCallback<Vec2>
	{
		@Override
		public void onCollisionStart()
		{
			
		}

		@Override
		public void onCollision( SpatialEntity<Vec2> entity, SpatialEntity<Vec2> collidedWith, float overlap, int index, boolean second )
		{
			
		}

		@Override
		public void onCollisionEnd()
		{
			
		}
	}
	
}
