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
		final V acc = subject.getAcceleration().muli( elapsed ).addi( vel );
		float acclength = acc.normalize();
		
		if (acclength != 0.0f)
		{
		    float inner = (float)Math.acos( dir.dot( acc ) );
            float outer = radians * elapsed;
            
		    if (inner > outer)
		    {
		        SteerMath.slerp( dir, acc, inner, outer / inner, acc );
		    }
		    
            acc.muli( acclength ).subi( vel ).divi( elapsed );
		}
	}
	
	
}
