
package org.magnos.steer.target;

import org.magnos.steer.Filter;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.vec.Vec;


public class TargetFilteredSubject<V extends Vec<V>> implements Target<V>
{

    public SteerSubject<V> target;
    public Filter<V, SteerSubject<V>> filter;

    public TargetFilteredSubject( SteerSubject<V> target, Filter<V, SteerSubject<V>> filter )
    {
        this.target = target;
        this.filter = filter;
    }

    @Override
    public V getTarget( SteerSubject<V> subject )
    {
        return filter.isValid( subject, target ) ? target.getTarget( subject ) : null;
    }

}
