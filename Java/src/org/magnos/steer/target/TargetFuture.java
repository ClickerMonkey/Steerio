
package org.magnos.steer.target;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.vec.Vec;


public class TargetFuture<V extends Vec<V>> implements Target<V>
{

    public static boolean DEFAULT_INTERCEPT_ONLY = false;
    
    public V position;
    public V velocity;
    public boolean interceptOnly;

    public final V future;

    public TargetFuture( SteerSubject<V> target )
    {
        this( target.getPosition(), target.getVelocity(), DEFAULT_INTERCEPT_ONLY );
    }

    public TargetFuture( SteerSubject<V> target, boolean interceptOnly )
    {
        this( target.getPosition(), target.getVelocity(), interceptOnly );
    }

    public TargetFuture( V position, V velocity, boolean interceptOnly )
    {
        this.position = position;
        this.velocity = velocity;
        this.future = position.create();
        this.interceptOnly = interceptOnly;
    }

    @Override
    public V getTarget( SteerSubject<V> subject )
    {
        future.set( position );

        float time = SteerMath.interceptTime( subject.getPosition(), subject.getVelocity().length(), position, velocity );

        if ( time > 0 )
        {
            future.addsi( velocity, time );
        }
        else if ( interceptOnly )
        {
            return null;
        }

        return future;
    }

}
