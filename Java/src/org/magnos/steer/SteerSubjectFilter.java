
package org.magnos.steer;

import org.magnos.steer.vec.Vec;

public interface SteerSubjectFilter<V extends Vec<V>, T>
{

    public boolean isValid( SteerSubject<V> subject, T test );
}
