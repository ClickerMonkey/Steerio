package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.vec.Vec;

// when your target is not relative to you, it's relative to another steer subject.
public class TargetRelative<V extends Vec<V>> implements Target<V>
{

    public SteerSubject<V> relativeTo;
    public Target<V> target;
    
    public TargetRelative( SteerSubject<V> relativeTo, Target<V> target )
    {
        this.relativeTo = relativeTo;
        this.target = target;
    }
    
    @Override
    public V getTarget( SteerSubject<V> subject )
    {
        return target.getTarget( relativeTo );
    }

}
