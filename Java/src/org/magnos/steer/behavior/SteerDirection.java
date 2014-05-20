package org.magnos.steer.behavior;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;



public class SteerDirection extends AbstractSteer
{

	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		force.clear();
		
		forward( subject, subject.getDirection(), force, this );
		
		return force;
	}

	@Override
	public boolean isShared()
	{
		return true;
	}

}
