
package org.magnos.steer;

import org.magnos.steer.vec.Vec;


public interface Obstacle<V extends Vec<V>>
{

    public float getDistanceAndNormal( V origin, V lookahead, V outNormal );
    
    public V getPosition( V out );
    
    public float getRadius();
    
}
