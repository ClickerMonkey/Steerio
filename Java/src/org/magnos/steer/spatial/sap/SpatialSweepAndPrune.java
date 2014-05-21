package org.magnos.steer.spatial.sap;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

import org.magnos.steer.spatial.CollisionCallback;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialUtility;
import org.magnos.steer.vec.Vec2;


public class SpatialSweepAndPrune implements SpatialDatabase<Vec2>
{

	public Edge tailX = new Edge();
	public Edge tailY = new Edge();
	public int adjustments;
	public int adjustmentDistance;
	public int adjustmentDistanceMax;
	public int xpairs;
	public int ypairs;
	public int alive;
	
	public SpatialSweepAndPrune()
	{
	}
	
	@Override
	public void add( SpatialEntity<Vec2> entity )
	{
		final Vec2 pos = entity.getPosition();
		final float rad = entity.getRadius();
		
		addEdge( new Edge( entity, pos.x - rad, true ), tailX );
		addEdge( new Edge( entity, pos.x + rad, false ), tailX );		
		addEdge( new Edge( entity, pos.y - rad, true ), tailY );
		addEdge( new Edge( entity, pos.y + rad, false ), tailY );
	}
	
	private void addEdge(Edge e, Edge tail)
	{
		e.prev = tail.prev;
		tail.prev = e;
		
		adjustOrder( e, tail );
	}

	@Override
	public void clear()
	{
		clearEdges( tailX );
		clearEdges( tailY );
	}
	
	private void clearEdges(Edge tail)
	{
		Edge e = tail;
		
		while (e.prev != null)
		{
			Edge p = e.prev;
			e.prev = null;
			e.entity = null;
			e = p;
		}
	}

	@Override
	public int refresh()
	{
		adjustments = 0;
		adjustmentDistance = 0;
		adjustmentDistanceMax = 0;
		
		alive = update( tailX, true );
		update( tailY , false );
		
		sort( tailX.prev, tailX );
		sort( tailY.prev, tailY );
		
		/*
		if (!isSorted( tailX.prev ))
		{
			System.out.println( "tailX is not sorted" );
		}

		if (!isSorted( tailY.prev ))
		{
			System.out.println( "tailY is not sorted" );
		}
		
		int countedX = count( tailX.prev );
		int countedY = count( tailY.prev );
		
		if ( countedX != countedY )
		{
			System.out.println( "countedX != countedY" );
		}
		
		if ( countedX != alive )
		{
			System.out.println( "countedX != alive" );
		}
		
		if ( countedY != alive )
		{
			System.out.println( "countedY != alive" );
		}
		*/
		
		return alive;
	}
	
	@Override
	public int handleCollisions( CollisionCallback<Vec2> callback )
	{
		int collisionCount = 0;

		Set<Pair> pairsX = getOverlappingPairs( tailX );
//		Set<Pair> pairsY = getOverlappingPairs( tailY );
//		
//		xpairs = pairsX.size();
//		ypairs = pairsY.size();
//		
//		pairsX.retainAll( pairsY );
//		
//		for (Pair p : pairsX)
//		{
//			collisionCount = SpatialUtility.handleCollision( p.a, p.b, collisionCount, callback );
//		}
		
		/**/
		xpairs = pairsX.size();
		
		Pair pairY = new Pair( null, null );
		Edge currY = tailY.prev;
		
		ypairs = 0;
		
		while (currY != null)
		{
			if (!currY.min)
			{
				Edge prevY = currY.prev;
				pairY.a = currY.entity;
				
				while (prevY != null && prevY.entity != currY.entity)
				{
					pairY.b = prevY.entity;
					
					if ( pairsX.remove( pairY ) )
					{
						ypairs++;
						collisionCount = SpatialUtility.handleCollision( pairY.a, pairY.b, collisionCount, callback );
					}
					
					prevY = prevY.prev;
				}
			}

			currY = currY.prev;
		}
		/**/
		
		return collisionCount;
	}

	@Override
	public int intersects( Vec2 offset, float radius, int max, long collidesWith, SearchCallback<Vec2> callback )
	{
		int intersectCount = 0;
		
		Edge e = tailX.prev;
		
		while (e != null)
		{
			if (e.min)
			{
				final SpatialEntity<Vec2> a = e.entity;
				
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
			}
			
			e = e.prev;
		}
		
		return intersectCount;
	}

	@Override
	public int contains( Vec2 offset, float radius, int max, long collidesWith, SearchCallback<Vec2> callback )
	{
		int containCount = 0;
		
		Edge e = tailX.prev;
		
		while (e != null)
		{
			if (e.min)
			{
				final SpatialEntity<Vec2> a = e.entity;
				
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
			}
			
			e = e.prev;
		}
		
		return containCount;
	}

	@Override
	public int knn( Vec2 offset, int k, long collidesWith, SpatialEntity<Vec2>[] nearest, float[] distance )
	{
		if (!SpatialUtility.prepareKnn( k, nearest, distance ))
		{
			return 0;
		}
		
		int near = 0;
		
		Edge e = tailX.prev;
		
		while (e != null)
		{
			if (e.min)
			{
				final SpatialEntity<Vec2> a = e.entity;

				if (!a.isInert() && (collidesWith & a.getSpatialGroups()) != 0)
				{
					near = SpatialUtility.accumulateKnn( SpatialUtility.distance( a, offset ), a, near, k, distance, nearest );
				}
			}
			
			e = e.prev;
		}
		
		return near;
	}
	
	public void adjustOrder(Edge e, Edge next)
	{
		Edge curr = e;
		
		int d = 0;
		
		while (curr.prev != null && curr.prev.value > e.value)
		{
			curr = curr.prev;
			d++;
		}
		
		adjustmentDistanceMax = Math.max( adjustmentDistanceMax, d );
		adjustmentDistance += d;
		
		if (curr != e)
		{
			next.prev = e.prev;
			e.prev = curr.prev;
			curr.prev = e;
			adjustments++;
		}
	}
	
	public int update(Edge tail, boolean x)
	{
		int alive = 0;
		
		Edge next = tail;
		Edge curr = tail.prev;
		
		while (curr != null)
		{
			SpatialEntity<Vec2> e = curr.entity;

			if (e.isInert())
			{
				next.prev = curr.prev;
			}
			else
			{
				next = curr;
				alive++;
				
				if (x)
				{
					if (curr.min)
					{
						curr.value = e.getPosition().x - e.getRadius();
					}
					else
					{
						curr.value = e.getPosition().x + e.getRadius();
					}
				}
				else
				{
					if (curr.min)
					{
						curr.value = e.getPosition().y - e.getRadius();
					}
					else
					{
						curr.value = e.getPosition().y + e.getRadius();
					}
				}
			}
			
			curr = curr.prev;
		}
		
		return alive;
	}
	
	public void sort( Edge e, Edge next )
	{
		if ( e == null )
		{
			return;
		}
		
		ArrayDeque<Edge> stack = new ArrayDeque<Edge>( alive );
		
		while ( next.prev.prev != null )
		{
			stack.push( next );
			
			next = next.prev;
		}
		
		while ( !stack.isEmpty() )
		{
			Edge g = stack.pop();
			
			adjustOrder( g.prev, g );
		}
	}
	
	public boolean isSorted( Edge e )
	{
		while (e.prev != null)
		{
			if (e.prev.value > e.value)
			{
				return false;
			}
			
			e = e.prev;
		}
		
		return true;
	}
	
	public int count( Edge e )
	{
		int total = 0;
		
		while ( e != null )
		{
			total++;
			e = e.prev;
		}
		
		return total;
	}
	
	public Set<Pair> getOverlappingPairs( Edge tail )
	{
		Set<Pair> pairs = new HashSet<Pair>( alive );
		
		Edge curr = tail.prev;
		
		while (curr != null)
		{
			if (!curr.min)
			{
				Edge prev = curr.prev;
				
				while (prev != null && prev.entity != curr.entity)
				{
					pairs.add( new Pair( curr.entity, prev.entity ) );
					prev = prev.prev;
				}
				
				if ( prev != null )
				{
					Edge prev2 = prev.prev;
					
					while (prev2 != null && prev2.value == prev.value)
					{
						pairs.add( new Pair( curr.entity, prev2.entity ) );
						prev2 = prev2.prev;
					}
				}
			}
			
			curr = curr.prev;
		}
		
		return pairs;
	}
	
	private class Pair
	{
		public SpatialEntity<Vec2> a, b;
		
		public Pair(SpatialEntity<Vec2> a, SpatialEntity<Vec2> b)
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
	
	public class Edge
	{
		public SpatialEntity<Vec2> entity;
		public float value;
		public boolean min;
		public Edge prev;
		
		public Edge()
		{
		}
		
		public Edge(SpatialEntity<Vec2> entity, float value, boolean min)
		{
			this.entity = entity;
			this.value = value;
			this.min = min;
		}
	}

}
