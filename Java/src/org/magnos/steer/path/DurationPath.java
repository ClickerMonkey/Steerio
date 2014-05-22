
package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.SteerMath;
import org.magnos.steer.vec.Vec;


public class DurationPath<V extends Vec<V>> implements Path<V>
{

    public float[] durations;
    public V[] points;
    public float durationTotal;

    public DurationPath()
    {
    }

    public DurationPath( V[] points, float[] durations )
    {
        this.points = points;
        this.durations = durations;
        this.updateDuration();
    }

    public void setPoints( V[] points )
    {
        this.points = points;
    }

    public void setDurations( float[] durations )
    {
        this.durations = durations;
        this.updateDuration();
    }

    public DurationPath<V> withPoints( V... points )
    {
        this.points = points;

        return this;
    }

    public DurationPath<V> withDurations( float... durations )
    {
        this.durations = durations;
        this.updateDuration();

        return this;
    }

    public DurationPath<V> addPoint( V point, float duration )
    {
        points = SteerMath.add( point, points );
        durations = SteerMath.add( duration, durations );
        durationTotal += duration;

        return this;
    }

    public float updateDuration()
    {
        durationTotal = 0;

        for ( int i = 0; i < durations.length; i++ )
        {
            durationTotal += durations[i];
        }

        return durationTotal;
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
            int i = 0;
            float d = delta * durationTotal;

            while ( d > durations[i] )
            {
                d -= durations[i++];
            }

            float q = d / durations[i];

            subject.interpolatei( points[i], points[i + 1], q );
        }

        return subject;
    }

}
