
package org.magnos.steer.spatial;

import org.magnos.steer.vec.Vec;

/**
 * A search callback which keeps track of found entities in a bounded array.
 */
public class SearchCallbackArray<V extends Vec<V>> implements SearchCallback<V>
{

	public final int capacity;
	public final SpatialEntity<V>[] entity;
	public final float[] overlap;
	public int count;

	public SearchCallbackArray( int capacity )
	{
		this.capacity = capacity;
		this.entity = new SpatialEntity[capacity];
		this.overlap = new float[capacity];
	}

	public boolean onFound( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
		if (index >= capacity)
		{
			return false;
		}

		this.entity[index] = entity;
		this.overlap[index] = overlap;
		this.count = index + 1;
		
		return true;
	}
	
}
