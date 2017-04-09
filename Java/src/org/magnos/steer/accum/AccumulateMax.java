package org.magnos.steer.accum;

import org.magnos.steer.Accumulator;
import org.magnos.steer.SteerMath;
import org.magnos.steer.vec.Vec;


public class AccumulateMax<V extends Vec<V>> implements Accumulator<V>
{
    
    public V force;
    public float max;
    public float remaining;
    
    public AccumulateMax( float max )
    {
        this.max = max;
    }

    @Override
    public void start( V out )
    {
        force = out;
        remaining = max;
    }

    @Override
    public void accumulate( V normal, float magnitude )
    {
        float available = Math.min( Math.abs( magnitude ), remaining );
        
        if ( available > 0 )
        {
            force.addsi( normal, available );
            remaining -= available;
        }
    }

    @Override
    public boolean isComplete()
    {
        return SteerMath.equals( 0, remaining, SteerMath.EPSILON );
    }

    @Override
    public float end()
    {
        return force.normalize();
    }

}
