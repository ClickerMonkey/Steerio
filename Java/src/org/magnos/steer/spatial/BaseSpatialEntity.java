package org.magnos.steer.spatial;

import org.magnos.steer.vec.Vec;

public abstract class BaseSpatialEntity<V extends Vec<V>> extends AbstractSpatialEntity<V>
{
    
    public long groups;
    public long collisionGroups;
    public boolean fixed;
    public boolean inert;
    public Object attachment;
    
    public BaseSpatialEntity<V> withGroups( long groups )
    {
        this.groups = groups;
        
        return this;
    }
    
    public BaseSpatialEntity<V> withCollisionGroups( long collisionGroups )
    {
        this.collisionGroups = collisionGroups;
        
        return this;
    }
    
    public BaseSpatialEntity<V> withFixed( boolean fixed )
    {
        this.fixed = fixed;
        
        return this;
    }
    
    public BaseSpatialEntity<V> withInert( boolean inert )
    {
        this.inert = inert;
        
        return this;
    }
    
    public BaseSpatialEntity<V> withAttachment( Object attachment )
    {
        this.attachment = attachment;
        
        return this;
    }
    
    public long getSpatialGroups()
    {
        return groups;
    }
    
    public long getSpatialCollisionGroups()
    {
        return collisionGroups;
    }
    
    public boolean isStatic()
    {
        return fixed;
    }
    
    public boolean isInert()
    {
        return inert;
    }
    
    public void attach( Object attachment )
    {
        this.attachment = attachment;
    }
    
    public <T> T attachment()
    {
        return (T) attachment;
    }
    
    public <T> T attachment( Class<T> type )
    {
        return attachment != null && type.isAssignableFrom( attachment.getClass() ) ? (T)attachment : null;
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