
package org.magnos.steer;

import org.magnos.steer.behavior.AbstractSteer;
import org.magnos.steer.vec.Vec;


/**
 * A steering behavior which can periodically update a another steering
 * behavior, apply a weight to the resulting force, and clamp the resulting
 * force's magnitude to a maximum value.
 */
public class SteerModifier<V extends Vec<V>> extends AbstractSteer<V, SteerModifier<V>>
{

    public Steer<V> steer;
    public float weight;
    public float update;
    public boolean enabled;

    protected float time;

    public SteerModifier( Steer<V> steer, float weight )
    {
        this( steer.getMinimum(), steer.getMaximum(), steer, weight, 0 );
    }

    public SteerModifier( float minimum, float maximum, Steer<V> steer, float weight )
    {
        this( minimum, maximum, steer, weight, 0 );
    }

    public SteerModifier( float magnitude, Steer<V> steer, float weight )
    {
        this( magnitude, magnitude, steer, weight, 0 );
    }

    public SteerModifier( float magnitude, Steer<V> steer, float weight, float update )
    {
        this( magnitude, magnitude, steer, weight, update );
    }

    public SteerModifier( float minimum, float maximum, Steer<V> steer, float weight, float update )
    {
        super( minimum, maximum );
        
        this.steer = steer;
        this.weight = weight;
        this.update = update;
        this.enabled = true;
    }

    @Override
    public float getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        time += elapsed;

        if ( time >= update && enabled )
        {
            steer.getForce( time, subject, out );

            if ( !out.isZero() )
            {
                out.muli( weight );

                if ( maximum != INFINITE )
                {
                    out.max( maximum );
                }
            }

            time = Math.max( 0, time - update );
        }
        
        return AbstractSteer.forceFromVector( steer, out );
    }

    @Override
    public boolean isShared()
    {
        return false;
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerModifier<V>( minimum, maximum, steer.isShared() ? steer : steer.clone(), weight, update );
    }

}
