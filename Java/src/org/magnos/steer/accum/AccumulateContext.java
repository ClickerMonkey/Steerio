package org.magnos.steer.accum;

import org.magnos.steer.Accumulator;
import org.magnos.steer.vec.Vec;


public class AccumulateContext<V extends Vec<V>> implements Accumulator<V>
{
    
    public static float DEFAULT_MATCH_THRESHOLD = 0.5f;
    public static int DEFAULT_SLOT_MAX = 32;
    
    public V force;
    public V[] normals;
    public float[] magnitudes;
    public int count;
    public float matchThreshold;
    
    public AccumulateContext()
    {
        this( DEFAULT_MATCH_THRESHOLD, DEFAULT_SLOT_MAX );
    }

    public AccumulateContext( int slotMax )
    {
        this( DEFAULT_MATCH_THRESHOLD, slotMax );
    }
    
    public AccumulateContext( float matchThreshold, int slotMax )
    {
        this.magnitudes = new float[ slotMax ];
    }

    @Override
    public void start( V out )
    {
        force = out;
        count = 0;
        
        if ( normals == null )
        {
            normals = out.createArray( magnitudes.length, true );
        }
    }

    @Override
    public void accumulate( V normal, float magnitude )
    {
        if ( magnitude != 0 )
        {
            final float matchThresholdScale = 1 / (1 - matchThreshold);
            final float sign = Math.signum( magnitude );
            int minimumMagnitude = -1;
            
            for (int i = 0; i < count; i++)
            {
                // Is this force the opposite type of force?
                if ( sign != Math.signum( magnitudes[i] ) )
                {
                    float match = normals[ i ].dot( normal );
                    
                    // Is it pointing in the opposite direction enough?
                    if ( match <= -matchThreshold )
                    {
                        float weight = 1 + ( ( matchThreshold + match ) * matchThresholdScale );
                        
                        magnitudes[ i ] *= weight;
                        magnitude *= weight;
                    }
                }
                
                // Keep track of the force with the smallest magnitude in the event that the array is full, we'll
                // replace the smallest force with this one.                
                if ( minimumMagnitude == -1 || Math.abs( magnitudes[ i ] ) < Math.abs( magnitudes[ minimumMagnitude ] ) )
                {
                    minimumMagnitude = i;
                }
            }
            
            if ( magnitude != 0 )
            {
                if ( count == normals.length )
                {
                    // Only replace the smallest force if this one is larger than it.
                    if ( Math.abs( magnitude ) > Math.abs( magnitudes[ minimumMagnitude ] ) )
                    {
                        normals[ minimumMagnitude ].set( normal );
                        magnitudes[ minimumMagnitude ] = magnitude;
                    }
                }
                else
                {
                    // Add this force to the end of the array.
                    normals[ count ].set( normal );
                    magnitudes[ count ] = magnitude;
                    count++;
                }
            }
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
        force.clear();
        
        for (int i = 0; i < count; i++)
        {
            force.addsi( normals[ i ], Math.abs( magnitudes[ i ] ) );
        }
        
        return force.normalize();
    }

}
