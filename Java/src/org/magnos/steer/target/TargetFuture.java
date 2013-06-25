package org.magnos.steer.target;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.Vector;



public class TargetFuture implements Target
{
	
	public Vector position;
	public Vector velocity;
	
	public final Vector future = new Vector();
	
	public TargetFuture(SteerSubject target)
	{
		this( target.getPosition(), target.getVelocity() );
	}
	
	public TargetFuture(Vector position, Vector velocity)
	{
		this.position = position;
		this.velocity = velocity;
	}
	
	@Override
	public Vector getTarget(SteerSubject subject)
	{
		future.set( position );
		
		float time = SteerMath.intersectionTime( position, velocity, subject.getPosition(), subject.getVelocity() );
		
		if (time > 0)
		{
			future.addsi( velocity, time );
		}
		
		return future;
	}
	
}
