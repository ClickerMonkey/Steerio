package org.magnos.steer.contraints;

import org.magnos.steer.Constraint;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec2;

/**
 * A steering constraint on the turning of an object.
 */
public class ConstraintTurning2 implements Constraint<Vec2>
{
	
	public float radians;
	
	public ConstraintTurning2()
	{
		this( SteerMath.PI * 0.5f );
	}
	
	public ConstraintTurning2(float radians)
	{
		this.radians = radians;
	}
	
	public void constrain(float elapsed, SteerSubject<Vec2> subject)
	{
		final Vec2 v = subject.getDirection();
		final Vec2 a = subject.getAcceleration();
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
