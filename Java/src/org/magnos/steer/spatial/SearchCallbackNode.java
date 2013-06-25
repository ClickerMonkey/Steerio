
package org.magnos.steer.spatial;

/**
 * A node which is used by SearchCallbackChain.
 */
public class SearchCallbackNode
{

	public final SpatialEntity entity;
	public final float overlap;
	public final int index;
	public final SearchCallbackNode next;

	public SearchCallbackNode( SpatialEntity entity, float overlap, int index, SearchCallbackNode next )
	{
		this.entity = entity;
		this.overlap = overlap;
		this.index = index;
		this.next = next;
	}
}
