
package org.magnos.steer.spatial.quad;

import org.magnos.steer.spatial.CollisionCallback;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialUtility;
import org.magnos.steer.vec.Vec;


public class SpatialQuadTree<V extends Vec<V>> implements SpatialDatabase<V>
{

    public int desiredLeafSize;
    public int refreshThreshold;
    public SpatialQuadNode<V> root;

    public SpatialQuadTree( V min, V max, int desiredLeafSize, int refreshThreshold )
    {
        this.root = new SpatialQuadNode<V>( null, min, max );
        this.desiredLeafSize = desiredLeafSize;
        this.refreshThreshold = refreshThreshold;
    }

    @Override
    public void add( SpatialEntity<V> entity )
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
    public int handleCollisions( CollisionCallback<V> callback )
    {
        callback.onCollisionStart();
        int collisionCount = root.handleCollisions( callback );
        callback.onCollisionEnd();

        return collisionCount;
    }

    @Override
    public int intersects( V offset, float radius, int max, long collidesWith, SearchCallback<V> callback )
    {
        return root.intersects( offset, radius, max, collidesWith, callback, 0 );
    }

    @Override
    public int contains( V offset, float radius, int max, long collidesWith, SearchCallback<V> callback )
    {
        return root.contains( offset, radius, max, collidesWith, callback, 0 );
    }

    @Override
    public int knn( V point, int k, long collidesWith, SpatialEntity<V>[] nearest, float[] distance )
    {
        if ( !SpatialUtility.prepareKnn( k, nearest, distance ) )
        {
            return 0;
        }

        return root.knn( point, k, collidesWith, nearest, distance, 0 );
    }

}
