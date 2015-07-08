
package org.magnos.steer;

import org.magnos.steer.behavior.AbstractSteer;
import org.magnos.steer.vec.Vec;


/**
 * A steering behavior which encapsulates an array of steering behaviors
 * prioritized by their order in the array. A maximum value can be given, if
 * the accumulated force from the steerings is greater than the maximum value
 * then the accumulation ceases (this creates prioritization). {@link SteerFirst} can be shared if all steering behaviors can also be shared, otherwise
 * a
 * clone must be made to share this set with another subject.
 */
public class SteerFirst<V extends Vec<V>> extends AbstractSteer<V, SteerFirst<V>>
{

    public Steer<V>[] steerings;

    public SteerFirst()
    {
        this( NONE, INFINITE, 0 );
    }

    public SteerFirst( float minimum, float maximum )
    {
        this( minimum, maximum, 0 );
    }

    public SteerFirst( float magnitude )
    {
        this( magnitude, magnitude, 0 );
    }

    public SteerFirst( float magnitude, int steerCount )
    {
        this( magnitude, magnitude, steerCount );
    }

    public SteerFirst( float minimum, float maximum, int steerCount )
    {
        super( minimum, maximum );
        
        this.steerings = new Steer[ steerCount ];
    }

    public SteerFirst( Steer<V>... steerings )
    {
        this( NONE, INFINITE, steerings );
    }

    public SteerFirst( float magnitude, Steer<V>... steerings )
    {
        this( NONE, magnitude, steerings );
    }

    public SteerFirst( float minimum, float maximum, Steer<V>... steerings )
    {
        super( minimum, maximum );
        
        this.steerings = steerings;
    }

    @Override
    public float getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        final int steerCount = steerings.length;

        for ( int i = 0; i < steerCount; i++ )
        {
            Steer<V> steer = steerings[i];

            if ( steer != null )
            {
                float magnitude = steer.getForce( elapsed, subject, out );
                
                if (magnitude > 0)
                {
                    out.muli( magnitude );
                    
                    break;
                }
            }
        }
        
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

        SteerFirst<V> cloned = new SteerFirst<V>( minimum, maximum, steerCount );

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
