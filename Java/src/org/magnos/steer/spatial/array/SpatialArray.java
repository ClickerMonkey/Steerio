
package org.magnos.steer.spatial.array;

import java.util.Arrays;

import org.magnos.steer.Vector;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;


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
	public int intersects( Vector offset, float radius, int max, long collidesWith, SearchCallback callback )
	{
		int intersectCount = 0;

		for (int j = 0; j < count; j++)
		{
			final SpatialEntity a = entities[j];

			if ((collidesWith & a.getSpatialGroups()) != 0)
			{
				final float overlap = getOverlap( a, offset, radius );

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

			if ((collidesWith & a.getSpatialGroups()) != 0 && contains( a, offset, radius ) && callback.onFound( a, 0, containCount, offset, radius, max, collidesWith ))
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
				final float overlap = -getOverlap( a, offset, 0 );
				int place = 0;

				while (place < near && overlap > distance[place])
				{
					place++;
				}

				if (place < k)
				{
					final int first = Math.max( 1, place );

					for (int i = near - 1; i > first; i--)
					{
						nearest[i] = nearest[i - 1];
						distance[i] = distance[i - 1];
					}

					nearest[place] = a;
					distance[place] = overlap;

					if (near < k)
					{
						near++;
					}
				}
			}
		}

		return near;
	}

	private float getOverlap( SpatialEntity a, Vector bpos, float bradius )
	{
		final Vector apos = a.getPosition();
		final float dx = apos.x - bpos.x;
		final float dy = apos.y - bpos.y;
		final float sr = a.getRadius() + bradius;
		return (sr * sr) - (dx * dx + dy * dy);
	}

	private boolean contains( SpatialEntity a, Vector bpos, float bradius )
	{
		final Vector apos = a.getPosition();
		final float dx = apos.x - bpos.x;
		final float dy = apos.y - bpos.y;
		final float mr = bradius - a.getRadius();
		return (dx * dx + dy * dy) <= (mr * mr);
	}

}
