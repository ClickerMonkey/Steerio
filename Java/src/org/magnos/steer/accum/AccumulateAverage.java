package org.magnos.steer.accum;

import org.magnos.steer.Accumulator;
import org.magnos.steer.Steer;
import org.magnos.steer.vec.Vec;


public class AccumulateAverage<V extends Vec<V>> implements Accumulator<V>
{
    
    @SuppressWarnings ("rawtypes")
    public static final AccumulateAverage INSTANCE = new AccumulateAverage();
    
    public V total;
    public int count;

    @Override
    public void start( V out )
    {
        total = out;
        count = 0;
    }

    @Override
    public void accumulate( V normal, float magnitude )
    {
        if ( magnitude != 0 )
        {
            total.addsi( normal, Math.abs( magnitude ) );
            count++;
        }
    }
    
    @Override
    public boolean isComplete()
    {
        return false;
    }

    @Override
    public float end()
    {
        if ( count == 0 )
        {
            return Steer.NONE;
        }
        
        return total.divi( count ).normalize();
    }

}
