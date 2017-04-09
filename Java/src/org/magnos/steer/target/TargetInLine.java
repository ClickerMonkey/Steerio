
package org.magnos.steer.target;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.spatial.SpatialEntity;
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
    public SpatialEntity<V> getTarget( SteerSubject<V> subject )
    {
        SpatialEntity<V> start = target0.getTarget( subject );

        if ( start == null )
        {
            return null;
        }

        SpatialEntity<V> end = target1.getTarget( subject );

        if ( end == null )
        {
            return null;
        }

        return SteerMath.closest( start.getPosition(), end.getPosition(), subject.getPosition(), closest );
    }

}
