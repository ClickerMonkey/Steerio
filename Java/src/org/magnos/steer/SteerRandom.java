
package org.magnos.steer;

import org.magnos.steer.behavior.AbstractSteer;
import org.magnos.steer.vec.Vec;


/**
 * A steering behavior which can periodically update a another steering
 * behavior, apply a weight to the resulting force, and clamp the resulting
 * force's magnitude to a maximum value.
 */
public class SteerRandom<V extends Vec<V>> extends AbstractSteer<V, SteerRandom<V>>
{

    public Steer<V> steer;
    public float minimumScale;
    public float maximumScale;
    public float jump;
    public float current;
    public boolean enabled;

    public SteerRandom( float minimum, float maximum, Steer<V> steer, float minimumScale, float maximumScale, float jump )
    {
        super( minimum, maximum );
        
        this.steer = steer;
        this.minimumScale = minimumScale;
        this.maximumScale = maximumScale;
        this.jump = jump;
        this.current = SteerMath.randomFloat( minimumScale, maximumScale );
        this.enabled = true;
    }

    @Override
    public float getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        float magnitude = steer.getForce( elapsed, subject, out );
        
        if ( enabled )
        {
            current += SteerMath.randomFloat( -jump, jump ) * elapsed;
            current = SteerMath.clamp( current, minimumScale, maximumScale );
            
            magnitude *= current;
        }
        
        return magnitude;
    }

    @Override
    public boolean isShared()
    {
        return false;
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerRandom<V>( minimum, maximum, steer.isShared() ? steer : steer.clone(), minimumScale, maximumScale, jump );
    }

}
