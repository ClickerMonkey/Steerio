
package org.magnos.steer.spatial.grid;

import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.util.LinkedNode;


// A SpatialNode holds a reference to a node for its cell, its database, 
public class SpatialGridNode
{

	// The node into the list of this entities cell
	public final LinkedNode<SpatialGridNode> cellNode;
	// The node into the spatial database list.
	public final LinkedNode<SpatialGridNode> databaseNode;
	// The entity of the spatial node.
	public final SpatialEntity entity;
	// The cell the entity currently exists in.
	public SpatialGridCell cell;

	// Creates a new SpatialNode for the given entity.
	public SpatialGridNode( SpatialEntity spatialEntity )
	{
		entity = spatialEntity;
		cellNode = new LinkedNode<SpatialGridNode>( this );
		databaseNode = new LinkedNode<SpatialGridNode>( this );
	}

	// Removes the node from the spatial database and its cell.
	public void remove()
	{
		cellNode.remove();
		databaseNode.remove();
	}
}
