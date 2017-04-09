package org.magnos.steer.spatial.dual;


import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import org.magnos.steer.spatial.CollisionCallback;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialUtility;
import org.magnos.steer.vec.Vec;


public class SpatialDualTree<V extends Vec<V>> implements SpatialDatabase<V>
{

	public int desiredLeafSize;
	public int refreshThreshold;
	public SpatialDualNode<V> root;
	private SpatialDualTreeIterator iterator;
	
	public SpatialDualTree(V min, V max, int desiredLeafSize, int refreshThreshold)
	{
		this.root = new SpatialDualNode<V>( null, 0, min, max );
		this.desiredLeafSize = desiredLeafSize;
		this.refreshThreshold = refreshThreshold;
		this.iterator = new SpatialDualTreeIterator();
	}
	
	@Override
	public Iterator<SpatialEntity<V>> iterator()
	{
	    return iterator.hasNext() ? new SpatialDualTreeIterator() : iterator.reset();
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
		if (!SpatialUtility.prepareKnn( k, nearest, distance ))
		{
			return 0;
		}
		
		return root.knn( point, k, collidesWith, nearest, distance, 0 );
	}
	
	private class SpatialDualTreeIterator implements Iterator<SpatialEntity<V>>
	{
	    private Queue<SpatialDualNode<V>> queue;
	    
	    public SpatialDualTreeIterator()
	    {
	        queue = new ArrayDeque<SpatialDualNode<V>>();
	    }
	    
	    // TODO
	    public SpatialDualTreeIterator reset()
	    {
	        queue.clear();
	        
	        if ( !root.isEmpty() )
	        {
	            queue.offer( root );   
	        }
	        
	        return this;
	    }

        @Override
        public boolean hasNext()
        {
            return queue.size() > 0;
        }

        @Override
        public SpatialEntity<V> next()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void remove()
        {
            // TODO Auto-generated method stub
            
        }
	    
	}

}
