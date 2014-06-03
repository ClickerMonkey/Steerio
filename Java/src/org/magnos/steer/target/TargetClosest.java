package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Filter;
import org.magnos.steer.Target;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


public class TargetClosest<V extends Vec<V>> implements Target<V>
{
    
	public static int DEFAULT_CHECK = 1;
	
	public SpatialDatabase<V> space;
	public Filter<V, SpatialEntity<V>> filter;
	public float maximum;
	public long groups;
	public SpatialEntity<V> chosen;
	public SpatialEntity<V>[] closest;
	public float[] closestDistance;
	
	public TargetClosest(SpatialDatabase<V> space, Filter<V, SpatialEntity<V>> filter, float maximum, long groups)
	{
		this( space, filter, maximum, groups, DEFAULT_CHECK );
	}
	
	public TargetClosest(SpatialDatabase<V> space, Filter<V, SpatialEntity<V>> filter, float maximum, long groups, int check)
	{
		this.space = space;
		this.filter = filter;
		this.maximum = maximum;
		this.groups = groups;
		this.closest = new SpatialEntity[ check ];
		this.closestDistance = new float[ check ];
	}
	
	@Override
	public V getTarget( SteerSubject<V> subject )
	{
		chosen = null;
		
		int found = space.knn( subject.getPosition(), closest.length, groups, closest, closestDistance );

		if ( found == 0 )
		{
			return null;
		}
		
		for (int i = 0; i < found; i++)
		{
			SpatialEntity<V> c = closest[i];
			
			if (c != null && (filter == null || filter.isValid( subject, c )))
			{
			    chosen = c;
			    
			    break;
			}
		}
		
		return ( chosen == null  ? null : chosen.getPosition() );
	}

}
