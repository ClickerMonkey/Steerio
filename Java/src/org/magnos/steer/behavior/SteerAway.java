package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.vec.Vec;

/**
 * A steering behavior that moves the subject away from a target at maximum 
 * acceleration.
 */
public class SteerAway<V extends Vec<V>> extends AbstractSteer<V, SteerAway<V>>
{

	public Target<V> target;
	public boolean shared;
    
    public SteerAway( float minimum, float maximum, Target<V> target )
    {
        this( minimum, maximum, target, DEFAULT_SHARED );
    }
    
    public SteerAway( float magnitude, Target<V> target )
    {
        this( magnitude, target, DEFAULT_SHARED );
    }
    
    public SteerAway( float magnitude, Target<V> target, boolean shared )
    {
        this( magnitude, magnitude, target, shared );
    }
	
	public SteerAway( float minimum, float maximum, Target<V> target, boolean shared )
	{
	    super( minimum, maximum );
	    
		this.target = target;
		this.shared = shared;
	}

	@Override
	public float getForce( float elapsed, SteerSubject<V> subject, V out )
	{
		V targetPosition = target.getTarget( subject );
		
		if (targetPosition != null)
		{
			return away(subject, targetPosition, out, this );
		}
		
		return Steer.NONE;
	}
	
	@Override
	public boolean isShared()
	{
		return shared;
	}

	@Override
	public Steer<V> clone()
	{
		return new SteerAway<V>( minimum, maximum, target, shared );
	}

}
