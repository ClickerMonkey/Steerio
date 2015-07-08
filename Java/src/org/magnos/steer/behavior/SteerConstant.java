package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec;

/**
 * A steering behavior with a constant force.
 */
public class SteerConstant<V extends Vec<V>> extends AbstractSteer<V, SteerConstant<V>>
{
	public final V force;
	public boolean shared;
    
    public SteerConstant( float minimum, float maximum, V force )
    {
        this( minimum, maximum, force, DEFAULT_SHARED );
    }
    
    public SteerConstant( float magnitude, V force )
    {
        this( magnitude, magnitude, force, DEFAULT_SHARED );
    }
    
    public SteerConstant( float magnitude, V force, boolean shared )
    {
        this( magnitude, magnitude, force, shared );
    }
	
	public SteerConstant( float minimum, float maximum, V force, boolean shared )
	{
	    super( minimum, maximum );
	    
		this.force = force;
		this.shared = shared;
	}
	
	@Override
	public float getForce( float elapsed, SteerSubject<V> subject, V out )
	{
	    return forceFromVector( this, out.set( force ) );
	}

	@Override
	public boolean isShared()
	{
		return shared;
	}

	@Override
	public Steer<V> clone()
	{
		return new SteerConstant<V>( minimum, maximum, shared ? force : force.clone(), shared );
	}

}
