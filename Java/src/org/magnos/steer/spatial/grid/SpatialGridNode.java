
package org.magnos.steer.spatial.grid;

import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.util.LinkedNode;
import org.magnos.steer.vec.Vec;


// A SpatialNode holds a reference to a node for its cell, its database, 
public class SpatialGridNode<V extends Vec<V>>
{

    // The node into the list of this entities cell
    public final LinkedNode<SpatialGridNode<V>> cellNode;

    // The node into the spatial database list.
    public final LinkedNode<SpatialGridNode<V>> databaseNode;

    // The entity of the spatial node.
    public final SpatialEntity<V> entity;

    // The cell the entity currently exists in.
    public SpatialGridCell<V> cell;

    // Creates a new SpatialNode for the given entity.
    public SpatialGridNode( SpatialEntity<V> spatialEntity )
    {
        entity = spatialEntity;
        cellNode = new LinkedNode<SpatialGridNode<V>>( this );
        databaseNode = new LinkedNode<SpatialGridNode<V>>( this );
    }

    // Removes the node from the spatial database and its cell.
    public void remove()
    {
        cellNode.remove();
        databaseNode.remove();
    }
}
