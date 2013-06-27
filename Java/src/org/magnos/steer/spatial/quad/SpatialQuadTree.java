package org.magnos.steer.spatial.quad;

import org.magnos.steer.Vector;
import org.magnos.steer.util.LinkedList;


public class SpatialQuadTree extends LinkedList<SpatialQuadNode>
{

	public int size;
	public int overflowRefreshes;
	public int underflowRefreshes;
	public SpatialQuadTree[] children;
	public SpatialQuadTree parent;
	public float l, t, r, b;
	
	public void resize(int desiredSize)
	{
		
	}
	
	public boolean isContained(Vector center, float radius)
	{
		return isContained( center.x - radius, center.y - radius, center.x + radius, center.y + radius );
	}
	
	public boolean isContained(float left, float top, float right, float bottom)
	{
		return (left < l || right >= r || top < t || bottom >= b);
	}
	
	public boolean isLeaf()
	{
		return (children == null);
	}
	
	public int getSize()
	{
		return size + getChildrenSize();
	}
	
	public int getChildrenSize()
	{
		int size = 0;
		
		if ( children != null )
		{
			size += children[0].getSize();
			size += children[1].getSize();
			size += children[2].getSize();
			size += children[3].getSize();
		}
		
		return size;
	}
	
}
