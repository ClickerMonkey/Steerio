package org.magnos.steer.spatial;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec;

public abstract class AbstractSpatialEntity<V extends Vec<V>> implements SpatialEntity<V>
{
    
    public V getPosition( V out )
    {
        return out.set( getPosition() );
    }
    
    public V getTarget( SteerSubject<V> subject )
    {
        return getPosition();
    }
    
    public float getDistanceAndNormal( V origin, V lookahead, V outNormal )
    {
        V position = getPosition();
        float radius = getRadius();
        V closest = SteerMath.closest( origin, lookahead, position, outNormal );
        V difference = closest.subi( position );
        float distance = difference.normalize();
        
        return distance - radius;
    }
    
    public float getRadius()
    {
        return 0;
    }
    
    public long getSpatialGroups()
    {
        return 0;
    }
    
    public long getSpatialCollisionGroups()
    {
        return 0;
    }
    
    public boolean isStatic()
    {
        return false;
    }
    
    public boolean isInert()
    {
        return false;
    }
    
    public void attach( Object attachment )
    {
        
    }
    
    public <T> T attachment()
    {
        return null;
    }
    
    public <T> T attachment( Class<T> clazz )
    {
        return null;
    }
    
    public V getDirection()
    {
        return getPosition().ZERO();
    }
    
    public V getVelocity()
    {
        return getPosition().ZERO();
    }
    
    public float getMaximumVelocity()
    {
        return 0;
    }
    
    public V getAcceleration()
    {
        return getPosition().ZERO();
    }
    
    public float getMaximumAcceleration()
    {
        return 0;
    }
    
}