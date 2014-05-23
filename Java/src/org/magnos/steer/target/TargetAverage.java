package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Filter;
import org.magnos.steer.Target;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


public class TargetAverage<V extends Vec<V>> implements Target<V>, SearchCallback<V>
{

	public SpatialDatabase<V> space;
	public Filter<V, SpatialEntity<V>> filter;
	public float queryOffset;
	public float queryRadius;
	public boolean contains;
	public int max;
	public long groups;
	
	public final V queryPosition;
	public final V average;
	
	protected SteerSubject<V> subject;

	public TargetAverage(SpatialDatabase<V> space, Filter<V, SpatialEntity<V>> filter, float queryOffset, float queryRadius, boolean contains, int max, long groups, V template)
	{
		this.space = space;
		this.filter = filter;
		this.queryOffset = queryOffset;
		this.queryRadius = queryRadius;
		this.contains = contains;
		this.max = max;
		this.groups = groups;
		this.queryPosition = template.create();
		this.average = template.create();
	}
	
	@Override
	public V getTarget( SteerSubject<V> s )
	{
	    subject = s;
		average.clear();
		
		queryPosition.set( subject.getPosition() );
		queryPosition.addsi( subject.getDirection(), queryOffset );
		
		int found = 0;
		
		if ( contains )
		{
			found = space.contains( queryPosition, queryRadius, max, groups, this );
		}
		else
		{
			found = space.intersects( queryPosition, queryRadius, max, groups, this );
		}
		
		if ( found == 0 )
		{
			return null;
		}
		
		average.divi( found );
		
		return average;
	}

	@Override
	public boolean onFound( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
	    boolean applicable = filter == null || filter.isValid( subject, entity );
	    
	    if ( applicable )
	    {
	        average.addi( entity.getPosition() );    
	    }
		
		return applicable;
	}

}
