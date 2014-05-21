
package org.magnos.steer.obstacle;

import org.magnos.steer.Obstacle;
import org.magnos.steer.SteerMath;
import org.magnos.steer.vec.Vec;


public class Sphere<V extends Vec<V>> implements Obstacle<V>
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
    public float getDistanceAndNormal( V origin, V lookahead, V outNormal )
    {
        return SteerMath.closest( origin, lookahead, position, outNormal ).subi( position ).normalize() - radius;
    }

    @Override
    public V getPosition( V out )
    {
        return out.set( position );
    }

    @Override
    public float getRadius()
    {
        return radius;
    }

}
