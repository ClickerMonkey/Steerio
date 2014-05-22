
package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.vec.Vec;


public class TimedPath<V extends Vec<V>> implements Path<V>
{

    public float[] times;
    public V[] points;

    public TimedPath()
    {
    }

    public TimedPath( V[] points, float[] times )
    {
        this.points = points;
        this.times = times;
    }

    protected void setPoints( V[] points )
    {
        this.points = points;
    }

    protected void setTimes( float[] times )
    {
        this.times = times;
    }

    @Override
    public V set( V subject, float delta )
    {
        if ( delta <= 0 )
        {
            subject.set( points[0] );
        }
        else if ( delta >= 1 )
        {
            subject.set( points[points.length - 1] );
        }
        else
        {
            int i = points.length - 1;
            while ( times[i] > delta )
                --i;
            float q = (delta - times[i]) / (times[i + 1] - times[i]);

            subject.interpolatei( points[i], points[i + 1], q );
        }

        return subject;
    }

}
