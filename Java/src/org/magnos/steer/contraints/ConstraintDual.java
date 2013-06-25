package org.magnos.steer.contraints;

import org.magnos.steer.Constraint;
import org.magnos.steer.SteerSubject;


public class ConstraintDual implements Constraint
{
	
	public Constraint first;
	public Constraint second;

	public ConstraintDual(Constraint first, Constraint second)
	{
		this.first = first;
		this.second = second;
	}
	
	@Override
	public void constrain( float elapsed, SteerSubject subject )
	{
		first.constrain( elapsed, subject );
		second.constrain( elapsed, subject );
	}

}
