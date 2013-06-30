package org.magnos.steer.contraints;

import org.magnos.steer.Constraint;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;

/**
 * A steering constraint on the turning of an object.
 */
public class ConstraintTurning implements Constraint
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
	
	public void constrain(float elapsed, SteerSubject subject)
	{
		final Vector v = subject.getDirection();
		final Vector a = subject.getAcceleration();
		float alength = a.length();
		float inner = (float)Math.acos( v.dot(a) / alength );
		
		if (inner > radians)
		{
			a.set( v );
			a.rotatei( v.cross( a ) > 0 ? radians : SteerMath.PI2 - radians );
			a.length( alength );
		}
	}
	
}
