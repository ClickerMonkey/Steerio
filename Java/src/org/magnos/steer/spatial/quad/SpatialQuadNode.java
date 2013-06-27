package org.magnos.steer.spatial.quad;

import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.util.LinkedNode;


public class SpatialQuadNode
{

	// The node into the list of this entities quad
	public final LinkedNode<SpatialQuadNode> treeNode;
	// The node into the spatial database list.
	public final LinkedNode<SpatialQuadNode> databaseNode;
	// The entity of the spatial node.
	public final SpatialEntity entity;
	// The tree the entity currently exists in.
	public SpatialQuadTree tree;
	
	// Creates a new SpatialNode for the given entity.
	public SpatialQuadNode( SpatialEntity spatialEntity )
	{
		entity = spatialEntity;
		treeNode = new LinkedNode<SpatialQuadNode>( this );
		databaseNode = new LinkedNode<SpatialQuadNode>( this );
	}

	// Removes the node from the spatial database and its cell.
	public void remove()
	{
		treeNode.remove();
		databaseNode.remove();
	}
	
}
