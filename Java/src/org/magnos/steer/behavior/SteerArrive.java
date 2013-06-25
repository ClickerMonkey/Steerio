package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.Vector;

/**
 * A steering behavior that moves the subject closer to a target with maximum
 * acceleration but slows down to a complete stop once it comes within a given
 * distance.
 */
public class SteerArrive extends AbstractSteer
{

	public Target target;
	public float caution;
	public float arrived;
	public boolean shared;
	
	public SteerArrive(Target target, float caution, float arrived)
	{
		this( target, caution, arrived, true );
	}
	
	public SteerArrive(Target target, float caution, float arrived, boolean shared)
	{
		this.target = target;
		this.caution = caution;
		this.arrived = arrived;
		this.shared = shared;
	}

	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		Vector targetPosition = target.getTarget( subject );
		
		if (targetPosition == null)
		{
			force.clear();
		}
		else
		{
			force.directi( subject.getPosition(), targetPosition );
			
			float distance = force.length();
			
			if (distance > arrived)
			{
				float factor = Math.min( distance / caution, 1 ); 
				
				force.divi( distance );
				force.muli( subject.getAccelerationMax() );
				force.muli( factor * factor );
				force.subi( subject.getVelocity() );
				force.divi( elapsed );
			}
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
		return new SteerArrive( target, caution, arrived, shared );
	}
	
}
