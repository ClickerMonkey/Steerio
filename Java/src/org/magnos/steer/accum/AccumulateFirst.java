package org.magnos.steer.accum;

import org.magnos.steer.Accumulator;
import org.magnos.steer.vec.Vec;


public class AccumulateFirst<V extends Vec<V>> implements Accumulator<V>
{
    
    @SuppressWarnings ("rawtypes")
    public static final AccumulateFirst INSTANCE = new AccumulateFirst();
    
    public V first;
    public float firstMagnitude;

    @Override
    public void start( V out )
    {
        first = out;
        firstMagnitude = 0;
    }

    @Override
    public void accumulate( V normal, float magnitude )
    {
        if ( !normal.isZero() && magnitude != 0 )
        {
            first.set( normal );
            firstMagnitude = magnitude;
        }
    }
    
    @Override
    public boolean isComplete()
    {
        return firstMagnitude != 0;
    }

    @Override
    public float end()
    {
        return firstMagnitude;
    }

}
