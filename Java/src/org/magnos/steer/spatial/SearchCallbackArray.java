
package org.magnos.steer.spatial;

import org.magnos.steer.Vector;

/**
 * A search callback which keeps track of found entities in a bounded array.
 */
public class SearchCallbackArray implements SearchCallback
{

	public final int capacity;
	public final SpatialEntity[] entity;
	public final float[] overlap;

	public SearchCallbackArray( int capacity )
	{
		this.capacity = capacity;
		this.entity = new SpatialEntity[capacity];
		this.overlap = new float[capacity];
	}

	public boolean onFound( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
		if (index >= capacity)
		{
			return false;
		}

		this.entity[index] = entity;
		this.overlap[index] = overlap;
		
		return true;
	}

}
