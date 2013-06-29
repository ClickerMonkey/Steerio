package org.magnos.steer.spatial.dual;

import org.magnos.steer.Vector;
import org.magnos.steer.spatial.CollisionCallback;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialUtility;
import org.magnos.steer.spatial.base.LinkedListRectangle;
import org.magnos.steer.util.LinkedNode;


public class SpatialDualNode extends LinkedListRectangle<SpatialEntity>
{
	
	public static final int CHILD_COUNT = 4;

	public int size;
	public int overflowResizes;
	public int underflowResizes;
	public SpatialDualNode tl;
	public SpatialDualNode br;
	public final SpatialDualNode parent;
	public final boolean hamburger;
	
	public SpatialDualNode(SpatialDualNode parent, boolean hamburger, float l, float t, float r, float b )
	{
		super( l, t, r, b );
		
		this.parent = parent;
		this.hamburger = hamburger;
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
			if ( tl.isContained( entity.getPosition(), entity.getRadius() ) )
			{
				tl.add( entity );
			}
			else if ( br.isContained( entity.getPosition(), entity.getRadius() ) )
			{
				br.add( entity );
			}
			else
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
				SpatialDualNode node = getNode( entity );
				
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
			alive += tl.refresh();
			alive += br.refresh();
		}
		
		return alive;
	}
	
	public SpatialDualNode getNode( SpatialEntity entity )
	{
		SpatialDualNode node = this;
		
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
			if ( node.isBranch() )
			{
				if ( node.tl.isContained( entity.getPosition(), entity.getRadius() ) )
				{
					node = node.tl;
				}
				else if ( node.br.isContained( entity.getPosition(), entity.getRadius() ) )
				{
					node = node.br;
				}
				else
				{
					placing = false;
				}
			}
			else
			{
				placing = false;
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
			tl.resize( desiredSize, resizeThreshold );
			br.resize( desiredSize, resizeThreshold );

			if ( tl.isLeaf() && br.isLeaf() )
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
		
		if (hamburger)
		{
			tl = new SpatialDualNode( this, false, l, t, cx, b ); // left
			br = new SpatialDualNode( this, false, cx, t, r, b ); // right
		}
		else
		{
			tl = new SpatialDualNode( this, true, l, t, r, cy ); // top
			br = new SpatialDualNode( this, true, l, cy, r, b ); // bottom
		}
		
		LinkedNode<SpatialEntity> start = head.next;
		
		while (start != head)
		{
			final LinkedNode<SpatialEntity> next = start.next;
			final SpatialEntity entity = start.value;

			if ( tl.isContained( entity.getPosition(), entity.getRadius() ) )
			{
				internalRemove( start );
				tl.internalAdd( start );
			}
			else if ( br.isContained( entity.getPosition(), entity.getRadius() ) )
			{
				internalRemove( start );
				br.internalAdd( start );
			}
			
			start = next;
		}
	}
	
	public void shrink()
	{
		final LinkedNode<SpatialEntity> endtl = tl.head;
		LinkedNode<SpatialEntity> starttl = endtl.next;
		
		while (starttl != endtl)
		{
			final LinkedNode<SpatialEntity> next = starttl.next;
			tl.internalRemove( starttl );
			internalAdd( starttl );
			starttl = next;
		}

		final LinkedNode<SpatialEntity> endbr = br.head;
		LinkedNode<SpatialEntity> startbr = endbr.next;
		
		while (startbr != endbr)
		{
			final LinkedNode<SpatialEntity> next = startbr.next;
			br.internalRemove( startbr );
			internalAdd( startbr );
			startbr = next;
		}
		
		tl = br = null;
	}
	
	public void destroy()
	{
		if ( isBranch() )
		{
			tl.destroy();
			br.destroy();
			tl = br = null;
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
			collisionCount += tl.handleCollisions( callback );
			collisionCount += br.handleCollisions( callback );
		}
		
		return collisionCount;
	}
	
	private int handleCollisionsWithChildren( SpatialEntity a, CollisionCallback callback )
	{
		return tl.handleEntityCollisionFromParent( a, callback ) + 
				 br.handleEntityCollisionFromParent( a, callback );
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
			
			if (!a.isInert() && (collidesWith & a.getSpatialGroups()) != 0)
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
			if ( tl.isIntersecting( offset, radius ) )
			{
				intersectCount = tl.intersects( offset, radius, max, collidesWith, callback, intersectCount );
			}
			if ( intersectCount < max && br.isIntersecting( offset, radius ) )
			{
				intersectCount = br.intersects( offset, radius, max, collidesWith, callback, intersectCount );
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
			if ( tl.isIntersecting( offset, radius ) )
			{
				containCount = tl.contains( offset, radius, max, collidesWith, callback, containCount );
			}
			if ( containCount < max && br.isIntersecting( offset, radius ) )
			{
				containCount = br.contains( offset, radius, max, collidesWith, callback, containCount );
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
			near = tl.knn( offset, k, collidesWith, nearest, distance, near );
			near = br.knn( offset, k, collidesWith, nearest, distance, near );
		}
		
		return near;
	}
	
	public boolean isLeaf()
	{
		return (tl == null);
	}
	
	public boolean isBranch()
	{
		return (tl != null);
	}
	
	public int getSize()
	{
		return size + getChildrenSize();
	}
	
	public int getChildrenSize()
	{
		return (tl != null ? tl.getSize() + br.getSize() : 0);
	}
	
}
