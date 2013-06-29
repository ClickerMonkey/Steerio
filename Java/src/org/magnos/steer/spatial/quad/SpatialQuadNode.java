package org.magnos.steer.spatial.quad;

import org.magnos.steer.Vector;
import org.magnos.steer.spatial.CollisionCallback;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialUtility;
import org.magnos.steer.spatial.base.LinkedListRectangle;
import org.magnos.steer.util.LinkedNode;


public class SpatialQuadNode extends LinkedListRectangle<SpatialEntity>
{
	
	public static final int CHILD_COUNT = 4;

	public int size;
	public int overflowResizes;
	public int underflowResizes;
	public SpatialQuadNode[] children;
	public final SpatialQuadNode parent;
	
	public SpatialQuadNode(SpatialQuadNode parent, float l, float t, float r, float b )
	{
		super( l, t, r, b );
		
		this.parent = parent;
	}
	
	private void internalAdd(LinkedNode<SpatialEntity> node)
	{
		add( node );
		size++;
	}
	
	private void internalRemove(LinkedNode<SpatialEntity> node)
	{
		node.remove();
		size--;
	}
	
	public void add(SpatialEntity entity)
	{
		if ( isLeaf() )
		{
			internalAdd( new LinkedNode<SpatialEntity>( entity ) );
		}
		else
		{
			boolean added = false;
			
			for (int i = 0; i < CHILD_COUNT && !added; i++)
			{
				final SpatialQuadNode child = children[i];
				
				if ( child.isContained( entity.getPosition(), entity.getRadius() ) )
				{
					child.add( entity );
					
					added = true;
				}
			}
			
			if (!added)
			{
				internalAdd( new LinkedNode<SpatialEntity>( entity ) );
			}
		}
	}
	
	public int refresh()
	{
		int alive = 0;
		
		LinkedNode<SpatialEntity> start = head.next;
		
		while (start != head)
		{
			final LinkedNode<SpatialEntity> next = start.next;
			final SpatialEntity entity = start.value;
			
			if (entity.isInert())
			{
				internalRemove( start );
			}
			else
			{
				SpatialQuadNode node = getNode( entity );
				
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
			alive += children[0].refresh();
			alive += children[1].refresh();
			alive += children[2].refresh();
			alive += children[3].refresh();
		}
		
		return alive;
	}
	
	public SpatialQuadNode getNode( SpatialEntity entity )
	{
		SpatialQuadNode node = this;
		
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
					SpatialQuadNode child = node.children[i];
					
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
			children[0].resize( desiredSize, resizeThreshold );
			children[1].resize( desiredSize, resizeThreshold );
			children[2].resize( desiredSize, resizeThreshold );
			children[3].resize( desiredSize, resizeThreshold );

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
		final float cx = (l + r) * 0.5f;
		final float cy = (t + b) * 0.5f;
		
		children = new SpatialQuadNode[] {
			new SpatialQuadNode(this, l, t, cx, cy), /* top left */
			new SpatialQuadNode(this, cx, t, r, cy), /* top right */
			new SpatialQuadNode(this, l, cy, cx, b), /* bottom left */
			new SpatialQuadNode(this, cx, cy, r, b), /* bottom right */
		};
		
		LinkedNode<SpatialEntity> start = head.next;
		
		while (start != head)
		{
			final LinkedNode<SpatialEntity> next = start.next;
			final SpatialEntity entity = start.value;

			for (int i = 0; i < CHILD_COUNT; i++)
			{
				final SpatialQuadNode child = children[i];
				
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
			final SpatialQuadNode child = children[i];
			final LinkedNode<SpatialEntity> end = child.head;
			LinkedNode<SpatialEntity> start = end.next;
			
			while (start != end)
			{
				final LinkedNode<SpatialEntity> next = start.next;
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
			children[0].destroy();
			children[1].destroy();
			children[2].destroy();
			children[3].destroy();
			children = null;
		}
		
		LinkedNode<SpatialEntity> start = head.next;
		
		while (start != head)
		{
			LinkedNode<SpatialEntity> next = start.next;
			start.remove();
			start = next;
		}
		
		size = 0;
	}
	
	public int handleCollisions( CollisionCallback callback )
	{
		int collisionCount = 0;

		// Handle collisions within this node
		LinkedNode<SpatialEntity> start = head.next;
		while (start != head)
		{
			final SpatialEntity a = start.value;
			
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
			collisionCount += children[0].handleCollisions( callback );
			collisionCount += children[1].handleCollisions( callback );
			collisionCount += children[2].handleCollisions( callback );
			collisionCount += children[3].handleCollisions( callback );
		}
		
		return collisionCount;
	}
	
	private int handleCollisionsWithChildren( SpatialEntity a, CollisionCallback callback )
	{
		int collisionCount = 0;
		
		for (int i = 0; i < CHILD_COUNT; i++)
		{
			collisionCount += children[i].handleEntityCollisionFromParent( a, callback );
		}
		
		return collisionCount;
	}
	
	private int handleEntityCollisionFromParent(SpatialEntity a, CollisionCallback callback)
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
	
	private int handleCollisionsAgainstList( SpatialEntity a, LinkedNode<SpatialEntity> start, LinkedNode<SpatialEntity> end, CollisionCallback callback )
	{
		start = start.next;

		int collisionCount = 0;
		
		while (start != end)
		{
			final LinkedNode<SpatialEntity> next = start.next;
			final SpatialEntity b = start.value;
			
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
	
	public int intersects( Vector offset, float radius, int max, long collidesWith, SearchCallback callback, int intersectCount )
	{
		LinkedNode<SpatialEntity> start = head.next;
		
		while (start != head)
		{
			final SpatialEntity a = start.value;
			
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
				SpatialQuadNode child = children[i];
				
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
	
	public int contains( Vector offset, float radius, int max, long collidesWith, SearchCallback callback, int containCount )
	{
		LinkedNode<SpatialEntity> start = head.next;
		
		while (start != head)
		{
			final SpatialEntity a = start.value;
			
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
				SpatialQuadNode child = children[i];
				
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
	
	public int knn( Vector offset, int k, long collidesWith, SpatialEntity[] nearest, float[] distance, int near )
	{
		LinkedNode<SpatialEntity> start = head.next;
		
		while (start != head)
		{
			final SpatialEntity a = start.value;
			
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
			size += children[0].getSize();
			size += children[1].getSize();
			size += children[2].getSize();
			size += children[3].getSize();
		}
		
		return size;
	}
	
}
