
package org.magnos.steer.behavior;

import org.magnos.steer.Obstacle;
import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec;


/**
 */
public class SteerContainment<V extends Vec<V>> extends AbstractSteer<V>
{

    public Obstacle<V> obstacle;
    public float buffer;
    public boolean shared;

    public SteerContainment( Obstacle<V> obstacle, float buffer )
    {
        this( obstacle, buffer, true );
    }

    public SteerContainment( Obstacle<V> obstacle, float buffer, boolean shared )
    {
        this.obstacle = obstacle;
        this.buffer = buffer;
        this.shared = shared;
    }

    @Override
    public void getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        final V p = subject.getPosition();
        final V v = subject.getVelocity();
        final V inner = p.create(); 
        
        float distance = obstacle.getDistanceAndNormal( p, p.add( v ), inner );
        
        if ( distance > -buffer )
        {
            out.set( inner ).negi();
            maximize( subject, out );
        }
    }

    @Override
    public boolean isShared()
    {
        return shared;
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerContainment<V>( obstacle, buffer, shared );
    }

}
