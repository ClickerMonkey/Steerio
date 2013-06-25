package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.Vector;

/**
 * A steering behavior that moves the subject away from a target at maximum 
 * acceleration.
 */
public class SteerAway extends AbstractSteer
{

	public Target target;
	public boolean shared;
	
	public SteerAway(Target target)
	{
		this( target, true );
	}
	
	public SteerAway(Target target, boolean shared)
	{
		this.target = target;
		this.shared = shared;
	}

	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		force.clear();
		
		Vector targetPosition = target.getTarget( subject );
		
		if (targetPosition != null)
		{
			away(subject, targetPosition, force );
		}
		
		return force;
	}
	
	@Override
	public boolean isShared()
	{
		return shared;
	}

	@Override
	public Steer clone()
	{
		return new SteerAway( target, shared );
	}

}
