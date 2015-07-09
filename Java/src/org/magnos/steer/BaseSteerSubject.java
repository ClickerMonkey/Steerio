
package org.magnos.steer;

import org.magnos.steer.spatial.BaseSpatialEntity;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.vec.Vec;


public class BaseSteerSubject<V extends Vec<V>> extends BaseSpatialEntity<V> implements SteerSubject<V>
{

    public V position;
    public V direction;
    public V velocity;
    public float velocityMax;
    public float accelerationMax = Float.MAX_VALUE;
    public V acceleration;
    public float radius;
    public SteerController<V> controller;

    public BaseSteerSubject( V template, float radius, float velocityMax )
    {
        this( template, radius, velocityMax, null );
    }

    public BaseSteerSubject( V template, float radius, float velocityMax, Steer<V> steer )
    {
        this.groups = SpatialDatabase.ALL_GROUPS;
        this.collisionGroups = SpatialDatabase.ALL_GROUPS;
        
        this.position = template.create();
        this.position.clear();

        this.direction = template.create();
        this.direction.defaultUnit();

        this.velocity = template.create();
        this.velocity.clear();

        this.acceleration = template.create();
        this.acceleration.clear();

        this.radius = radius;
        this.velocityMax = velocityMax;

        if ( steer != null )
        {
            this.controller = new SteerController<V>( this, steer );
        }
    }

    public void update( float elapsed )
    {
        if ( controller != null )
        {
            controller.update( elapsed );
        }
    }

    @Override
    public float getDistanceAndNormal( V origin, V lookahead, V outNormal )
    {
        V away = position.clone();
        float intersectionTime = SteerMath.interceptTime( origin, origin.distance( lookahead ), position, velocity );
        
        if ( intersectionTime > 0 )
        {
            away.addsi( velocity, intersectionTime );
        }
        else
        {
            return Float.MAX_VALUE;
        }
        
        return SteerMath.closest( origin, lookahead, away, outNormal ).subi( away ).normalize() - radius;
    }

    @Override
    public float getRadius()
    {
        return radius;
    }

    @Override
    public V getPosition()
    {
        return position;
    }

    @Override
    public V getDirection()
    {
        return direction;
    }

    @Override
    public V getVelocity()
    {
        return velocity;
    }

    @Override
    public float getMaximumVelocity()
    {
        return velocityMax;
    }
    
    @Override
    public float getMaximumAcceleration()
    {
        return accelerationMax;
    }

    @Override
    public V getAcceleration()
    {
        return acceleration;
    }

}
