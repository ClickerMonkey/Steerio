
package org.magnos.steer;

import org.magnos.steer.behavior.AbstractSteer;
import org.magnos.steer.vec.Vec;


/**
 * A steering behavior which encapsulates an array of steering behaviors
 * prioritized by their order in the array. A maximum value can be given, if
 * the accumulated force from the steerings is greater than the maximum value
 * then the accumulation ceases (this creates prioritization). {@link SteerContext} can be shared if all steering behaviors can also be shared, otherwise
 * a
 * clone must be made to share this set with another subject.
 */
public class SteerContext<V extends Vec<V>> extends AbstractSteer<V, SteerContext<V>>
{

    public static float DEFAULT_MATCH_THRESHOLD = 0.5f;
    
    public Steer<V>[] steerings;
    public boolean[] good;
    public float matchThreshold;
    
    public SteerContext()
    {
        this( NONE, INFINITE, 0 );
    }

    public SteerContext( float minimum, float maximum, float matchThreshold )
    {
        this( minimum, maximum, matchThreshold, 0 );
    }

    public SteerContext( float magnitude, float matchThreshold )
    {
        this( magnitude, magnitude, matchThreshold, 0 );
    }

    public SteerContext( float magnitude, float matchThreshold, int steerCount )
    {
        this( magnitude, magnitude, matchThreshold, steerCount );
    }

    public SteerContext( float minimum, float maximum, float matchThreshold, int steerCount )
    {
        super( minimum, maximum );
        
        this.matchThreshold = matchThreshold;
        this.steerings = new Steer[ steerCount ];
        this.good = new boolean[ steerCount ];
    }

    public SteerContext( boolean[] good, Steer<V>... steerings )
    {
        this( NONE, INFINITE, DEFAULT_MATCH_THRESHOLD, good, steerings );
    }

    public SteerContext( float magnitude, boolean[] good, Steer<V>... steerings )
    {
        this( NONE, magnitude, DEFAULT_MATCH_THRESHOLD, good, steerings );
    }

    public SteerContext( float minimum, float maximum, float matchThreshold, boolean[] good, Steer<V>... steerings )
    {
        super( minimum, maximum );
        
        this.matchThreshold = matchThreshold;
        this.good = good;
        this.steerings = steerings;
    }

    @Override
    public float getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        final int steerCount = steerings.length;
        final float matchThresholdScale = 1 / (1 - matchThreshold);
        final V[] normals = out.createArray( steerCount, true );
        final float[] magnitudes = new float[ steerCount ];
        
        for (int i = 0; i < steerCount; i++)
        {
            Steer<V> steer = steerings[ i ];
            
            if ( steer != null )
            {
                magnitudes[ i ] = steer.getForce( elapsed, subject, normals[ i ] );
            }
        }
        
        for (int i = 0; i < steerCount - 1; i++)
        {
            V normali = normals[ i ];
            
            for (int k = i + 1; k < steerCount; k++)
            {
                V normalk = normals[ k ];
                float match = normali.dot( normalk );                
                
                if ( match <= -matchThreshold )
                {
                    float weight = 1 + ((matchThreshold + match) * matchThresholdScale);
                    
                    magnitudes[ i ] *= weight;
                    magnitudes[ k ] *= weight;
                }
            }
        }
        
        for (int i = 0; i < steerCount; i++)
        {
            out.addsi( normals[ i ], magnitudes[ i ] );
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

        SteerContext<V> cloned = new SteerContext<V>( minimum, maximum, steerCount );

        for ( int i = 0; i < steerCount; i++ )
        {
            Steer<V> s = steerings[i];

            cloned.steerings[i] = (s == null || s.isShared()) ? s : s.clone();
        }

        return cloned;
    }

    public void add( boolean isGood, Steer<V> steer )
    {
        good = SteerMath.add( isGood, good );
        steerings = SteerMath.add( steer, steerings );
    }

    public int size()
    {
        return steerings.length;
    }

}
