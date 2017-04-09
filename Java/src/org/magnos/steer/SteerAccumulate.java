
package org.magnos.steer;

import org.magnos.steer.behavior.AbstractSteer;
import org.magnos.steer.vec.Vec;


/**
 * A steering behavior which encapsulates an array of steering behaviors
 * prioritized by their order in the array. A maximum value can be given, if
 * the accumulated force from the steerings is greater than the maximum value
 * then the accumulation ceases (this creates prioritization). {@link SteerAccumulate} can be shared if all steering behaviors can also be shared, otherwise
 * a
 * clone must be made to share this set with another subject.
 */
public class SteerAccumulate<V extends Vec<V>> extends AbstractSteer<V, SteerAccumulate<V>>
{

    public Steer<V>[] steerings;
    public Accumulator<V> accumulator;

    public SteerAccumulate( Accumulator<V> accumulator)
    {
        this( accumulator, NONE, INFINITE, 0 );
    }

    public SteerAccumulate( Accumulator<V> accumulator, float minimum, float maximum )
    {
        this( accumulator, minimum, maximum, 0 );
    }

    public SteerAccumulate( Accumulator<V> accumulator, float magnitude )
    {
        this( accumulator, magnitude, magnitude, 0 );
    }

    public SteerAccumulate( Accumulator<V> accumulator, float magnitude, int steerCount )
    {
        this( accumulator, magnitude, magnitude, steerCount );
    }

    public SteerAccumulate( Accumulator<V> accumulator, float minimum, float maximum, int steerCount )
    {
        this( accumulator, minimum, maximum, new Steer[ steerCount ] );
    }

    public SteerAccumulate( Accumulator<V> accumulator, Steer<V>... steerings )
    {
        this( accumulator, NONE, INFINITE, steerings );
    }

    public SteerAccumulate( Accumulator<V> accumulator, float magnitude, Steer<V>... steerings )
    {
        this( accumulator, NONE, magnitude, steerings );
    }

    public SteerAccumulate( Accumulator<V> accumulator, float minimum, float maximum, Steer<V>... steerings )
    {
        super( minimum, maximum );
        
        this.accumulator = accumulator;
        this.steerings = steerings;
    }

    @Override
    public float getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        final int steerCount = steerings.length;
        
        accumulator.start();
        
        for ( int i = 0; i < steerCount; i++ )
        {            
            out.clear();
            
            float magnitude = steerings[ i ].getForce( elapsed, subject, out );
            
            accumulator.accumulate( out, magnitude );
            
            if ( accumulator.isComplete() )
            {
                break;
            }
        }
        
        out.set( accumulator.end() );
        
        return forceFromVector( this, out );
    }

    @Override
    public boolean isShared()
    {
        final int steerCount = steerings.length;

        for ( int i = 0; i < steerCount; i++ )
        {
            Steer<V> steer = steerings[i];

            if ( steer != null && !steer.isShared() )
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public Steer<V> clone()
    {
        int steerCount = steerings.length;

        SteerAccumulate<V> cloned = new SteerAccumulate<V>( accumulator, minimum, maximum, steerCount );

        for ( int i = 0; i < steerCount; i++ )
        {
            Steer<V> s = steerings[i];

            cloned.steerings[i] = (s == null || s.isShared()) ? s : s.clone();
        }

        return cloned;
    }

    public void add( Steer<V> steer )
    {
        steerings = SteerMath.add( steer, steerings );
    }

    public int size()
    {
        return steerings.length;
    }

}
