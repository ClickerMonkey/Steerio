
package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.vec.Vec;


public class TargetInterpose<V extends Vec<V>> implements Target<V>
{

    public static final float DEFAULT_DELTA = 0.5f;

    public Target<V> target0;
    public Target<V> target1;
    public float delta;
    public final V interpose;

    public TargetInterpose( Target<V> target0, Target<V> target1, V template )
    {
        this( target0, target1, DEFAULT_DELTA, template );
    }

    public TargetInterpose( Target<V> target0, Target<V> target1, float delta, V template )
    {
        this.target0 = target0;
        this.target1 = target1;
        this.delta = delta;
        this.interpose = template.create();
    }

    @Override
    public V getTarget( SteerSubject<V> subject )
    {
        V t0 = target0.getTarget( subject );

        if ( t0 == null )
        {
            return null;
        }

        V t1 = target1.getTarget( subject );

        if ( t1 == null )
        {
            return null;
        }

        return interpose.interpolatei( t0, t1, delta );
    }

}
