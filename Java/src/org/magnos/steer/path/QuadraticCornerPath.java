
package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.SteerMath;
import org.magnos.steer.vec.Vec;


/**
 * 
 * @author Philip Diffenderfer
 *
 * @param <T>
 */
public class QuadraticCornerPath<V extends Vec<V>> implements Path<V>
{

    protected V[] points;
    protected final V temp0;
    protected final V temp1;
    protected boolean loops;
    protected float midpoint;

    public QuadraticCornerPath( float midpoint, boolean loops, V... points )
    {
        this.midpoint = midpoint;
        this.loops = loops;
        this.points = points;
        this.temp0 = points[0].create();
        this.temp1 = points[0].create();
    }

    @Override
    public V set( V subject, float delta )
    {
        final float negmidpoint = 1.0f - midpoint;
        final float halfmidpoint = midpoint * 0.5f;
        final int n = points.length - (loops ? 0 : 1);
        final float a = delta * n;
        final int i = SteerMath.clamp( (int)a, 0, n - 1 );
        float d = a - i;

        V p0 = points[getActualIndex( i - 1 )];
        V p1 = points[getActualIndex( i )];
        V p2 = points[getActualIndex( i + 1 )];
        V p3 = points[getActualIndex( i + 2 )];

        if ( d < midpoint )
        {
            d = (d / midpoint);

            temp0.interpolatei( p0, p1, d * halfmidpoint + negmidpoint + halfmidpoint );
            temp1.interpolatei( p1, p2, d * halfmidpoint + halfmidpoint );

            p1 = temp0;
            p2 = temp1;
            d = d * 0.5f + 0.5f;
        }
        else if ( d > negmidpoint )
        {
            d = (d - negmidpoint) / midpoint;

            temp0.interpolatei( p1, p2, d * halfmidpoint + negmidpoint );
            temp1.interpolatei( p2, p3, d * halfmidpoint );

            p1 = temp0;
            p2 = temp1;
            d = d * 0.5f;
        }

        subject.interpolatei( p1, p2, d );

        return subject;
    }

    public int getActualIndex( int index )
    {
        final int n = points.length;

        return (loops ? (index + n) % n : SteerMath.clamp( index, 0, n - 1 ));
    }

}
