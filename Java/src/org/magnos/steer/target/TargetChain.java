package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.Vector;


public class TargetChain implements Target
{

	public Target first;
	public Target second;
	
	public TargetChain(Target first, Target second)
	{
		this.first = first;
		this.second = second;
	}
	
	@Override
	public Vector getTarget( SteerSubject subject )
	{
		Vector target = first.getTarget( subject );
		
		if ( target == null )
		{
			target = second.getTarget( subject );
		}
		
		return target;
	}

}
