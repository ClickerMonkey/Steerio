package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Filter;
import org.magnos.steer.Target;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


public class TargetSlowest<V extends Vec<V>> implements Target<V>, SearchCallback<V>
{

	public SpatialDatabase<V> space;
    public Filter<V> filter;
	public float queryOffset;
	public float queryRadius;
	public boolean contains;
	public int max;
	public long groups;
	
	public float slowestVelocitySq;
	public final V queryPosition;

	public SpatialEntity<V> slowest;
	
	protected SteerSubject<V> subject;
	
	public TargetSlowest(SpatialDatabase<V> space, Filter<V> filter, float queryOffset, float queryRadius, boolean contains, int max, long groups, V template)
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
	public SpatialEntity<V> getTarget( SteerSubject<V> s )
	{
	    subject = s;
	    slowest = null;
		slowestVelocitySq = Float.MAX_VALUE;
		
		queryPosition.set( s.getPosition() );
		queryPosition.addsi( s.getDirection(), queryOffset );
		
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
		
		return slowest;
	}

	@Override
	public boolean onFound( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
	    if ( filter != null && !filter.isValid( subject, entity ) )
	    {
	        return false;
	    }
	    
	    float vsq = entity.getVelocity().lengthSq();
        
        if ( vsq < slowestVelocitySq )
        {
            slowestVelocitySq = vsq;
            slowest = entity;
        }

        return true;
	}

}
