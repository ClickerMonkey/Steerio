package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec;

/**
 * A steering behavior with a constant force.
 */
public class SteerConstant<V extends Vec<V>> extends AbstractSteer<V>
{
	public final V force;
	public boolean shared;
	
	public SteerConstant(V force)
	{
		this( force, true );
	}
	
	public SteerConstant(V force, boolean shared)
	{
		this.force = force;
		this.shared = shared;
	}
	
	@Override
	public void getForce( float elapsed, SteerSubject<V> subject, V out )
	{
	    out.set( force );
	}

	@Override
	public boolean isShared()
	{
		return shared;
	}

	@Override
	public Steer<V> clone()
	{
		return new SteerConstant<V>( shared ? force : force.clone(), shared );
	}

}
