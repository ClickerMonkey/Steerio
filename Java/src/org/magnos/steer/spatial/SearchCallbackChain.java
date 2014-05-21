
package org.magnos.steer.spatial;

import org.magnos.steer.vec.Vec;

/**
 * A search callback which keeps track of found entities in a singly linked
 * list.
 */
public class SearchCallbackChain<V extends Vec<V>> implements SearchCallback<V>
{

	public SearchCallbackNode<V> head = null;

	public boolean onFound( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
		head = new SearchCallbackNode<V>( entity, overlap, index, head );

		return true;
	}

	public void clear()
	{
		head = null;
	}
}
