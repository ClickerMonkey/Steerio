
package org.magnos.steer;

import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.vec.Vec;


public class BaseSteerSubject<V extends Vec<V>> implements SteerSubject<V>
{

    public V position;
    public V direction;
    public V velocity;
    public float velocityMax;
    public V acceleration;
    public float accelerationMax;
    public float radius;
    public long groups = SpatialDatabase.ALL_GROUPS;
    public long collisionGroups = SpatialDatabase.ALL_GROUPS;
    public boolean dynamic = true;
    public boolean inert = false;
    public Object attachment;
    public SteerController<V> controller;

    public BaseSteerSubject( V template, float radius, float max )
    {
        this( template, radius, max, max, null );
    }

    public BaseSteerSubject( V template, float radius, float velocityMax, float accelerationMax )
    {
        this( template, radius, velocityMax, accelerationMax, null );
    }

    public BaseSteerSubject( V template, float radius, float velocityMax, float accelerationMax, Steer<V> steer )
    {
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
        this.accelerationMax = accelerationMax;

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
    public V getTarget( SteerSubject<V> subject )
    {
        return position;
    }

    @Override
    public float getRadius()
    {
        return radius;
    }

    @Override
    public long getSpatialGroups()
    {
        return groups;
    }

    @Override
    public long getSpatialCollisionGroups()
    {
        return collisionGroups;
    }

    @Override
    public boolean isStatic()
    {
        return !dynamic;
    }

    @Override
    public boolean isInert()
    {
        return inert;
    }

    @Override
    public V getPosition()
    {
        return position;
    }

    @Override
    public V getPosition( V out )
    {
        return out.set( position );
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
    public float getVelocityMax()
    {
        return velocityMax;
    }

    @Override
    public V getAcceleration()
    {
        return acceleration;
    }

    @Override
    public float getAccelerationMax()
    {
        return accelerationMax;
    }

    @Override
    public void attach( Object attachment )
    {
        this.attachment = attachment;
    }

    @Override
    public <T> T attachment()
    {
        return (T)attachment;
    }

    @Override
    public <T> T attachment( Class<T> type )
    {
        return attachment != null && type.isAssignableFrom( attachment.getClass() ) ? (T)attachment : null;
    }

}
