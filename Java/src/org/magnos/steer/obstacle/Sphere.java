
package org.magnos.steer.obstacle;

import org.magnos.steer.spatial.BaseSpatialEntity;
import org.magnos.steer.vec.Vec;


public class Sphere<V extends Vec<V>> extends BaseSpatialEntity<V>
{

    public V position;
    public float radius;

    public Sphere()
    {
    }

    public Sphere( V position, float radius )
    {
        this.position = position;
        this.radius = radius;
    }

    @Override
    public V getPosition()
    {
        return position;
    }

    @Override
    public float getRadius()
    {
        return radius;
    }

}
