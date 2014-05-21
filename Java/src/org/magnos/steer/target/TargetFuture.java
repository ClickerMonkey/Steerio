
package org.magnos.steer.target;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.vec.Vec;


public class TargetFuture<V extends Vec<V>> implements Target<V>
{

    public V position;
    public V velocity;

    public final V future;

    public TargetFuture( SteerSubject<V> target )
    {
        this( target.getPosition(), target.getVelocity() );
    }

    public TargetFuture( V position, V velocity )
    {
        this.position = position;
        this.velocity = velocity;
        this.future = position.create();
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

        return future;
    }

}
