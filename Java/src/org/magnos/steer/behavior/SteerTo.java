
package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.vec.Vec;


/**
 * A steering behavior that moves the subject closer to a target with maximum
 * acceleration if their within a certain distance.
 */
public class SteerTo<V extends Vec<V>> extends AbstractSteer<V>
{

    public Target<V> target;
    public boolean shared;

    public SteerTo( Target<V> target )
    {
        this( target, true );
    }

    public SteerTo( Target<V> target, boolean shared )
    {
        this.target = target;
        this.shared = shared;
    }

    @Override
    public void getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        V targetPosition = target.getTarget( subject );

        if ( targetPosition != null )
        {
            towards( subject, targetPosition, out, this );
        }
    }

    @Override
    public boolean isShared()
    {
        return shared;
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerTo<V>( target, shared );
    }

}
