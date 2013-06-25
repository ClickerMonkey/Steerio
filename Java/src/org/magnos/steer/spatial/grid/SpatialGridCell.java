package org.magnos.steer.spatial.grid;

import org.magnos.steer.util.LinkedList;

// A cell is list of the entities that exist in the cell's boundaries.
public class SpatialGridCell extends LinkedList<SpatialGridNode>
{
	// The boundaries of the cell.
	public final float t, b, r, l;
	// The location of this cell on the grid.
	public final int x, y;

	public SpatialGridCell(int cellX, int cellY, SpatialGrid db) 
	{
		this.x = cellX; 
		this.y = cellY;
		this.t = (y * db.size.y) + db.offset.y;
		this.b = t + db.size.y;
		this.l = (x * db.size.x) + db.offset.x;
		this.r = l + db.size.x;
	}
}