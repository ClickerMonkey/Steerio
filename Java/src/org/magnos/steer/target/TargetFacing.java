
package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.behavior.AbstractSteer;
import org.magnos.steer.vec.Vec;


public class TargetFacing<V extends Vec<V>> implements Target<V>
{

    public Target<V> target;
    public V direction;
    public boolean front;

    public TargetFacing( SteerSubject<V> target, boolean front )
    {
        this( target, target.getDirection(), front );
    }

    public TargetFacing( Target<V> target, V direction, boolean front )
    {
        this.target = target;
        this.direction = direction;
        this.front = front;
    }

    @Override
    public V getTarget( SteerSubject<V> subject )
    {
        V actual = target.getTarget( subject );

        if ( actual == null )
        {
            return null;
        }

        if ( AbstractSteer.inFront( actual, direction, subject.getPosition() ) != front )
        {
            return null;
        }

        return actual;
    }

}
