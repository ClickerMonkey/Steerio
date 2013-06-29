
package org.magnos.steer.spatial.array;

import java.util.Arrays;

import org.magnos.steer.Vector;
import org.magnos.steer.spatial.CollisionCallback;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialUtility;


// A SpatialDatabase that uses Brute-Force
public class SpatialArray implements SpatialDatabase
{

	private SpatialEntity[] entities;
	private int count;

	public SpatialArray( int initialCapacity )
	{
		this.entities = new SpatialEntity[initialCapacity];
	}

	@Override
	public void add( SpatialEntity entity )
	{
		if (count == entities.length)
		{
			entities = Arrays.copyOf( entities, count + (count >> 1) );
		}

		entities[count++] = entity;
	}
	
	@Override
	public void clear()
	{
		while (count > 0)
		{
			entities[--count] = null;
		}
	}

	@Override
	public int refresh()
	{
		int alive = 0;

		for (int i = 0; i < count; i++)
		{
			final SpatialEntity e = entities[i];

			if (!e.isInert())
			{
				entities[alive++] = e;
			}
		}

		while (count > alive)
		{
			entities[--count] = null;
		}

		return alive;
	}

	@Override
	public int handleCollisions( CollisionCallback callback )
	{
		int collisionCount = 0;
		
		callback.onCollisionStart();
		
		for (int j = 0; j < count - 1; j++)
		{
			final SpatialEntity a = entities[j];
			
			if ( a.isInert() )
			{
				continue;
			}
			
			for (int k = j + 1; k < count; k++)
			{
				final SpatialEntity b = entities[k];
				
				if ( b.isInert() )
				{
					continue;
				}
				
				collisionCount += SpatialUtility.handleCollision( a, b, collisionCount, callback );
				
				if ( a.isInert() )
				{
					break;
				}
			}
		}
		
		callback.onCollisionEnd();
		
		return collisionCount;
	}
	
	@Override
	public int intersects( Vector offset, float radius, int max, long collidesWith, SearchCallback callback )
	{
		int intersectCount = 0;

		for (int j = 0; j < count; j++)
		{
			final SpatialEntity a = entities[j];

			if (!a.isInert() && (collidesWith & a.getSpatialGroups()) != 0)
			{
				final float overlap = SpatialUtility.overlap( a, offset, radius );

				if (overlap > 0 && callback.onFound( a, overlap, intersectCount, offset, radius, max, collidesWith ))
				{
					intersectCount++;

					if (intersectCount >= max)
					{
						break;
					}
				}
			}
		}

		return intersectCount;
	}

	@Override
	public int contains( Vector offset, float radius, int max, long collidesWith, SearchCallback callback )
	{
		int containCount = 0;

		for (int j = 0; j < count; j++)
		{
			final SpatialEntity a = entities[j];

			if (!a.isInert() && (collidesWith & a.getSpatialGroups()) != 0)
			{
				final float aradius2 = a.getRadius() * 2;
				final float overlap = SpatialUtility.overlap( a, offset, radius );
				
				if (overlap >= aradius2)
				{
					if (callback.onFound( a, radius - overlap, containCount, offset, radius, max, collidesWith ))
					{
						containCount++;

						if (containCount >= max)
						{
							break;
						}
					}
				}
			}
		}

		return containCount;
	}

	@Override
	public int knn( Vector offset, int k, long collidesWith, SpatialEntity[] nearest, float[] distance )
	{
		if (count == 0 || !SpatialUtility.prepareKnn( k, nearest, distance ))
		{
			return 0;
		}
		
		int near = 0;

		for (int j = 0; j < count; j++)
		{
			final SpatialEntity a = entities[j];

			if (!a.isInert() && (collidesWith & a.getSpatialGroups()) != 0)
			{
				near = SpatialUtility.accumulateKnn( SpatialUtility.distance( a, offset ), a, near, k, distance, nearest );
			}
		}

		return near;
	}

}
