
package org.magnos.steer.spatial.grid;

import org.magnos.steer.Vector;
import org.magnos.steer.spatial.CollisionCallback;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialUtility;
import org.magnos.steer.util.LinkedList;
import org.magnos.steer.util.LinkedNode;


// A SpatialDatabase stores SpatialEntitys to be quickly searched and checked for intersections.
public class SpatialGrid implements SpatialDatabase
{

	// A grid of cells, which are a linked list of entities.
	public final SpatialGridCell[][] cells;
	// The number of cells wide the database is.
	public final int width;
	// The number of cells high the database is.
	public final int height;
	// The offset of the database grid, typically at {0,0}
	public final Vector offset;
	// The size of each cell in the database.
	public final Vector size;
	// The inverse of size.
	public final Vector sizeInversed;
	// The list of entities outside the grid.
	public final SpatialGridCell outside;
	// The list of entities in the database.
	public final LinkedList<SpatialGridNode> entities;
	// The boundaries of this database.
	public final float t, b, r, l;

	public int collisionChecks;
	public int collisionMatches;
	public int collisions;
	public int remapped;

	public SpatialGrid( int cellsWide, int cellsHigh, float cellWidth, float cellHeight, float offsetX, float offsetY )
	{
		this.offset = new Vector( offsetX, offsetY );
		this.size = new Vector( cellWidth, cellHeight );
		this.sizeInversed = new Vector( 1 / cellWidth, 1 / cellHeight );
		this.width = cellsWide;
		this.height = cellsHigh;
		this.outside = new SpatialGridCell( -1, -1, this );
		this.entities = new LinkedList<SpatialGridNode>();
		this.t = offset.y;
		this.b = offset.y + cellsHigh * cellHeight;
		this.l = offset.x;
		this.r = offset.x + cellsWide * cellWidth;

		this.cells = new SpatialGridCell[cellsHigh][cellsWide];
		for (int y = 0; y < cellsHigh; y++)
		{
			for (int x = 0; x < cellsWide; x++)
			{
				this.cells[y][x] = new SpatialGridCell( x, y, this );
			}
		}
	}

	// Adds the entity to the database and to
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
	
	private boolean updateNode(SpatialGridNode node)
	{
		final SpatialEntity entity = node.entity;
		final LinkedNode<SpatialGridNode> cellNode = node.cellNode;
		final SpatialGridCell cell = node.cell;
		final Vector pos = entity.getPosition();
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
		
		final int actualX = getCellX( pos.x - rad );
		final int actualY = getCellY( pos.y - rad );
		final int l = getCellX( pos.x - rad, 0, width - 1 );
		final int r = getCellX( pos.x + rad, 0, width - 1 );
		final int t = getCellY( pos.y - rad, 0, height - 1 );
		final int b = getCellY( pos.y + rad, 0, height - 1 );
		
		for (int y = t; y <= b; y++)
		{
			for (int x = l; x <= r; x++)
			{
				SpatialGridCell currentCell = cells[y][x];
				currentCell.lookbackX = Math.max( currentCell.lookbackX, x - actualX );
				currentCell.lookbackY = Math.max( currentCell.lookbackY, y - actualY );
			}
		}
		
		return false;
	}

	// Updates which cell each entity exists in.
	@Override
	public int refresh()
	{
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

			// Next entity...
			linkedNode = nextNode;
		}

		return alive;
	}
	
	@Override
	public int handleCollisions( CollisionCallback callback )
	{
		int collisionCount = 0;
		
		callback.onCollisionStart();
		
		LinkedNode<SpatialGridNode> linkedNode = entities.head.next;

		while (linkedNode != entities.head)
		{
			final SpatialGridNode node = linkedNode.value;
			final SpatialEntity entity = node.entity;
			final Vector pos = entity.getPosition();
			final float rad = entity.getRadius();
			final LinkedNode<SpatialGridNode> nextNode = linkedNode.next;
			
			
			// TODO entity
			
			// Next entity...
			linkedNode = nextNode;
		}
		
		callback.onCollisionEnd();
		
		return collisionCount;
	}

	@Override
	public int intersects( Vector offset, float radius, int max, long collidesWith, SearchCallback callback )
	{
		int intersectCount = 0;
		int cellL = SpatialUtility.floor( (offset.x - radius) * sizeInversed.x, 0, width );
		int cellR = SpatialUtility.ceil( (offset.x + radius) * sizeInversed.x, 0, width );
		int cellT = SpatialUtility.floor( (offset.y - radius) * sizeInversed.y, 0, height );
		int cellB = SpatialUtility.ceil( (offset.y + radius) * sizeInversed.y, 0, height );

		for (int y = cellT; y < cellB; y++)
		{
			for (int x = cellL; x < cellR; x++)
			{
				final SpatialGridCell cell = cells[y][x];
				final LinkedNode<SpatialGridNode> cellHead = cell.head;
				LinkedNode<SpatialGridNode> cellNode = cellHead.next;

				while (cellNode != cellHead)
				{
					final LinkedNode<SpatialGridNode> nextNode = cellNode.next;
					final SpatialEntity entity = cellNode.value.entity;

					if ((entity.getSpatialGroups() & collidesWith) != 0)
					{
						final float overlap = SpatialUtility.getOverlap( entity, offset, radius );
						if (overlap > 0)
						{
							if (callback.onFound( entity, overlap, intersectCount, offset, radius, max, collidesWith ))
							{
								intersectCount++;
								if (intersectCount >= max)
								{
									y = cellB;
									x = cellR;
									break;
								}
							}
						}
					}

					cellNode = nextNode;
				}
			}
		}

		return intersectCount;
	}

	@Override
	public int contains( Vector offset, float radius, int max, long collidesWith, SearchCallback callback )
	{
		int containedCount = 0;
		int cellL = SpatialUtility.floor( (offset.x - radius) * sizeInversed.x, 0, width );
		int cellR = SpatialUtility.ceil( (offset.x + radius) * sizeInversed.x, 0, width );
		int cellT = SpatialUtility.floor( (offset.y - radius) * sizeInversed.y, 0, height );
		int cellB = SpatialUtility.ceil( (offset.y + radius) * sizeInversed.y, 0, height );

		for (int y = cellT; y < cellB; y++)
		{
			for (int x = cellL; x < cellR; x++)
			{
				final SpatialGridCell cell = cells[y][x];
				final LinkedNode<SpatialGridNode> cellHead = cell.head;
				LinkedNode<SpatialGridNode> cellNode = cellHead.next;

				while (cellNode != cellHead)
				{
					final LinkedNode<SpatialGridNode> nextNode = cellNode.next;
					final SpatialEntity entity = cellNode.value.entity;

					if ((entity.getSpatialGroups() & collidesWith) != 0)
					{
						if (SpatialUtility.contains( entity, offset, radius ))
						{
							if (callback.onFound( entity, 0, containedCount, offset, radius, max, collidesWith ))
							{
								containedCount++;
								if (containedCount >= max)
								{
									y = cellB;
									x = cellR;
									break;
								}
							}
						}
					}

					cellNode = nextNode;
				}
			}
		}

		return containedCount;
	}

	@Override
	public int knn( Vector offset, int k, long collidesWith, SpatialEntity[] nearest, float[] distance )
	{
		if (k == 0 || k > nearest.length || k > distance.length)
		{
			return 0;
		}

		for (int i = 0; i < k; i++)
		{
			nearest[i] = null;
			distance[i] = Float.MAX_VALUE;
		}

		int near = 0;

		final LinkedNode<SpatialGridNode> headNode = entities.head;
		LinkedNode<SpatialGridNode> cellNode = headNode.next;

		while (cellNode != headNode)
		{
			final LinkedNode<SpatialGridNode> nextNode = cellNode.next;
			final SpatialEntity a = cellNode.value.entity;

			if ((collidesWith & a.getSpatialGroups()) != 0)
			{
				final float overlap = -SpatialUtility.getOverlap( a, offset, 0 );
				
				near = SpatialUtility.accumulateKnn( overlap, a, near, k, distance, nearest );
			}

			cellNode = nextNode;
		}

		return near;
	}

	// Computes the x cell the entity exists in, or -1 if it doesn't exist.
	private int getCellX( float px, int outsideLeft, int outsideRight )
	{
		final int cx = (int)((px - offset.x) * sizeInversed.x);
		return (cx < 0 ? outsideLeft : (cx >= width ? outsideRight : cx));
	}
	
	private int getCellX( float px )
	{
		return (int)((px - offset.x) * sizeInversed.x);
	}

	// Computes the y cell the entity exists in, or -1 if it doesn't exist.
	private int getCellY( float py, int outsideTop, int outsideBottom )
	{
		final int cy = (int)((py - offset.y) * sizeInversed.y);
		return (cy < 0 ? outsideTop : (cy >= height ? outsideBottom : cy));
	}
	
	private int getCellY( float py )
	{
		return (int)((py - offset.y) * sizeInversed.y);
	}
	
	private void getCellSpan(float left, float top, float right, float bottom, CellSpan out)
	{
		
	}
	
	public class CellSpan
	{
		public int L, T, R, B;
	}

}
