
package org.magnos.steer;

import org.magnos.steer.vec.Vec;


/**
 * A steering behavior which can periodically update a another steering
 * behavior, apply a weight to the resulting force, and clamp the resulting
 * force's magnitude to a maximum value.
 */
public class SteerModifier<V extends Vec<V>> implements Steer<V>
{

    public Steer<V> steer;
    public float maximum;
    public float weight;
    public float update;
    public boolean enabled;

    protected boolean maximized = true;
    protected float time;

    public SteerModifier( Steer<V> steer, float weight )
    {
        this( steer, INFINITE, weight, 0 );
    }

    public SteerModifier( Steer<V> steer, float maximum, float weight )
    {
        this( steer, maximum, weight, 0 );
    }

    public SteerModifier( Steer<V> steer, float maximum, float weight, float update )
    {
        this.steer = steer;
        this.maximum = maximum;
        this.weight = weight;
        this.update = update;
        this.enabled = true;
    }

    @Override
    public void getForce( float elapsed, SteerSubject<V> subject, V out )
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
    }

    @Override
    public boolean isShared()
    {
        return false;
    }

    @Override
    public boolean isMaximized()
    {
        return maximized;
    }

    @Override
    public void setMaximized( boolean maximize )
    {
        this.maximized = maximize;
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerModifier<V>( steer.isShared() ? steer : steer.clone(), maximum, weight, update );
    }

}
