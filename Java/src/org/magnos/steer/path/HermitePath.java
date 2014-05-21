
package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.vec.Vec;


public class HermitePath<V extends Vec<V>> implements Path<V>
{

    public V start;
    public V startTangent;
    public V end;
    public V endTangent;

    public HermitePath()
    {
    }

    public HermitePath( V start, V startTangent, V end, V endTangent )
    {
        this.start = start;
        this.startTangent = startTangent;
        this.end = end;
        this.endTangent = endTangent;
    }

    @Override
    public V set( V subject, float d )
    {
        float d2 = d * d;
        float d3 = d2 * d;

        subject.clear();
        subject.addsi( start, 2 * d3 - 3 * d2 + 1 );
        subject.addsi( end, -2 * d3 + 3 * d2 );
        subject.addsi( startTangent, d3 - 2 * d2 + d );
        subject.addsi( endTangent, d3 - d2 );

        return subject;
    }

}
