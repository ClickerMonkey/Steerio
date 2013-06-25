package org.magnos.steer.behavior;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;



public class SteerFollower extends AbstractSteer
{
	
	public SteerSubject leader;
	public float distance;
	public boolean shared;
	
	private Vector future = new Vector();
	private Vector closest = new Vector();
	
	public SteerFollower(SteerSubject leader, float distance)
	{
		this( leader, distance, true );
	}
	
	public SteerFollower(SteerSubject leader, float distance, boolean shared)
	{
		this.leader = leader;
		this.distance = distance;
		this.shared = shared;
	}
	
	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		force.clear();
		
		future.set( leader.getPosition() );
		future.add( leader.getVelocity() );
		
		SteerMath.closest( leader.getPosition(), future, subject.getPosition(), closest );
		
		float distanceSq = closest.dot( subject.getPosition() );
		
		if (distanceSq <= distance * distance)
		{
			away( subject, closest, force );
		}
		
		return force;
	}
	
	@Override
	public boolean isShared()
	{
		return shared;
	}
	
}
