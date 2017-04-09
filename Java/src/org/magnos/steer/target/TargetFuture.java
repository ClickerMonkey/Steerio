
package org.magnos.steer.target;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


public class TargetFuture<V extends Vec<V>> implements Target<V>
{

    public static boolean DEFAULT_INTERCEPT_ONLY = false;
    
    public Target<V> target;
    public boolean interceptOnly;

    public final V future;

    public TargetFuture( Target<V> target, V factory )
    {
        this( target, DEFAULT_INTERCEPT_ONLY, factory );
    }

    public TargetFuture( Target<V> target, boolean interceptOnly, V factory )
    {
        this.target = target;
        this.future = factory.create();
        this.interceptOnly = interceptOnly;
    }

    @Override
    public SpatialEntity<V> getTarget( SteerSubject<V> subject )
    {
        SpatialEntity<V> found = target.getTarget( subject );
        
        if ( found == null )
        {
            return null;
        }
        
        V position = found.getPosition();
        V velocity = found.getVelocity();
        
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
