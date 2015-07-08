package org.magnos.steer.behavior;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec;



public class SteerDirection<V extends Vec<V>> extends AbstractSteer<V, SteerDirection<V>>
{

    public SteerDirection( float minimum, float maximum )
    {
        super( minimum, maximum );
    }

    public SteerDirection( float magnitude )
    {
        super( magnitude, magnitude );
    }

    @Override
	public float getForce( float elapsed, SteerSubject<V> subject, V out )
	{
		return forward( subject, subject.getDirection(), out, this );
	}

	@Override
	public boolean isShared()
	{
		return true;
	}

}
