package org.magnos.steer.behavior;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;


public class SteerFixed extends AbstractSteer
{

	public Vector fixed;
	
	public SteerFixed(Vector fixed)
	{
		this.fixed = fixed;
	}
	
	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		force.set( fixed );
		
		return force;
	}

	@Override
	public boolean isShared()
	{
		return true;
	}

}
