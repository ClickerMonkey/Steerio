
package org.magnos.steer;

import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;

public interface Filter<V extends Vec<V>>
{

    public boolean isValid( SteerSubject<V> subject, SpatialEntity<V> test );
}
