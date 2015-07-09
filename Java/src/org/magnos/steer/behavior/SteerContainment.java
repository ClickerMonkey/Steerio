
package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


/**
 */
public class SteerContainment<V extends Vec<V>> extends AbstractSteer<V, SteerContainment<V>>
{

    public SpatialEntity<V> obstacle;
    public float buffer;
    public boolean shared;

    public SteerContainment( float minimum, float maximum, SpatialEntity<V> obstacle, float buffer )
    {
        this( minimum, maximum, obstacle, buffer, DEFAULT_SHARED );
    }

    public SteerContainment( float magnitude, SpatialEntity<V> obstacle, float buffer )
    {
        this( magnitude, magnitude, obstacle, buffer, DEFAULT_SHARED );
    }

    public SteerContainment( float magnitude, SpatialEntity<V> obstacle, float buffer, boolean shared )
    {
        this( magnitude, magnitude, obstacle, buffer, shared );
    }

    public SteerContainment( float minimum, float maximum, SpatialEntity<V> obstacle, float buffer, boolean shared )
    {
        super( minimum, maximum );
        
        this.obstacle = obstacle;
        this.buffer = buffer;
        this.shared = shared;
    }

    @Override
    public float getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        final V p = subject.getPosition();
        final V v = subject.getVelocity();
        final V inner = p.create(); 
        
        float distance = obstacle.getDistanceAndNormal( p, p.add( v ), inner );
        
        if ( distance > -buffer )
        {
            out.set( inner ).negi();
            
            return forceFromVector( this, out );
        }
        
        return Steer.NONE;
    }

    @Override
    public boolean isShared()
    {
        return shared;
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerContainment<V>( minimum, maximum, obstacle, buffer, shared );
    }

}
