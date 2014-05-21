
package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.vec.Vec;


public class Tween<V extends Vec<V>> implements Path<V>
{

    public V start;
    public V end;

    public Tween()
    {
    }

    public Tween( V start, V end )
    {
        this.start = start;
        this.end = end;
    }

    @Override
    public V set( V subject, float delta )
    {
        subject.interpolate( start, end, delta );

        return subject;
    }

}
