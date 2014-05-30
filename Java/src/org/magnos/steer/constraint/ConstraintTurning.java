package org.magnos.steer.constraint;

import org.magnos.steer.Constraint;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec;

/**
 * A steering constraint on the turning of an object.
 */
public class ConstraintTurning<V extends Vec<V>> implements Constraint<V>
{
	
	public float radians;
	
	public ConstraintTurning()
	{
		this( SteerMath.PI * 0.5f );
	}
	
	public ConstraintTurning(float radians)
	{
		this.radians = radians;
	}
	
	public void constrain(float elapsed, SteerSubject<V> subject)
	{
		final V dir = subject.getDirection();
		final V vel = subject.getVelocity();
		final V acc = subject.getAcceleration();
		final V desired = vel.adds( acc, elapsed );
		float deslength = desired.normalize();
		
		if (deslength != 0.0f)
		{
		    float inner = (float)Math.acos( dir.dot( desired ) );
            float outer = radians * elapsed;
            
		    if (inner > outer)
		    {
		        SteerMath.slerp( dir, desired, inner, outer / inner, desired );
		    }
		    
            acc.set( desired ).muli( deslength ).subi( vel ).divi( elapsed );
		}
	}
	
	
}
