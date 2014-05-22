
package org.magnos.steer.spatial.grid;

import org.magnos.steer.spatial.base.LinkedListBounds;
import org.magnos.steer.vec.Vec;


// A cell is list of the entities that exist in the cell's boundaries.
public class SpatialGridCell<V extends Vec<V>> extends LinkedListBounds<V, SpatialGridNode<V>>
{

    // The location of this cell on the grid.
    public final V index;
    
    // The offset of this cell in the grid
    public final int offset;
    
    // The look-backs for entity's that overlap cells on the right and bottom.
    public V lookback;

    public SpatialGridCell( V index, int offset, SpatialGrid<V> db )
    {
        super( calculateMin( index, db ), calculateMax( index, db ) );
        
        this.index = index;
        this.offset = offset;
        this.lookback = index.create();
    }
    
    public static <V extends Vec<V>> V calculateMin( V index, SpatialGrid<V> db )
    {
        V min = index.clone();
        min.muli( db.size );
        min.addi( db.offset );
        return min;
    }
    
    public static <V extends Vec<V>> V calculateMax( V index, SpatialGrid<V> db )
    {
        return calculateMin( index, db ).addi( db.size );
    }

}
