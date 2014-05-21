
package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.vec.Vec;


public class SubPath<V extends Vec<V>> implements Path<V>
{

    public float start;
    public float end;
    public Path<V> path;

    public SubPath( float start, float end, Path<V> path )
    {
        this.start = start;
        this.end = end;
        this.path = path;
    }

    @Override
    public V set( V subject, float delta )
    {
        path.set( subject, (end - start) * delta + start );

        return subject;
    }

}
