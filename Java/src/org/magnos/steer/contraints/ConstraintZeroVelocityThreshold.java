package org.magnos.steer.contraints;

import org.magnos.steer.Constraint;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;


public class ConstraintZeroVelocityThreshold implements Constraint
{
	
	public float threshold;

	public ConstraintZeroVelocityThreshold(float threshold)
	{
		this.threshold = threshold;
	}
	
	@Override
	public void constrain( float elapsed, SteerSubject subject )
	{
		final Vector v = subject.getVelocity();
		
		if ( v.lengthSq() < threshold * threshold )
		{
			v.clear();
		}
	}

}
