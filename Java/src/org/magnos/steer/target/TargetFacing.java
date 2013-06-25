package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.Vector;
import org.magnos.steer.behavior.AbstractSteer;



public class TargetFacing implements Target
{

	public Target target;
	public Vector direction;
	public boolean front;
	
	public TargetFacing(SteerSubject target, boolean front)
	{
		this( target, target.getDirection(), front );
	}
	
	public TargetFacing(Target target, Vector direction, boolean front)
	{
		this.target = target;
		this.direction = direction;
		this.front = front;
	}
	
	@Override
	public Vector getTarget( SteerSubject subject )
	{
		Vector actual = target.getTarget( subject );
		
		if ( actual == null )
		{
			return null;
		}
		
		if (AbstractSteer.inFront( actual, direction, subject.getPosition() ) != front)
		{
			return null;
		}
		
		return actual;
	}

}
