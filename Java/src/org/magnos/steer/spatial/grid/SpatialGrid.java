
package org.magnos.steer.spatial.grid;

import org.magnos.steer.spatial.CollisionCallback;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialUtility;
import org.magnos.steer.util.LinkedList;
import org.magnos.steer.util.LinkedNode;
import org.magnos.steer.vec.Vec;

// A SpatialDatabase stores SpatialEntitys to be quickly searched and checked for intersections.
// TODO KNN using the cells
// TODO verify accuracy against brute-force method (SpatialArray)
public class SpatialGrid<V extends Vec<V>> implements SpatialDatabase<V>
{

	// A grid of cells, which are a linked list of entities.
	public final SpatialGridCell<V>[][] cells;
	
	// The number of cells wide the database is.
	public final int width;
	
	// The number of cells high the database is.
	public final int height;
	
	// The offset of the database grid, typically at {0,0}
	public final V offset;
	
	// The size of each cell in the database.
	public final V size;
	
	// The inverse of size.
	public final V sizeInversed;
	
	// The list of entities outside the grid.
	public final SpatialGridCell<V> outside;
	
	// The list of entities in the database.
	public final LinkedList<SpatialGridNode<V>> entities;
	
	// The boundaries of this database.
	public final V min, max;

	public int collisionChecks;
	public int collisionMatches;
	public int collisions;
	public int remapped;

	public SpatialGrid( int cellsWide, int cellsHigh, float cellWidth, float cellHeight, float offsetX, float offsetY )
	{
		this.offset = new Vec( offsetX, offsetY );
		this.size = new Vec( cellWidth, cellHeight );
		this.sizeInversed = new Vec( 1 / cellWidth, 1 / cellHeight );
		this.width = cellsWide;
		this.height = cellsHigh;
		this.outside = new SpatialGridCell<V>( -1, -1, this );
		this.entities = new LinkedList<SpatialGridNode<V>>();
		this.t = offset.y;
		this.b = offset.y + cellsHigh * cellHeight;
		this.l = offset.x;
		this.r = offset.x + cellsWide * cellWidth;

		this.cells = new SpatialGridCell[cellsHigh][cellsWide];
		for (int y = 0; y < cellsHigh; y++)
		{
			for (int x = 0; x < cellsWide; x++)
			{
				this.cells[y][x] = new SpatialGridCell<V>( x, y, this );
			}
		}
	}

	@Override
	public void add( SpatialEntity entity )
	{
		// Create node for entity
		final SpatialGridNode node = new SpatialGridNode( entity );

		// Add the entity to the database entity list.
		entities.add( node.databaseNode );
		
		// Assume the node is on the outside, let the update fix it.
		outside.add( node.cellNode );
		
		// Add them to their cell or the outside list.
		updateNode( node );
	}

	@Override
	public void clear()
	{
		clear( entities );
		clear( outside );
		
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				clear( cells[y][x] );
			}
		}
	}
	
	private void clear(LinkedList<?> list)
	{
		LinkedNode<?> end = list.head;
		LinkedNode<?> start = end.next;
		
		while (start != end)
		{
			LinkedNode<?> next = start.next;
			start.remove();
			start = next;
		}
	}
	
	@Override
	public int refresh()
	{
		// Updates which cell each entity exists in.
		int alive = 0;

		remapped = 0;

		// Clear all look-backs
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				SpatialGridCell cell = cells[y][x];
				cell.lookbackX = 0;
				cell.lookbackY = 0;
			}
		}
		
		LinkedNode<SpatialGridNode> linkedNode = entities.head.next;

		// Update entity nodes
		while (linkedNode != entities.head)
		{
			final SpatialGridNode node = linkedNode.value;
			final SpatialEntity entity = node.entity;
			final LinkedNode<SpatialGridNode> nextNode = linkedNode.next;

			// If the entity is inert, remove it from the database.
			if (entity.isInert())
			{
				node.remove();
			}
			// If they are not static, update their cell.
			else if (!entity.isStatic())
			{
				// An entity that's alive!
				alive++;

				// Re-calculate cell index
				if ( updateNode( node ) )
				{
					remapped++;
				}
			}

			// Update look-back of cells
			updateLookback( entity.getPosition(), entity.getRadius() );

			// Next entity...
			linkedNode = nextNode;
		}

		return alive;
	}
	
	private boolean updateNode(SpatialGridNode node)
	{
		final SpatialEntity entity = node.entity;
		final LinkedNode<SpatialGridNode> cellNode = node.cellNode;
		final SpatialGridCell cell = node.cell;
		final Vec pos = entity.getPosition();
		final float rad = entity.getRadius();
		
		final int cx = getCellX( pos.x - rad, -1, -1 );
		final int cy = getCellY( pos.y - rad, -1, -1 );
		
		boolean isOutside = ( cx == -1 || cy == -1 ); 
		
		if ( ((cell == null) != isOutside) || (cell != null && (cell.x != cx || cell.y != cy) ) )
		{
			cellNode.remove();
			
			if ( isOutside )
			{
				outside.add( cellNode );
				node.cell = null;
			}
			else
			{
				cells[cy][cx].add( node.cellNode );
				node.cell = cells[cy][cx];
			}
			
			return true;
		}
		
		return false;
	}
	
	private void updateLookback( Vec pos, float rad )
	{
		final int actualX = getCellX( pos.x - rad );
		final int actualY = getCellY( pos.y - rad );
		final CellSpan span = getCellSpan( pos, rad, false, new CellSpan() );
		
		for (int y = span.T; y <= span.B; y++)
		{
			for (int x = span.L; x <= span.R; x++)
			{
				SpatialGridCell currentCell = cells[y][x];
				currentCell.lookbackX = Math.max( currentCell.lookbackX, x - actualX );
				currentCell.lookbackY = Math.max( currentCell.lookbackY, y - actualY );
			}
		}
	}
	
	@Override
	public int handleCollisions( CollisionCallback callback )
	{
		int collisionCount = 0;
		
		callback.onCollisionStart();
		
		LinkedNode<SpatialGridNode> linkedNode = entities.head.next;
		CellSpan span = new CellSpan();
		
		while (linkedNode != entities.head)
		{
			final SpatialGridNode node = linkedNode.value;
			final SpatialEntity entity = node.entity;
			final Vec pos = entity.getPosition();
			final float rad = entity.getRadius();
			final LinkedNode<SpatialGridNode> nextNode = linkedNode.next;
			
			if ( entity.isInert() )
			{
				continue;
			}
			
			getCellSpan( pos, rad, false, span );
			
			for (int y = span.T; y <= span.B; y++)
			{
				for (int x = span.L; x <= span.R; x++)
				{
					SpatialGridCell cell = cells[y][x];
					
					// For the cell of the entity, start looking after the entity node to avoid duplicate.
					if ( cell == node.cell )
					{
						collisionCount += handleCollisions( entity, node.cellNode, cell.head, callback );
					}
					// For other cells, check against entire cell.
					else
					{
						collisionCount += handleCollisions( entity, cell.head, cell.head, callback );
					}
					
					if ( entity.isInert() )
					{
						x = span.R + 1;
						y = span.B + 1;
					}
				}
			}
			
			// If the current entity is partially outside, check collisions with other entities
			if ( !entity.isInert() && isOutsidePartially( pos, rad ) )
			{
				collisionCount += handleCollisions( entity, outside.head, outside.head, callback );
			}
			
			// Next entity...
			linkedNode = nextNode;
		}
		
		callback.onCollisionEnd();
		
		return collisionCount;
	}
	
	private int handleCollisions( SpatialEntity a, LinkedNode<SpatialGridNode> start, LinkedNode<SpatialGridNode> end, CollisionCallback callback)
	{
		start = start.next;
		
		int collisionCount = 0;
		
		while ( start != end )
		{
			final SpatialEntity entity = start.value.entity;
			
			if ( !entity.isInert() )
			{
				collisionCount = SpatialUtility.handleCollision( a, entity, collisionCount, callback );	
				
				if ( a.isInert() )
				{
					break;
				}
			}
			
			start = start.next;
		}
		
		return collisionCount;
	}

	@Override
	public int intersects( Vec offset, float radius, int max, long collidesWith, SearchCallback callback )
	{
		final CellSpan span = getCellSpan( offset, radius, true, new CellSpan() );
		
		int intersectCount = 0;

		for (int y = span.T; y <= span.B; y++)
		{
			for (int x = span.L; x <= span.R; x++)
			{
				intersectCount = intersects( offset, radius, max, collidesWith, callback, intersectCount, cells[y][x] );
				
				if (intersectCount == max)
				{
					y = span.B + 1;
					x = span.R + 1;
				}
			}
		}
		
		if ( intersectCount < max && isOutsidePartially( offset, radius ) )
		{
			intersectCount = intersects( offset, radius, max, collidesWith, callback, intersectCount, outside );
		}

		return intersectCount;
	}

	private int intersects( Vec offset, float radius, int max, long collidesWith, SearchCallback callback, int intersectCount, LinkedList<SpatialGridNode> list )
	{
		final LinkedNode<SpatialGridNode> end = list.head;
		LinkedNode<SpatialGridNode> start = end.next;

		while (start != end)
		{
			final SpatialEntity a = start.value.entity;

			if ( !a.isInert() && (a.getSpatialGroups() & collidesWith) != 0)
			{
				final float overlap = SpatialUtility.overlap( a, offset, radius );
				
				if (overlap > 0)
				{
					if (callback.onFound( a, overlap, intersectCount, offset, radius, max, collidesWith ))
					{
						intersectCount++;
						
						if (intersectCount >= max)
						{
							break;
						}
					}
				}
			}

			start = start.next;
		}
		
		return intersectCount;
	}
	
	@Override
	public int contains( Vec offset, float radius, int max, long collidesWith, SearchCallback callback )
	{
		final CellSpan span = getCellSpan( offset, radius, true, new CellSpan() );
		
		int containCount = 0;

		for (int y = span.T; y <= span.B; y++)
		{
			for (int x = span.L; x <= span.R; x++)
			{
				containCount = contains( offset, radius, max, collidesWith, callback, containCount, cells[y][x] );
				
				if (containCount == max)
				{
					y = span.B + 1;
					x = span.R + 1;
				}
			}
		}
		
		if ( containCount < max && isOutsidePartially( offset, radius ) )
		{
			containCount = contains( offset, radius, max, collidesWith, callback, containCount, outside );
		}

		return containCount;
	}

	private int contains( Vec offset, float radius, int max, long collidesWith, SearchCallback callback, int containCount, LinkedList<SpatialGridNode> list )
	{
		final LinkedNode<SpatialGridNode> end = list.head;
		LinkedNode<SpatialGridNode> start = end.next;
		
		while (start != end)
		{
			final LinkedNode<SpatialGridNode> next = start.next;
			final SpatialEntity a = start.value.entity;

			if ( !a.isInert() && (a.getSpatialGroups() & collidesWith) != 0)
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

			start = next;
		}
		
		return containCount;
	}

	@Override
	public int knn( Vec offset, int k, long collidesWith, SpatialEntity[] nearest, float[] distance )
	{
		if (!SpatialUtility.prepareKnn( k, nearest, distance ))
		{
			return 0;
		}
		
		int near = knn( offset, k, collidesWith, nearest, distance, 0, entities );

		if ( isOutsidePartially( offset, 0.0f ) )
		{
			near = knn( offset, k, collidesWith, nearest, distance, near, outside );
		}

		return near;
	}
	
	private int knn( Vec offset, int k, long collidesWith, SpatialEntity[] nearest, float[] distance, int near, LinkedList<SpatialGridNode> list )
	{
		final LinkedNode<SpatialGridNode> end = list.head;
		LinkedNode<SpatialGridNode> start = end.next;
		
		while (start != end)
		{
			final LinkedNode<SpatialGridNode> next = start.next;
			final SpatialEntity a = start.value.entity;

			if (!a.isInert() && (a.getSpatialGroups() & collidesWith) != 0)
			{
				near = SpatialUtility.accumulateKnn( SpatialUtility.distance( a, offset ), a, near, k, distance, nearest );
			}

			start = next;
		}
		
		return near;
	}
	
	private int getCellX( float px )
	{
		return (int)((px - offset.x) * sizeInversed.x);
	}

	private int getCellX( float px, int outsideLeft, int outsideRight )
	{
		final int cx = (int)((px - offset.x) * sizeInversed.x);
		return (cx < 0 ? outsideLeft : (cx >= width ? outsideRight : cx));
	}
	
	private int getCellY( float py )
	{
		return (int)((py - offset.y) * sizeInversed.y);
	}

	private int getCellY( float py, int outsideTop, int outsideBottom )
	{
		final int cy = (int)((py - offset.y) * sizeInversed.y);
		return (cy < 0 ? outsideTop : (cy >= height ? outsideBottom : cy));
	}
	
	private CellSpan getCellSpan(Vec center, float radius, boolean lookback, CellSpan out)
	{
		return getCellSpan( center.x - radius, center.y - radius, center.x + radius, center.y + radius, lookback, out );
	}
	
	private CellSpan getCellSpan(float left, float top, float right, float bottom, boolean lookback, CellSpan out)
	{
		out.L = getCellX( left, 0, width - 1 );
		out.R = getCellX( right, 0, width - 1 );
		out.T = getCellY( top, 0, height - 1 );
		out.B = getCellY( bottom, 0, height - 1 );
		
		if ( lookback )
		{
			int maxX = 0;
			int maxY = 0;
			
			for (int y = out.T; y <= out.B; y++)
			{
				maxX = Math.max( maxX, cells[y][out.L].lookbackX );
			}
			
			for (int x = out.L; x <= out.R; x++)
			{
				maxY = Math.max( maxY, cells[out.T][x].lookbackY );
			}
			
			out.L = Math.max( 0, out.L - maxX );
			out.T = Math.max( 0, out.T - maxY );
		}
		
		return out;
	}
	
	public boolean isOutsidePartially(Vec center, float radius)
	{
		return (center.x - radius < l || 
				  center.x + radius >= r ||
				  center.y - radius < t || 
				  center.y + radius >= b);
	}
	
	public class CellSpan
	{
		public int L, T, R, B;
	}

}
