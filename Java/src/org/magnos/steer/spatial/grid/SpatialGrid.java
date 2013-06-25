
package org.magnos.steer.spatial.grid;

import org.magnos.steer.Vector;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
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
		// Calculate cell index
		final int cx = getCellX( entity.getPosition() );
		final int cy = getCellY( entity.getPosition() );

		// Create node for entity
		final SpatialGridNode node = new SpatialGridNode( entity );

		// If they are outside the database...
		if (cx == -1 || cy == -1)
		{
			outside.add( node.cellNode );
		}
		// Else add them to the appropriate cell.
		else
		{
			cells[cy][cx].add( node.cellNode );
			node.cell = cells[cy][cx];
		}

		// Add the entity to the database entity list.
		entities.add( node.databaseNode );
	}

	// Updates which cell each entity exists in.
	public int refresh()
	{
		int alive = 0;

		remapped = 0;

		LinkedNode<SpatialGridNode> linkedNode = entities.head.next;

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
				final int cx = getCellX( entity.getPosition() );
				final int cy = getCellY( entity.getPosition() );

				// If the index is not equal to the cell the entity is actually
				// in...
				if ((node.cell == null && (cx != -1 || cy != -1)) || (node.cell != null && (cx != node.cell.x || cy != node.cell.y)))
				{
					// Remove them from their current cell...
					final LinkedNode<SpatialGridNode> cellNode = node.cellNode;
					cellNode.remove();

					// Either add them outside or add them to the appropriate cell.
					if (cx == -1 || cy == -1)
					{
						outside.add( cellNode );
						node.cell = null;
					}
					else
					{
						cells[cy][cx].add( cellNode );
						node.cell = cells[cy][cx];
					}
					remapped++;
				}
			}

			// Next entity...
			linkedNode = nextNode;
		}

		return alive;
	}

	@Override
	public int intersects( Vector offset, float radius, int max, long collidesWith, SearchCallback callback )
	{
		int intersectCount = 0;
		int cellL = floor( (offset.x - radius) * sizeInversed.x, 0, width );
		int cellR = ceil( (offset.x + radius) * sizeInversed.x, 0, width );
		int cellT = floor( (offset.y - radius) * sizeInversed.y, 0, height );
		int cellB = ceil( (offset.y + radius) * sizeInversed.y, 0, height );

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
						final float overlap = getOverlap( entity, offset, radius );
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
		int cellL = floor( (offset.x - radius) * sizeInversed.x, 0, width );
		int cellR = ceil( (offset.x + radius) * sizeInversed.x, 0, width );
		int cellT = floor( (offset.y - radius) * sizeInversed.y, 0, height );
		int cellB = ceil( (offset.y + radius) * sizeInversed.y, 0, height );

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
						if (contains( entity, offset, radius ))
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
				final float overlap = -getOverlap( a, offset, 0 );
				int place = 0;

				while (place < near && overlap > distance[place])
				{
					place++;
				}

				if (place < k)
				{
					final int first = Math.max( 1, place );

					for (int i = near - 1; i > first; i--)
					{
						nearest[i] = nearest[i - 1];
						distance[i] = distance[i - 1];
					}

					nearest[place] = a;
					distance[place] = overlap;

					if (near < k)
					{
						near++;
					}
				}
			}

			cellNode = nextNode;
		}

		return near;
	}

	// Computes the x cell the entity exists in, or -1 if it doesn't exist.
	private int getCellX( Vector pos )
	{
		final int cx = (int)((pos.x - offset.x) * sizeInversed.x);
		return (cx < 0 || cx >= width ? -1 : cx);
	}

	// Computes the y cell the entity exists in, or -1 if it doesn't exist.
	private int getCellY( Vector pos )
	{
		final int cy = (int)((pos.y - offset.y) * sizeInversed.y);
		return (cy < 0 || cy >= height ? -1 : cy);
	}

	private float getOverlap( SpatialEntity a, Vector bpos, float bradius )
	{
		final Vector apos = a.getPosition();
		final float dx = apos.x - bpos.x;
		final float dy = apos.y - bpos.y;
		final float sr = a.getRadius() + bradius;
		return (sr * sr) - (dx * dx + dy * dy);
	}

	private boolean contains( SpatialEntity a, Vector bpos, float bradius )
	{
		final Vector apos = a.getPosition();
		final float dx = apos.x - bpos.x;
		final float dy = apos.y - bpos.y;
		final float mr = bradius - a.getRadius();
		return (dx * dx + dy * dy) <= (mr * mr);
	}

	private int floor( float amount, int min, int max )
	{
		int v = (int)Math.floor( amount );
		if (v < min) v = min;
		if (v > max) v = max;
		return v;
	}

	private int ceil( float amount, int min, int max )
	{
		int v = (int)Math.ceil( amount );
		if (v < min) v = min;
		if (v > max) v = max;
		return v;
	}
}
