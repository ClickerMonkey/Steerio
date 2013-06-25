
package org.magnos.steer.spatial;

import org.magnos.steer.Vector;

/**
 * A search callback which keeps track of found entities in a singly linked
 * list.
 */
public class SearchCallbackChain implements SearchCallback
{

	public SearchCallbackNode head = null;

	public boolean onFound( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
		head = new SearchCallbackNode( entity, overlap, index, head );

		return true;
	}

	public void clear()
	{
		head = null;
	}
}
