package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.vec.Vec;

/**
 * A steering behavior that moves the subject away from a target at maximum 
 * acceleration.
 */
public class SteerAway<V extends Vec<V>> extends AbstractSteer<V>
{

	public Target<V> target;
	public boolean shared;
	
	public SteerAway(Target<V> target)
	{
		this( target, true );
	}
	
	public SteerAway(Target<V> target, boolean shared)
	{
		this.target = target;
		this.shared = shared;
	}

	@Override
	public void getForce( float elapsed, SteerSubject<V> subject, V out )
	{
		V targetPosition = target.getTarget( subject );
		
		if (targetPosition != null)
		{
			away(subject, targetPosition, out, this );
		}
	}
	
	@Override
	public boolean isShared()
	{
		return shared;
	}

	@Override
	public Steer<V> clone()
	{
		return new SteerAway<V>( target, shared );
	}

}
