package org.magnos.steer.spatial.grid;

import org.magnos.steer.spatial.base.LinkedListRectangle;

// A cell is list of the entities that exist in the cell's boundaries.
public class SpatialGridCell extends LinkedListRectangle<SpatialGridNode>
{
	// The location of this cell on the grid.
	public final int x, y;
	
	// The look-backs for entity's that overlap cells on the right and bottom.
	public int lookbackX, lookbackY;

	public SpatialGridCell(int cellX, int cellY, SpatialGrid db) 
	{
		super ( cellX * db.size.x + db.offset.x, 
			     cellY * db.size.y + db.offset.y,
			     cellX * db.size.x + db.offset.x + db.size.x, 
			     cellY * db.size.y + db.offset.y + db.size.y );
		
		this.x = cellX; 
		this.y = cellY;
		this.lookbackX = 0;
		this.lookbackY = 0;
	}
	
}