
package org.magnos.steer.obstacle;

import org.magnos.steer.Obstacle;
import org.magnos.steer.SteerMath;
import org.magnos.steer.vec.Vec;


public class Segment<V extends Vec<V>> implements Obstacle<V>
{

    public static final float DEFAULT_THICKNESS = 0.000001f;

    public V start;
    public V end;
    public float thickness;

    public Segment()
    {
    }

    public Segment( V start, V end )
    {
        this( start, end, DEFAULT_THICKNESS );
    }

    public Segment( V start, V end, float thickness )
    {
        this.start = start;
        this.end = end;
        this.thickness = thickness;
    }

    @Override
    public float getDistanceAndNormal( V origin, V lookahead, V outNormal )
    {
        V a = start;
        V b = end.sub( start );
        V c = origin;
        V e = lookahead.sub( origin );
        V u = a.sub( c );

        float ub = u.dot( b );
        float ee = e.dot( e );
        float ue = u.dot( e );
        float be = b.dot( e );
        float bb = b.dot( b );
        float denom = be * be - bb * ee;

        // parallel or coincident
        if ( denom == 0.0f )
        {
            return SteerMath.closest( a, b, c, outNormal ).subi( c ).normalize() - thickness;
        }

        // skew
        float invdenom = 1.0f / denom;
        float s = (ub * ee - ue * be) * invdenom;
        float t = (ub * be - ue * bb) * invdenom;

        float sclamped = SteerMath.clamp( s, 0.0f, 1.0f );
        float tclamped = SteerMath.clamp( t, 0.0f, 1.0f );

        V ab = outNormal.interpolate( a, b, sclamped );
        V ce = outNormal.interpolate( c, e, tclamped );

        return outNormal.directi( ce, ab ).normalize() - thickness;
    }

    @Override
    public V getPosition( V out )
    {
        return out.interpolatei( start, end, 0.5f );
    }

    @Override
    public float getRadius()
    {
        return start.distance( end ) * 0.5f;
    }

}
