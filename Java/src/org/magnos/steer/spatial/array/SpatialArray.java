
package org.magnos.steer.spatial.array;

import java.util.Arrays;
import java.util.Iterator;

import org.magnos.steer.spatial.CollisionCallback;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialUtility;
import org.magnos.steer.vec.Vec;


// A SpatialDatabase that uses Brute-Force
public class SpatialArray<V extends Vec<V>> implements SpatialDatabase<V>
{

	private SpatialEntity<V>[] entities;
	private int count;
	private SpatialArrayIterator iterator;
	    
	public SpatialArray( int initialCapacity )
	{
		this.entities = new SpatialEntity[initialCapacity];
		this.iterator = new SpatialArrayIterator();
	}
	
	@Override
	public Iterator<SpatialEntity<V>> iterator()
	{
	    return iterator.hasNext() ? new SpatialArrayIterator() : iterator.reset();
	}

	@Override
	public void add( SpatialEntity<V> entity )
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
			final SpatialEntity<V> e = entities[i];

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
	public int handleCollisions( CollisionCallback<V> callback )
	{
		int collisionCount = 0;
		
		callback.onCollisionStart();
		
		for (int j = 0; j < count - 1; j++)
		{
			final SpatialEntity<V> a = entities[j];
			
			if ( a.isInert() )
			{
				continue;
			}
			
			for (int k = j + 1; k < count; k++)
			{
				final SpatialEntity<V> b = entities[k];
				
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
	public int intersects( V offset, float radius, int max, long collidesWith, SearchCallback<V> callback )
	{
		int intersectCount = 0;

		for (int j = 0; j < count; j++)
		{
			final SpatialEntity<V> a = entities[j];

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
	public int contains( V offset, float radius, int max, long collidesWith, SearchCallback<V> callback )
	{
		int containCount = 0;

		for (int j = 0; j < count; j++)
		{
			final SpatialEntity<V> a = entities[j];

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
	public int knn( V offset, int k, long collidesWith, SpatialEntity<V>[] nearest, float[] distance )
	{
		if (count == 0 || !SpatialUtility.prepareKnn( k, nearest, distance ))
		{
			return 0;
		}
		
		int near = 0;

		for (int j = 0; j < count; j++)
		{
			final SpatialEntity<V> a = entities[j];

			if (!a.isInert() && (collidesWith & a.getSpatialGroups()) != 0)
			{
				near = SpatialUtility.accumulateKnn( SpatialUtility.distance( a, offset ), a, near, k, distance, nearest );
			}
		}

		return near;
	}
	
	private class SpatialArrayIterator implements Iterator<SpatialEntity<V>>
	{
	    
	    private int index;
	    private boolean removed;
	    
	    public SpatialArrayIterator()
	    {
	        index = Integer.MAX_VALUE;
	        removed = false;
	    }
	    
	    public SpatialArrayIterator reset()
	    {
	        index = 0;
	        removed = false;
	        
	        return this;
	    }

        @Override
        public boolean hasNext()
        {
            return index < count;
        }

        @Override
        public SpatialEntity<V> next()
        {
            removed = false;
            
            return entities[ index++ ];
        }

        @Override
        public void remove()
        {
            if ( !removed )
            {
                index--;
                count--;
                entities[ index ] = entities[ count ];   
                removed = true;
            }
        }
	    
	}

}
