package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.Vector;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;


public class TargetSlowest implements Target, SearchCallback
{

	public SpatialDatabase space;
	public float queryOffset;
	public float queryRadius;
	public boolean contains;
	public int max;
	public long groups;
	
	public float slowestVelocitySq;
	public final Vector queryPosition = new Vector();
	public final Vector target = new Vector();

	public TargetSlowest(SpatialDatabase space, float queryOffset, float queryRadius, boolean contains, int max, long groups)
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
		slowestVelocitySq = Float.MAX_VALUE;
		
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
		
		return target;
	}

	@Override
	public boolean onFound( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
		boolean applicable = (entity instanceof SteerSubject);

		if ( applicable )
		{
			SteerSubject subject = (SteerSubject)entity;
			
			float vsq = subject.getVelocity().lengthSq();
			
			if ( vsq < slowestVelocitySq )
			{
				slowestVelocitySq = vsq;
				target.set( subject.getPosition() );
			}
		}
		
		return applicable;
	}

}
