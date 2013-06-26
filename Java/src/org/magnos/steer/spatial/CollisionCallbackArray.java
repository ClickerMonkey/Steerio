
package org.magnos.steer.spatial;

public class CollisionCallbackArray implements CollisionCallback
{

	public final int capacity;
	public final CollisionPair[] pairs;
	public int count;

	public CollisionCallbackArray( int capacity )
	{
		this.capacity = capacity;
		this.pairs = new CollisionPair[capacity];

		for (int i = 0; i < capacity; i++)
		{
			pairs[i] = new CollisionPair();
		}
	}

	public void onCollisionStart()
	{
		count = 0;
	}

	public void onCollision( SpatialEntity entity, SpatialEntity collidedWith, float overlap, int index, boolean second )
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
