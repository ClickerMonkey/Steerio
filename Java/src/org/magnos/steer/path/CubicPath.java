
package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.vec.Vec;


public class CubicPath<V extends Vec<V>> implements Path<V>
{

    public V p0;
    public V p1;
    public V p2;
    public V p3;

    public CubicPath()
    {
    }

    public CubicPath( V p0, V p1, V p2, V p3 )
    {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    @Override
    public V set( V subject, float d1 )
    {
        float d2 = d1 * d1;
        float d3 = d1 * d2;
        float i1 = 1 - d1;
        float i2 = i1 * i1;
        float i3 = i1 * i2;

        subject.set( p0 );
        subject.muli( i3 );
        subject.addsi( p1, 3 * i2 * d1 );
        subject.addsi( p2, 3 * i1 * d2 );
        subject.addsi( p3, d3 );

        return subject;
    }

}
