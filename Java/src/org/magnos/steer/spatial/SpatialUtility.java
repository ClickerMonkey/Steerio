package org.magnos.steer.spatial;

import org.magnos.steer.Vector;


public class SpatialUtility
{

	public static boolean prepareKnn( int k, SpatialEntity[] nearest, float[] distance )
	{
		if (k == 0 || k > nearest.length || k > distance.length)
		{
			return false;
		}
		
		for (int i = 0; i < k; i++)
		{
			nearest[i] = null;
			distance[i] = Float.MAX_VALUE;
		}
		
		return true;
	}

	public static int accumulateKnn(float overlap, SpatialEntity a, int near, int k, float[] distance, SpatialEntity[] nearest)
	{
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
		
		return near;
	}
	
	public static float getOverlap( SpatialEntity a, Vector bpos, float bradius )
	{
		final Vector apos = a.getPosition();
		final float dx = apos.x - bpos.x;
		final float dy = apos.y - bpos.y;
		final float sr = a.getRadius() + bradius;
		return (sr * sr) - (dx * dx + dy * dy);
	}

	public static boolean contains( SpatialEntity a, Vector bpos, float bradius )
	{
		final Vector apos = a.getPosition();
		final float dx = apos.x - bpos.x;
		final float dy = apos.y - bpos.y;
		final float mr = bradius - a.getRadius();
		return (dx * dx + dy * dy) <= (mr * mr);
	}

	public static int floor( float amount, int min, int max )
	{
		int v = (int)Math.floor( amount );
		if (v < min) v = min;
		if (v > max) v = max;
		return v;
	}

	public static int ceil( float amount, int min, int max )
	{
		int v = (int)Math.ceil( amount );
		if (v < min) v = min;
		if (v > max) v = max;
		return v;
	}
	
	public static int handleCollision( SpatialEntity a, SpatialEntity b, int index, CollisionCallback callback )
	{
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
					callback.onCollision( a, b, overlap, index, second );
					second = true;
				}
				
				// If B can collide with A, notify B of a collision
				if ( applicableB )
				{
					callback.onCollision( b, a, overlap, index, second );
				}
				
				index++;
			}
		}
		
		return index;
	}
	
}
