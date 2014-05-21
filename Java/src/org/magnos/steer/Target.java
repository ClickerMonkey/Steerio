package org.magnos.steer;

import org.magnos.steer.vec.Vec;

public interface Target<V extends Vec<V>>
{
	public V getTarget( SteerSubject<V> subject );
}
