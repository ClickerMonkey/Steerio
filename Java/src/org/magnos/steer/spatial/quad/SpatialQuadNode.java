package org.magnos.steer.spatial.quad;

import org.magnos.steer.spatial.CollisionCallback;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialUtility;
import org.magnos.steer.spatial.base.LinkedListBounds;
import org.magnos.steer.util.LinkedNode;
import org.magnos.steer.vec.Vec;


public class SpatialQuadNode<V extends Vec<V>> extends LinkedListBounds<V, SpatialEntity<V>>
{
	
	public static final int CHILD_COUNT = 4;

	public int size;
	public int overflowResizes;
	public int underflowResizes;
	public SpatialQuadNode<V>[] children;
	public final SpatialQuadNode<V> parent;
	
	public SpatialQuadNode(SpatialQuadNode<V> parent, V min, V max )
	{
		super( min, max );
		
		this.parent = parent;
	}
	
	private void internalAdd(LinkedNode<SpatialEntity<V>> node)
	{
		add( node );
		size++;
	}
	
	private void internalRemove(LinkedNode<SpatialEntity<V>> node)
	{
		node.remove();
		size--;
	}
	
	public void add(SpatialEntity<V> entity)
	{
		if ( isLeaf() )
		{
			internalAdd( new LinkedNode<SpatialEntity<V>>( entity ) );
		}
		else
		{
			boolean added = false;
			
			for (int i = 0; i < CHILD_COUNT && !added; i++)
			{
				final SpatialQuadNode<V> child = children[i];
				
				if ( child.isContained( entity.getPosition(), entity.getRadius() ) )
				{
					child.add( entity );
					
					added = true;
				}
			}
			
			if (!added)
			{
				internalAdd( new LinkedNode<SpatialEntity<V>>( entity ) );
			}
		}
	}
	
	public int refresh()
	{
		int alive = 0;
		
		LinkedNode<SpatialEntity<V>> start = head.next;
		
		while (start != head)
		{
			final LinkedNode<SpatialEntity<V>> next = start.next;
			final SpatialEntity<V> entity = start.value;
			
			if (entity.isInert())
			{
				internalRemove( start );
			}
			else
			{
				SpatialQuadNode<V> node = getNode( entity );
				
				if ( node != this )
				{
					internalRemove( start );
					node.internalAdd( start );
				}
				else
				{
					alive++;
				}
			}
			
			start = next;
		}
		
		if ( isBranch() )
		{
            for (int i = 0; i < children.length; i++)
            {
                alive += children[i].refresh();
            }
		}
		
		return alive;
	}
	
	public SpatialQuadNode<V> getNode( SpatialEntity<V> entity )
	{
		SpatialQuadNode<V> node = this;
		
		while (node.parent != null && !node.isContained( entity.getPosition(), entity.getRadius() ))
		{
			node = node.parent;
		}
		
		if ( node != this )
		{
			return node;
		}

		boolean placing = true;
		
		while (placing)
		{
			placing = false;
			
			if ( node.isBranch() )
			{
				for (int i = 0; i < CHILD_COUNT; i++)
				{
					SpatialQuadNode<V> child = node.children[i];
					
					if ( child.isContained( entity.getPosition(), entity.getRadius() ) )
					{
						node = child;
						placing = true;
						
						break;
					}
				}
			}
		}
		
		return node;
	}
	
	public void resize(int desiredSize, int resizeThreshold)
	{
		// Leaves can expand if their size >= desiredSize for resizeThreshold number of resize requests.
		if ( isLeaf() )
		{
			if ( size >= desiredSize )
			{
				overflowResizes++;
			}
			else
			{
				overflowResizes = 0;
			}
			
			if ( overflowResizes >= resizeThreshold )
			{
				overflowResizes = 0;
				expand();
			}
		}
		// Branches can shrink if their total size < desiredSize for resizeThreshold number of resize requests AND all their children are leaves.
		else
		{
			// Try to resize children
		    for (int i = 0; i < children.length; i++)
		    {
	            children[i].resize( desiredSize, resizeThreshold );   
		    }

			if ( children[0].isLeaf() && children[1].isLeaf() && children[2].isLeaf() && children[3].isLeaf() )
			{
				if ( getSize() < desiredSize ) 
				{
					underflowResizes++;
				}
				else
				{
					underflowResizes = 0;
				}
				
				if ( underflowResizes >= resizeThreshold )
				{
					underflowResizes = 0;
					shrink();
				}
			}
			else
			{
				underflowResizes = 0;
			}
		}
	}
	
	public void expand()
	{
	    int components = center.size();
	    int childCount = 1 << components;
	    
	    children = new SpatialQuadNode[ childCount ];
	    
	    for (int i = 0; i < childCount; i++)
	    {
	        V childMin = min.clone();
	        V childMax = max.clone();
	        
	        for (int k = 0; k < components; k++)
	        {
	            if (((i >> k) & 0x1) == 0x1)
	            {
	                childMin.setComponent( k, center.getComponent( k ) );
	            }
	            else
	            {
	                childMax.setComponent( k, center.getComponent( k ) );
	            }
	        }
	        
	        children[i] = new SpatialQuadNode<V>( this, childMin, childMax );
	    }

	    LinkedNode<SpatialEntity<V>> start = head.next;
		
		while (start != head)
		{
			final LinkedNode<SpatialEntity<V>> next = start.next;
			final SpatialEntity<V> entity = start.value;

			for (int i = 0; i < CHILD_COUNT; i++)
			{
				final SpatialQuadNode<V> child = children[i];
				
				if (child.isContained( entity.getPosition(), entity.getRadius() ))
				{
					internalRemove( start );
					child.internalAdd( start );
					
					break;
				}
			}
			
			start = next;
		}
	}
	
	public void shrink()
	{
		for (int i = 0; i < CHILD_COUNT; i++)
		{
			final SpatialQuadNode<V> child = children[i];
			final LinkedNode<SpatialEntity<V>> end = child.head;
			LinkedNode<SpatialEntity<V>> start = end.next;
			
			while (start != end)
			{
				final LinkedNode<SpatialEntity<V>> next = start.next;
				child.internalRemove( start );
				internalAdd( start );
				start = next;
			}
		}
		
		children = null;
	}
	
	public void destroy()
	{
		if ( isBranch() )
		{
            for (int i = 0; i < children.length; i++)
            {
                children[i].destroy();
            }
			children = null;
		}
		
		LinkedNode<SpatialEntity<V>> start = head.next;
		
		while (start != head)
		{
			LinkedNode<SpatialEntity<V>> next = start.next;
			start.remove();
			start = next;
		}
		
		size = 0;
	}
	
	public int handleCollisions( CollisionCallback<V> callback )
	{
		int collisionCount = 0;

		// Handle collisions within this node
		LinkedNode<SpatialEntity<V>> start = head.next;
		while (start != head)
		{
			final SpatialEntity<V> a = start.value;
			
			if ( !a.isInert() )
			{
				collisionCount += handleCollisionsAgainstList( a, start, head, callback );	
			}
			
			start = start.next;
		}
		
		// Handle collisions with children and within children
		if (isBranch())
		{
			// Handle collisions between the entities in this node and the entities in the children
			start = head.next;
			while (start != head)
			{
				collisionCount += handleCollisionsWithChildren( start.value, callback );
				start = start.next;
			}
			
			// Handle collisions for children
            for (int i = 0; i < children.length; i++)
            {
                collisionCount += children[i].handleCollisions( callback );
            }
		}
		
		return collisionCount;
	}
	
	private int handleCollisionsWithChildren( SpatialEntity<V> a, CollisionCallback<V> callback )
	{
		int collisionCount = 0;
		
		for (int i = 0; i < CHILD_COUNT; i++)
		{
			collisionCount += children[i].handleEntityCollisionFromParent( a, callback );
		}
		
		return collisionCount;
	}
	
	private int handleEntityCollisionFromParent(SpatialEntity<V> a, CollisionCallback<V> callback)
	{
		int collisionCount = 0;
		
		if ( !a.isInert() && isIntersecting( a.getPosition(), a.getRadius() ))
		{
			collisionCount += handleCollisionsAgainstList( a, head, head, callback );
			
			if ( isBranch() )
			{
				collisionCount += handleCollisionsWithChildren( a, callback );
			}
		}
		
		return collisionCount;
	}
	
	private int handleCollisionsAgainstList( SpatialEntity<V> a, LinkedNode<SpatialEntity<V>> start, LinkedNode<SpatialEntity<V>> end, CollisionCallback<V> callback )
	{
		start = start.next;

		int collisionCount = 0;
		
		while (start != end)
		{
			final LinkedNode<SpatialEntity<V>> next = start.next;
			final SpatialEntity<V> b = start.value;
			
			if ( !b.isInert() )
			{
				collisionCount = SpatialUtility.handleCollision( a, start.value, collisionCount, callback );
				
				if ( a.isInert() )
				{
					break;
				}
			}
			
			start = next;
		}
		
		return collisionCount;
	}
	
	public int intersects( V offset, float radius, int max, long collidesWith, SearchCallback<V> callback, int intersectCount )
	{
		LinkedNode<SpatialEntity<V>> start = head.next;
		
		while (start != head)
		{
			final SpatialEntity<V> a = start.value;
			
			if ( !a.isInert() && (collidesWith & a.getSpatialGroups()) != 0)
			{
				final float overlap = SpatialUtility.overlap( a, offset, radius );

				if (overlap > 0 && callback.onFound( a, overlap, intersectCount, offset, radius, max, collidesWith ))
				{
					intersectCount++;

					if (intersectCount >= max)
					{
						break;
					}
				}
			}
			start = start.next;
		}
		
		if ( intersectCount < max && isBranch() )
		{
			for (int i = 0; i < CHILD_COUNT; i++)
			{
				SpatialQuadNode<V> child = children[i];
				
				if ( child.isIntersecting( offset, radius ) )
				{
					intersectCount = child.intersects( offset, radius, max, collidesWith, callback, intersectCount );

					if (intersectCount >= max)
					{
						break;
					}
				}
			}
		}
		
		return intersectCount;
	}
	
	public int contains( V offset, float radius, int max, long collidesWith, SearchCallback<V> callback, int containCount )
	{
		LinkedNode<SpatialEntity<V>> start = head.next;
		
		while (start != head)
		{
			final SpatialEntity<V> a = start.value;
			
			if (!a.isInert() && (collidesWith & a.getSpatialGroups()) != 0)
			{
				final float aradius2 = a.getRadius() * 2;
				final float overlap = SpatialUtility.overlap( a, offset, radius );
				
				if (overlap >= aradius2)
				{
					if (callback.onFound( a, radius - overlap, containCount, offset, radius, max, collidesWith ))
					{
						containCount++;

						if (containCount >= max)
						{
							break;
						}
					}
				}
			}
			start = start.next;
		}
		
		if ( containCount < max && isBranch() )
		{
			for (int i = 0; i < CHILD_COUNT; i++)
			{
				SpatialQuadNode<V> child = children[i];
				
				if ( child.isIntersecting( offset, radius ) )
				{
					containCount = child.contains( offset, radius, max, collidesWith, callback, containCount );
					
					if (containCount >= max)
					{
						break;
					}
				}
			}
		}
		
		return containCount;
	}
	
	public int knn( V offset, int k, long collidesWith, SpatialEntity<V>[] nearest, float[] distance, int near )
	{
		LinkedNode<SpatialEntity<V>> start = head.next;
		
		while (start != head)
		{
			final SpatialEntity<V> a = start.value;
			
			if (!a.isInert() && (a.getSpatialGroups() & collidesWith) != 0)
			{
				near = SpatialUtility.accumulateKnn( SpatialUtility.distance( a, offset ), a, near, k, distance, nearest );
			}
			
			start = start.next;
		}
		
		if ( isBranch() )
		{
			for (int i = 0; i < CHILD_COUNT; i++)
			{
				 near = children[i].knn( offset, k, collidesWith, nearest, distance, near );
			}
		}
		
		return near;
	}
	
	public boolean isLeaf()
	{
		return (children == null);
	}
	
	public boolean isBranch()
	{
		return (children != null);
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
            for (int i = 0; i < children.length; i++)
            {
                size += children[i].getSize();
            }
		}
		
		return size;
	}
	
}
