
package org.magnos.steer.spatial;

import org.magnos.steer.Target;
import org.magnos.steer.vec.Vec;


public interface SpatialEntity<V extends Vec<V>> extends Target<V>
{

    public V getPosition();
    
    public V getPosition( V out );

    public float getRadius();
    
    public float getDistanceAndNormal( V origin, V lookahead, V outNormal );

    public long getSpatialGroups();

    public long getSpatialCollisionGroups();

    public boolean isStatic();

    public boolean isInert();

    public void attach( Object attachment );

    public <T> T attachment();

    public <T> T attachment( Class<T> type );
    
    public V getDirection();
    
    public V getVelocity();
    
    public float getMaximumVelocity();
    
    public V getAcceleration();
    
    public float getMaximumAcceleration();

}
