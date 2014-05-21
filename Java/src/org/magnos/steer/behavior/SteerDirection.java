package org.magnos.steer.behavior;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec;



public class SteerDirection<V extends Vec<V>> extends AbstractSteer<V>
{

	@Override
	public void getForce( float elapsed, SteerSubject<V> subject, V out )
	{
		forward( subject, subject.getDirection(), out, this );
	}

	@Override
	public boolean isShared()
	{
		return true;
	}

}
