package org.magnos.steer.target;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.Vector;



public class TargetInLine implements Target
{

	public Target target0;
	public Target target1;
	
	public final Vector closest = new Vector();
	
	public TargetInLine(Target target0, Target target1)
	{
		this.target0 = target0;
		this.target1 = target1;
	}
	
	@Override
	public Vector getTarget( SteerSubject subject )
	{
		Vector s = target0.getTarget( subject );
		
		if ( s == null )
		{
			return null;
		}
		
		Vector e = target1.getTarget( subject );
		
		if ( e == null )
		{
			return null;
		}
		
		Vector v = subject.getPosition();
		
		float dx = e.x - s.x;
		float dy = e.y - s.y;
		float delta = ((v.x - s.x) * dx + (v.y - s.y) * dy) / (dx * dx + dy * dy);
		
		delta = SteerMath.clamp( delta, 0, 1 );
		
		closest.x = (s.x + delta * dx);
		closest.y = (s.y + delta * dy);
		
		return closest;
	}

}
