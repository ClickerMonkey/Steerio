
package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Filter;
import org.magnos.steer.Target;
import org.magnos.steer.vec.Vec;


public class TargetFiltered<V extends Vec<V>> implements Target<V>
{

    public Target<V> target;
    public Filter<V, V> filter;

    public TargetFiltered( Target<V> target, Filter<V, V> filter )
    {
        this.target = target;
        this.filter = filter;
    }

    @Override
    public V getTarget( SteerSubject<V> subject )
    {
        V position = target.getTarget( subject );

        if ( position != null && !filter.isValid( subject, position ) )
        {
            position = null;
        }

        return position;
    }

}
