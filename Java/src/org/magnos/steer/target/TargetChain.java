
package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.vec.Vec;


public class TargetChain<V extends Vec<V>> implements Target<V>
{

    public Target<V> first;
    public Target<V> second;

    public TargetChain( Target<V> first, Target<V> second )
    {
        this.first = first;
        this.second = second;
    }

    @Override
    public V getTarget( SteerSubject<V> subject )
    {
        V target = first.getTarget( subject );

        if ( target == null )
        {
            target = second.getTarget( subject );
        }

        return target;
    }

}
