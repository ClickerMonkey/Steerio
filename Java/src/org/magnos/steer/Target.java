package org.magnos.steer;

import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;

public interface Target<V extends Vec<V>>
{
	public SpatialEntity<V> getTarget( SteerSubject<V> subject );
}
