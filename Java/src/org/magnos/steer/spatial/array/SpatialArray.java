
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
			
			for (int k = j + 1; k < count; k++)
			{
				final SpatialEntity b = entities[k];
				
				// Based on their groups, determine if they are applicable for collision
				final boolean applicableA = (a.getSpatialCollisionGroups() & b.getSpatialGroups()) != 0;
				final boolean applicableB = (b.getSpatialCollisionGroups() & a.getSpatialGroups()) != 0;
				
				// At least one needs to be...
				if ( applicableA || applicableB )
				{
					// Calculate overlap
					final float overlap = SpatialUtility.getOverlap( a, b.getPosition(), b.getRadius() );
					
					// If they are intersecting...
					if ( overlap > 0 )
					{
						// If they both can intersect with each other, make sure to 
						// let the callback know that it's a duplicate collision
						// notification, it's just going the other way.
						boolean second = false;
						
						// If A can collide with B, notify A of a collision.
						if ( applicableA )
						{
							callback.onCollision( a, b, overlap, collisionCount, second );
							second = true;
						}
						
						// If B can collide with A, notify B of a collision
						if ( applicableB )
						{
							callback.onCollision( b, a, overlap, collisionCount, second );
						}
						
						collisionCount++;
					}
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

			if ((collidesWith & a.getSpatialGroups()) != 0)
			{
				final float overlap = SpatialUtility.getOverlap( a, offset, radius );

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

			if ((collidesWith & a.getSpatialGroups()) != 0 && 
				 SpatialUtility.contains( a, offset, radius ) && 
				 callback.onFound( a, 0, containCount, offset, radius, max, collidesWith ))
			{
				containCount++;

				if (containCount >= max)
				{
					break;
				}
			}
		}

		return containCount;
	}

	@Override
	public int knn( Vector offset, int k, long collidesWith, SpatialEntity[] nearest, float[] distance )
	{
		if (count == 0 || k == 0 || k > nearest.length || k > distance.length)
		{
			return 0;
		}

		for (int i = 0; i < k; i++)
		{
			nearest[i] = null;
			distance[i] = Float.MAX_VALUE;
		}

		int near = 0;

		for (int j = 0; j < count; j++)
		{
			final SpatialEntity a = entities[j];

			if ((collidesWith & a.getSpatialGroups()) != 0)
			{
				final float overlap = -SpatialUtility.getOverlap( a, offset, 0 );
				
				near = SpatialUtility.accumulateKnn( overlap, a, near, k, distance, nearest );
			}
		}

		return near;
	}

}
