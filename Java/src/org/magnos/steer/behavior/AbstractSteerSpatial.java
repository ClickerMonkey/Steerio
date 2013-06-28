package org.magnos.steer.behavior;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;

/**
 * Abstract steering behavior that cares about other Steerables around it.
 */
public abstract class AbstractSteerSpatial extends AbstractSteer implements SearchCallback
{
	
	public boolean shared;
	public long groups;
	public float query;
	public int max;
	public SpatialDatabase space;
	public SteerSubject subject;
	
	public AbstractSteerSpatial(SpatialDatabase space, float query, long groups, int max, boolean shared)
	{
		this.space = space;
		this.query = query;
		this.groups = groups;
		this.max = max;
		this.shared = shared;
	}
	
	@Override
	public boolean isShared()
	{
		return shared;
	}
	
	protected int search( SteerSubject ss )
	{
		subject = ss;
		
		return space.intersects( ss.getPosition(), query, max, groups, this );
	}
	
}
