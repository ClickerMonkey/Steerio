
package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.behavior.AbstractSteer;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


public class TargetFacing<V extends Vec<V>> implements Target<V>
{

    public Target<V> target;
    public boolean front;

    public TargetFacing( Target<V> target, boolean front )
    {
        this.target = target;
        this.front = front;
    }

    @Override
    public SpatialEntity<V> getTarget( SteerSubject<V> subject )
    {
        SpatialEntity<V> actual = target.getTarget( subject );

        if ( actual == null )
        {
            return null;
        }

        if ( AbstractSteer.inFront( actual.getPosition(), actual.getDirection(), subject.getPosition() ) != front )
        {
            return null;
        }

        return actual;
    }

}
