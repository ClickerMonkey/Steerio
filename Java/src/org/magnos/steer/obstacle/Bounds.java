
package org.magnos.steer.obstacle;

import org.magnos.steer.Obstacle;
import org.magnos.steer.SteerMath;
import org.magnos.steer.vec.Vec;


public class Bounds<V extends Vec<V>> implements Obstacle<V>
{

    public static <V extends Vec<V>> Bounds<V> fromMinMax( V min, V max )
    {
        V center = min.interpolate( min, max, 0.5f );
        
        return new Bounds<V>( min, max, center, center.distance( min ) );
    }

    public static <V extends Vec<V>> Bounds<V> fromCenter( V center, V size )
    {
        V min = center.adds( size, -0.5f );
        V max = center.adds( size, +0.5f );
        
        return new Bounds<V>( min, max, center, center.distance( min ) );
    }
    
    public V min;
    public V max;
    public V center;
    public float radius;
    
    public Bounds()
    {
    }

    private Bounds( V min, V max, V center, float radius )
    {
        this.min = min;
        this.max = max;
        this.center = min.interpolate( min, max, 0.5f );
        this.radius = center.distance( min );
    }

    @Override
    public float getDistanceAndNormal( V origin, V lookahead, V outNormal )
    {
        SteerMath.closest( origin, lookahead, center, outNormal );

        V closest = outNormal.clamp( min, max );

        // inside bounds
        if ( closest.isEqual( outNormal ) )
        {
            return outNormal.subi( center ).normalize() - radius;
        }
        // outside bounds
        else
        {
            return outNormal.subi( closest ).normalize();
        }
    }

    @Override
    public V getPosition( V out )
    {
        return out.set( center );
    }

    @Override
    public float getRadius()
    {
        return radius;
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
