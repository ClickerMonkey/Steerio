package org.magnos.steer.target;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.Vector;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;

// TODO factor in the estimated cost of turning to face the weakest subject
public class TargetWeakest implements Target, SearchCallback
{

	public SpatialDatabase space;
	public float queryOffset;
	public float queryRadius;
	public boolean contains;
	public int max;
	public long groups;
	
	public SteerSubject subject;
	public float weakestTime;
	public SteerSubject weakest;
	public final Vector queryPosition = new Vector();
	public final Vector target = new Vector();

	public TargetWeakest(SpatialDatabase space, float queryOffset, float queryRadius, boolean contains, int max, long groups)
	{
		this.space = space;
		this.queryOffset = queryOffset;
		this.queryRadius = queryRadius;
		this.contains = contains;
		this.max = max;
		this.groups = groups;
	}
	
	@Override
	public Vector getTarget( SteerSubject ss )
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
		
		target.set( weakest.getPosition() );
		target.addsi( weakest.getVelocity(), weakestTime );
		
		return target;
	}

	@Override
	public boolean onFound( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
		boolean applicable = (entity instanceof SteerSubject);
		
		if (applicable)
		{
			SteerSubject ss = (SteerSubject)entity;
			
			float time = SteerMath.interceptTime( subject.getPosition(), subject.getVelocityMax(), ss.getPosition(), ss.getVelocity() );
			
			if ( time > 0 )
			{
				if ( time < weakestTime )
				{
					weakestTime = time;
					weakest = ss;
				}
			}
			else
			{
				applicable = false;
			}
		}
		
		return applicable;
	}

}
