
package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.SteerMath;
import org.magnos.steer.vec.Vec;


public class BezierPath<V extends Vec<V>> implements Path<V>
{

    public V[] points;
    private int[] weights;
    private float[] inverses;

    public BezierPath()
    {
    }

    public BezierPath( V... points )
    {
        this.points = points;
        this.weights = computeWeights( points.length );
        this.inverses = new float[points.length];
    }

    private int[] computeWeights( int n )
    {
        int[] w = new int[n--];

        for ( int i = 0; i <= n; i++ )
        {
            w[i] = SteerMath.choose( n, i );
        }

        return w;
    }

    @Override
    public V set( V subject, float delta )
    {
        final int n = points.length;
        float x = 1;

        inverses[n - 1] = 1;

        for ( int i = n - 2; i >= 0; i-- )
        {
            inverses[i] = inverses[i + 1] * (1 - delta);
        }

        subject.clear();

        for ( int i = 0; i < n; i++ )
        {
            subject.addsi( points[i], weights[i] * inverses[i] * x );

            x *= delta;
        }

        return subject;
    }

    public V[] points()
    {
        return points;
    }

}
