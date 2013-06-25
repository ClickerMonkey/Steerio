package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.Vector;



public class TargetLocal implements Target
{

	public Target target;
	public float minimum;
	public float maximum;
	
	public TargetLocal(Target target, float maximum)
	{
		this( target, 0, maximum );
	}
	
	public TargetLocal(Target target, float minimum, float maximum)
	{
		this.target = target;
		this.minimum = minimum;
		this.maximum = maximum;
	}
	
	@Override
	public Vector getTarget( SteerSubject subject )
	{
		Vector actual = target.getTarget( subject );
		
		if ( actual == null )
		{
			return null;
		}
		
		float distanceSq = actual.distanceSq( subject.getPosition() );
		
		if (minimum != 0 && distanceSq < minimum * minimum)
		{
			return null;
		}
		
		if (maximum != Float.MAX_VALUE && distanceSq > maximum * maximum)
		{
			return null;
		}
		
		return actual;
	}

}
