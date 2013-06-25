package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;

/**
 * A steering behavior that moves the subject away when a target is within a 
 * given distance and the target is facing the subject.
 */
public class SteerAvoid extends AbstractSteer
{
	public SteerSubject target;
	public float distance;
	public boolean shared;
	
	public SteerAvoid(SteerSubject target, float distance)
	{
		this( target, distance, true );
	}
	
	public SteerAvoid(SteerSubject target, float distance, boolean shared)
	{
		this.target = target;
		this.distance = distance;
		this.shared = shared;
	}

	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		force.clear();

		SteerSubject target = getTarget();
		Vector targetPosition = target.getPosition();
		Vector targetDirection = target.getDirection();
		Vector subjectPosition = subject.getPosition();
		
		if ( distance == INFINITE || targetPosition.distanceSq( subjectPosition ) < distance * distance )
		{
			if (inFront( targetPosition, targetDirection, subjectPosition ))
			{
				away(subject, targetPosition, force );
			}
		}
		
		return force;
	}
	
	@Override
	public boolean isShared()
	{
		return shared;
	}
	
	public SteerSubject getTarget()
	{
		return target;
	}

	@Override
	public Steer clone()
	{
		return new SteerAvoid( target, distance, shared );
	}

}
