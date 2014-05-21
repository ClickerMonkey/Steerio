
package org.magnos.steer;

import org.magnos.steer.vec.Vec;


public interface Path<V extends Vec<V>>
{

    public V set( V subject, float delta );
}
