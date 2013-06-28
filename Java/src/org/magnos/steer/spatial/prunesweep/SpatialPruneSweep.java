package org.magnos.steer.spatial.prunesweep;

import java.util.Arrays;
import java.util.HashSet;

import org.magnos.steer.Vector;
import org.magnos.steer.spatial.CollisionCallback;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialUtility;


public class SpatialPruneSweep implements SpatialDatabase
{

	public int nodeCount;
	public SpatialPruneSweepNode[] nodeX;
	public SpatialPruneSweepNode[] nodeY;
	public int moves;
	public int averageSpanMax;
	public int xpairs, ypairs, xypairs;
	
	public SpatialPruneSweep(int initialCapacity)
	{
		this.nodeX = new SpatialPruneSweepNode[initialCapacity];
		this.nodeY = new SpatialPruneSweepNode[initialCapacity];
	}
	
	@Override
	public void add( SpatialEntity entity )
	{
		if (nodeCount == nodeX.length)
		{
			nodeX = Arrays.copyOf( nodeX, nodeCount + (nodeCount >> 1) );
			nodeY = Arrays.copyOf( nodeY, nodeCount + (nodeCount >> 1) );
		}

		final int i = nodeCount++;
		
		SpatialPruneSweepNode nx = new SpatialPruneSweepNode( entity );
		SpatialPruneSweepNode ny = new SpatialPruneSweepNode( entity );
		
		nodeX[i] = nx;
		nodeY[i] = ny;
		
		updateX( i );
		updateY( i );
		
		adjustOrder( i, nodeX );
		adjustOrder( i, nodeY );
		
		updateSpanMax( i, nodeX );
		updateSpanMax( i, nodeY );
	}

	@Override
	public void clear()
	{
		while (--nodeCount >= 0)
		{
			nodeX[nodeCount] = null;
			nodeY[nodeCount] = null;
		}
		
		nodeCount = 0;
	}

	@Override
	public int refresh()
	{
		// Clear stats
		moves = 0;
		averageSpanMax = 0;
		
		// Remove inert entities.
		nodeCount = ( refresh( nodeX, nodeCount ) + refresh( nodeY, nodeCount ) ) >> 1;
		
		// Update node's minimum and maximum based on entities new positions.
		for (int i = 0; i < nodeCount; i++)
		{
			updateX( i );
			updateY( i );
		}
		
		// Insertion sort all nodes based on their minimum.
		for (int i = 1; i < nodeCount; i++)
		{
			adjustOrder( i, nodeX );
			adjustOrder( i, nodeY );
		}
		
		// Update the spanMax on every node.
		for (int i = nodeCount - 2; i >= 0; i--)
		{
			updateSpanMax( i, nodeX );
			updateSpanMax( i, nodeY );
		}
		
		averageSpanMax /= nodeCount * 2;
		
		return nodeCount;
	}
	
	private int refresh( SpatialPruneSweepNode[] nodes, int n )
	{
		int alive = 0;

		for (int i = 0; i < n; i++)
		{
			final SpatialEntity e = nodes[i].entity;

			if (!e.isInert())
			{
				nodes[alive++] = nodes[i];
			}
		}
		
		while (n > alive)
		{
			nodes[--n] = null;
		}
		
		return alive;
	}

	@Override
	public int handleCollisions( CollisionCallback callback )
	{
		int collisionCount = 0;
		
		// overlapping pairs based on spanMax
		HashSet<Pair> pairsX = getPairsSpanMax( nodeX );
		HashSet<Pair> pairsY = getPairsSpanMax( nodeY );
		
		xpairs = pairsX.size();
		ypairs = pairsY.size();
		
		// intersection
		pairsX.retainAll( pairsY );
		
		xypairs = pairsX.size();
		
		// check for collisions
		for (Pair p : pairsX)
		{
			collisionCount = SpatialUtility.handleCollision( p.a, p.b, collisionCount, callback );
		}
		
		return collisionCount;
	}

	@Override
	public int intersects( Vector offset, float radius, int max, long collidesWith, SearchCallback callback )
	{
		return 0;
	}

	@Override
	public int contains( Vector offset, float radius, int max, long collidesWith, SearchCallback callback )
	{
		return 0;
	}

	@Override
	public int knn( Vector point, int k, long collidesWith, SpatialEntity[] nearest, float[] distance )
	{
		return 0;
	}
	
	private void adjustOrder(int i, SpatialPruneSweepNode[] nodes)
	{
		SpatialPruneSweepNode n = nodes[i];
		
		int insert = i;
		
		while (insert > 0 && n.min < nodes[insert - 1].min) insert--;
		
		if (i != insert)
		{
			System.arraycopy( nodes, insert, nodes, insert + 1, i - insert );
			
			nodes[insert] = n;
			
			moves++;
		}
	}
	
	private void updateSpanMax(int i, SpatialPruneSweepNode[] nodes)
	{
		SpatialPruneSweepNode n = nodes[i];
		n.spanMax = 0;
		
		while (++i < nodeCount && nodes[i].min < n.max)
		{
			n.spanMax++;
		}
		
		averageSpanMax += n.spanMax;
	}
	
	private void updateX(int i)
	{
		SpatialPruneSweepNode n = nodeX[i];
		SpatialEntity e = n.entity;
		Vector p = e.getPosition();
		float r = e.getRadius();
		
		n.min = p.x - r;
		n.max = p.x + r;
	}
	
	private void updateY(int i)
	{
		SpatialPruneSweepNode n = nodeY[i];
		SpatialEntity e = n.entity;
		Vector p = e.getPosition();
		float r = e.getRadius();
		
		n.min = p.y - r;
		n.max = p.y + r;
	}
	
	private HashSet<Pair> getPairsSpanMax(SpatialPruneSweepNode[] nodes)
	{
		HashSet<Pair> pairs = new HashSet<Pair>();
		
		for (int i = 0; i < nodeCount; i++)
		{
			SpatialPruneSweepNode n = nodes[i];
			
			for (int k = 1; k <= n.spanMax; k++ )
			{
				pairs.add( new Pair( n.entity, nodes[k].entity ) );
			}
		}
		
		return pairs;
	}
	
	private class Pair
	{
		public SpatialEntity a, b;
		
		public Pair(SpatialEntity a, SpatialEntity b)
		{
			this.a = a;
			this.b = b;
		}

		@Override
		public int hashCode()
		{
			return a.hashCode() ^ b.hashCode();
		}

		@Override
		public boolean equals( Object obj )
		{
			Pair p = (Pair)obj;
			
			return ( p.a == a && p.b == b ) || 
					 ( p.a == b && p.b == a );
		}
	}

}
