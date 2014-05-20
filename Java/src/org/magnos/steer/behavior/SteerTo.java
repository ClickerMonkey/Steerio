package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.Vector;


/**
 * A steering behavior that moves the subject closer to a target with maximum
 * acceleration if their within a certain distance.
 */
public class SteerTo extends AbstractSteer
{
	public Target target;
	public boolean shared;
	
	public SteerTo(Target target)
	{
		this( target, true );
	}
	
	public SteerTo(Target target, boolean shared)
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
			towards(subject, targetPosition, force, this );
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
		return new SteerTo( target, shared );
	}

}
