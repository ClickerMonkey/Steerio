
package org.magnos.steer;

import org.magnos.steer.vec.Vec;


/**
 * A steering behavior which encapsulates an array of steering behaviors
 * prioritized by their order in the array. A maximum value can be given, if
 * the accumulated force from the steerings is greater than the maximum value
 * then the accumulation ceases (this creates prioritization). {@link SteerSet} can be shared if all steering behaviors can also be shared, otherwise
 * a
 * clone must be made to share this set with another subject.
 */
public class SteerSet<V extends Vec<V>> implements Steer<V>
{

    public float maximum;
    public Steer<V>[] steerings;

    protected boolean maximized = false;

    public SteerSet()
    {
        this( 0, INFINITE );
    }

    public SteerSet( float maximum )
    {
        this( 0, maximum );
    }

    public SteerSet( int steerCount, float maximum )
    {
        this.steerings = new Steer[steerCount];
        this.maximum = maximum;
    }

    public SteerSet( Steer<V>... steerings )
    {
        this( INFINITE, steerings );
    }

    public SteerSet( float maximum, Steer<V>... steerings )
    {
        this.maximum = maximum;
        this.steerings = steerings;
    }

    @Override
    public void getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        V temp = out.create();

        float maximumSq = maximum * maximum;

        final int steerCount = steerings.length;

        for ( int i = 0; i < steerCount; i++ )
        {
            Steer<V> steer = steerings[i];

            if ( steer != null )
            {
                temp.clear();

                steer.getForce( elapsed, subject, temp );

                if ( !temp.isZero() )
                {
                    out.addi( temp );

                    if ( maximum != INFINITE && out.lengthSq() > maximumSq )
                    {
                        out.clamp( 0, maximum );

                        break;
                    }
                }
            }
        }
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
    public boolean isMaximized()
    {
        return maximized;
    }

    @Override
    public void setMaximized( boolean maximize )
    {
        this.maximized = maximize;
    }

    @Override
    public Steer<V> clone()
    {
        int steerCount = steerings.length;

        SteerSet<V> cloned = new SteerSet<V>();
        cloned.maximum = maximum;
        cloned.steerings = new Steer[steerCount];

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
