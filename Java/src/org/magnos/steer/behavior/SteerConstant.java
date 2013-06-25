package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;

/**
 * A steering behavior with a constant force.
 */
public class SteerConstant extends AbstractSteer
{
	public final Vector force;
	public boolean shared;
	
	public SteerConstant(Vector force)
	{
		this( force, true );
	}
	
	public SteerConstant(Vector force, boolean shared)
	{
		this.force = force;
		this.shared = shared;
	}
	
	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		return force;
	}

	@Override
	public boolean isShared()
	{
		return shared;
	}

	@Override
	public Steer clone()
	{
		return new SteerConstant( shared ? force : force.clone(), shared );
	}

}
