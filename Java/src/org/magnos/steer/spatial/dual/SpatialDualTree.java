package org.magnos.steer.spatial.dual;


import org.magnos.steer.Vector;
import org.magnos.steer.spatial.CollisionCallback;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialUtility;


public class SpatialDualTree implements SpatialDatabase
{

	public int desiredLeafSize;
	public int refreshThreshold;
	public SpatialDualNode root;
	
	public SpatialDualTree(float l, float t, float r, float b, int desiredLeafSize, int refreshThreshold)
	{
		this.root = new SpatialDualNode( null, false, l, t, r, b );
		this.desiredLeafSize = desiredLeafSize;
		this.refreshThreshold = refreshThreshold;
	}
	
	@Override
	public void add( SpatialEntity entity )
	{
		root.add( entity );
	}

	@Override
	public void clear()
	{
		root.destroy();
	}

	@Override
	public int refresh()
	{
		int alive = root.refresh();
		
		root.resize( desiredLeafSize, refreshThreshold );
		
		return alive;
	}

	@Override
	public int handleCollisions( CollisionCallback callback )
	{
		callback.onCollisionStart();
		int collisionCount = root.handleCollisions( callback );
		callback.onCollisionEnd();
		
		return collisionCount;
	}

	@Override
	public int intersects( Vector offset, float radius, int max, long collidesWith, SearchCallback callback )
	{
		return root.intersects( offset, radius, max, collidesWith, callback, 0 );
	}

	@Override
	public int contains( Vector offset, float radius, int max, long collidesWith, SearchCallback callback )
	{
		return root.contains( offset, radius, max, collidesWith, callback, 0 );
	}

	@Override
	public int knn( Vector point, int k, long collidesWith, SpatialEntity[] nearest, float[] distance )
	{
		if (!SpatialUtility.prepareKnn( k, nearest, distance ))
		{
			return 0;
		}
		
		return root.knn( point, k, collidesWith, nearest, distance, 0 );
	}

}
