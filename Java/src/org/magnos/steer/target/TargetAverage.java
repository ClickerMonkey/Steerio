package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.Vector;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;


public class TargetAverage implements Target, SearchCallback
{

	public SpatialDatabase space;
	public float queryOffset;
	public float queryRadius;
	public boolean contains;
	public int max;
	public long groups;
	
	public final Vector queryPosition = new Vector();
	public final Vector sum = new Vector();

	public TargetAverage(SpatialDatabase space, float queryOffset, float queryRadius, boolean contains, int max, long groups)
	{
		this.space = space;
		this.queryOffset = queryOffset;
		this.queryRadius = queryRadius;
		this.contains = contains;
		this.max = max;
		this.groups = groups;
	}
	
	@Override
	public Vector getTarget( SteerSubject subject )
	{
		sum.clear();
		
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
		
		sum.divi( found );
		
		return sum;
	}

	@Override
	public boolean onFound( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
		sum.addi( entity.getPosition() );
		
		return true;
	}

}
