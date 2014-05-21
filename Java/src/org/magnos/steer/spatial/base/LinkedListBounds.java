
package org.magnos.steer.spatial.base;

import org.magnos.steer.util.LinkedList;
import org.magnos.steer.vec.Vec;


public class LinkedListBounds<V extends Vec<V>, N> extends LinkedList<N>
{

    public final V min, max, center;

    public LinkedListBounds( V min, V max )
    {
        this.min = min;
        this.max = max;
        this.center = min.interpolate( min, max, 0.5f );
    }

    public boolean isOverCenter( V position, float radius )
    {
        return center.isBetween( position, position, -radius );
    }

    public boolean isContained( V center, float radius )
    {
        return center.isBetween( min, max, radius );
    }

    public boolean isIntersecting( V center, float radius )
    {
        return center.isBetween( min, max, -radius );
    }

}
