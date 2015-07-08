
package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.vec.Vec;


/**
 * A steering behavior that moves the subject closer to a target with maximum
 * acceleration if their within a certain distance.
 */
public class SteerTo<V extends Vec<V>> extends AbstractSteer<V, SteerTo<V>>
{

    public Target<V> target;
    public boolean shared;

    public SteerTo( float minimum, float maximum, Target<V> target )
    {
        this( minimum, maximum, target, DEFAULT_SHARED );
    }

    public SteerTo( float magnitude, Target<V> target )
    {
        this( magnitude, magnitude, target, DEFAULT_SHARED );
    }

    public SteerTo( float magnitude, Target<V> target, boolean shared )
    {
        this( magnitude, magnitude, target, shared );
    }

    public SteerTo( float minimum, float maximum, Target<V> target, boolean shared )
    {
        super( minimum, maximum );
        
        this.target = target;
        this.shared = shared;
    }

    @Override
    public float getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        V targetPosition = target.getTarget( subject );

        if ( targetPosition != null )
        {
            return towards( subject, targetPosition, out, this );
        }
        
        return Steer.NONE;
    }

    @Override
    public boolean isShared()
    {
        return shared;
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerTo<V>( minimum, maximum, target, shared );
    }

}
