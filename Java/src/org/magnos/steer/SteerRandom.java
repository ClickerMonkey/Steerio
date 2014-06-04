
package org.magnos.steer;

import org.magnos.steer.vec.Vec;


/**
 * A steering behavior which can periodically update a another steering
 * behavior, apply a weight to the resulting force, and clamp the resulting
 * force's magnitude to a maximum value.
 */
public class SteerRandom<V extends Vec<V>> implements Steer<V>
{

    public Steer<V> steer;
    public float min;
    public float max;
    public float jump;
    public float current;
    public boolean enabled;

    public SteerRandom( Steer<V> steer, float min, float max, float jump )
    {
        this.steer = steer;
        this.min = min;
        this.max = max;
        this.jump = jump;
        this.current = SteerMath.randomFloat( min, max );
        this.enabled = true;
    }

    @Override
    public void getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        steer.getForce( elapsed, subject, out );
        
        if ( enabled )
        {
            current += SteerMath.randomFloat( -jump, jump ) * elapsed;
            current = SteerMath.clamp( current, min, max );
            
            out.muli( current );
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
        return false;
    }

    @Override
    public void setMaximized( boolean maximize )
    {
        
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerRandom<V>( steer.isShared() ? steer : steer.clone(), min, max, jump );
    }

}
