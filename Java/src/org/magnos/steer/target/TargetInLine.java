
package org.magnos.steer.target;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.vec.Vec;


public class TargetInLine<V extends Vec<V>> implements Target<V>
{

    public Target<V> target0;
    public Target<V> target1;

    public final V closest;

    public TargetInLine( Target<V> target0, Target<V> target1, V template )
    {
        this.target0 = target0;
        this.target1 = target1;
        this.closest = template.create();
    }

    @Override
    public V getTarget( SteerSubject<V> subject )
    {
        V s = target0.getTarget( subject );

        if ( s == null )
        {
            return null;
        }

        V e = target1.getTarget( subject );

        if ( e == null )
        {
            return null;
        }

        V v = subject.getPosition();

        return SteerMath.closest( s, e, v, closest );
    }

}
