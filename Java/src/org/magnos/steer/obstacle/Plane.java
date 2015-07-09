
package org.magnos.steer.obstacle;

import org.magnos.steer.spatial.BaseSpatialEntity;
import org.magnos.steer.vec.Vec;


public class Plane<V extends Vec<V>> extends BaseSpatialEntity<V>
{

    public static final float DEFAULT_THICKNESS = 0.000001f;

    public V origin;
    public V normal;
    public float thickness;

    public Plane()
    {
    }

    public Plane( V origin, V normal )
    {
        this( origin, normal, DEFAULT_THICKNESS );
    }

    public Plane( V origin, V normal, float thickness )
    {
        this.origin = origin;
        this.normal = normal;
        this.thickness = thickness;
    }

    @Override
    public float getDistanceAndNormal( V position, V lookahead, V outNormal )
    {
        outNormal.set( normal );

        float d0 = normal.dot( position );
        float d1 = normal.dot( lookahead );

        return Math.min( d0, d1 ) - normal.dot( origin ) - thickness;
    }

    @Override
    public V getPosition()
    {
        return origin;
    }

    @Override
    public float getRadius()
    {
        return Float.POSITIVE_INFINITY;
    }
    
}
