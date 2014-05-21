
package org.magnos.steer.spatial;

import org.magnos.steer.vec.Vec;

/**
 * A node which is used by SearchCallbackChain.
 */
public class SearchCallbackNode<V extends Vec<V>>
{

	public final SpatialEntity<V> entity;
	public final float overlap;
	public final int index;
	public final SearchCallbackNode<V> next;

	public SearchCallbackNode( SpatialEntity<V> entity, float overlap, int index, SearchCallbackNode<V> next )
	{
		this.entity = entity;
		this.overlap = overlap;
		this.index = index;
		this.next = next;
	}
}
