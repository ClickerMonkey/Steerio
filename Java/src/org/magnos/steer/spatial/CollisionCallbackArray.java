
package org.magnos.steer.spatial;

import org.magnos.steer.vec.Vec;

public class CollisionCallbackArray<V extends Vec<V>> implements CollisionCallback<V>
{

	public final int capacity;
	public final CollisionPair<V>[] pairs;
	public int count;

	public CollisionCallbackArray( int capacity )
	{
		this.capacity = capacity;
		this.pairs = new CollisionPair[capacity];

		for (int i = 0; i < capacity; i++)
		{
			pairs[i] = new CollisionPair<V>();
		}
	}

	public void onCollisionStart()
	{
		count = 0;
	}

	public void onCollision( SpatialEntity<V> entity, SpatialEntity<V> collidedWith, float overlap, int index, boolean second )
	{
		if (index < capacity)
		{
			if (second)
			{
				pairs[index].mutual = true;
			}
			else
			{
				pairs[index].set( entity, collidedWith, overlap, false );
				count = index + 1;
			}
		}
	}

	public void onCollisionEnd()
	{

	}

}
