package org.magnos.steer.target;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Filter;
import org.magnos.steer.Target;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;

// TODO factor in the estimated cost of turning to face the weakest subject
public class TargetWeakest<V extends Vec<V>> implements Target<V>, SearchCallback<V>
{

	public SpatialDatabase<V> space;
    public Filter<V> filter;
	public float queryOffset;
	public float queryRadius;
	public boolean contains;
	public int max;
	public long groups;
	
	public SteerSubject<V> subject;
	public float weakestTime;
	public SpatialEntity<V> weakest;
	public final V queryPosition;

	public TargetWeakest(SpatialDatabase<V> space, Filter<V> filter, float queryOffset, float queryRadius, boolean contains, int max, long groups, V template)
	{
		this.space = space;
		this.filter = filter;
		this.queryOffset = queryOffset;
		this.queryRadius = queryRadius;
		this.contains = contains;
		this.max = max;
		this.groups = groups;
		this.queryPosition = template.create();
	}
	
	@Override
	public SpatialEntity<V> getTarget( SteerSubject<V> ss )
	{
		weakest = null;
		weakestTime = Float.MAX_VALUE;
		subject = ss;
		
		queryPosition.set( ss.getPosition() );
		queryPosition.addsi( ss.getDirection(), queryOffset );
		
		int found = 0;
		
		if ( contains )
		{
			found = space.contains( queryPosition, queryRadius, max, groups, this );
		}
		else
		{
			found = space.intersects( queryPosition, queryRadius, max, groups, this );
		}
		
		if ( found == 0 || weakestTime == Float.MAX_VALUE )
		{
			return null;
		}
		
		return weakest;
	}

	@Override
	public boolean onFound( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
	    if ( filter != null && !filter.isValid( subject, entity ) )
	    {
	        return false;
	    }
	    
	    boolean found = false;
		float time = SteerMath.interceptTime( subject.getPosition(), subject.getMaximumVelocity(), entity.getPosition(), entity.getVelocity() );
		
		if ( time > 0 )
		{
			if ( time < weakestTime )
			{
				weakestTime = time;
				weakest = entity;
			}
			
			found = true;
		}
		
		return found;
	}

}
