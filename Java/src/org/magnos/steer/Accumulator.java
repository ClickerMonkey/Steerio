package org.magnos.steer;

import org.magnos.steer.vec.Vec;


public interface Accumulator<V extends Vec<V>>
{

    public void start( V out );
    
    public void accumulate( V normal, float magnitude );
    
    public boolean isComplete();
    
    public float end();
    
}
