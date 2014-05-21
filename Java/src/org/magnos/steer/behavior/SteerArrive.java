
package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.vec.Vec;


/**
 * A steering behavior that moves the subject closer to a target with maximum acceleration but slows down to a complete stop once it comes within a
 * given distance.
 */
public class SteerArrive<V extends Vec<V>> extends AbstractSteer<V>
{

    public Target<V> target;
    public float caution;
    public float arrived;
    public boolean shared;

    /**
     * Instantiates a new SteerArrive that can be shared.
     * 
     * @param target
     *        The target to steer towards and arrive at.
     * @param caution
     *        The radius of the circle around the target to slow down once entered.
     * @param arrived
     *        The distance from the target that is considered "arrived". A common value for this is 0.
     */
    public SteerArrive( Target<V> target, float caution, float arrived )
    {
        this( target, caution, arrived, true );
    }

    /**
     * Instantiates a new SteerArrive.
     * 
     * @param target
     *        The target to steer towards and arrive at.
     * @param caution
     *        The radius of the circle around the target to slow down once entered.
     * @param arrived
     *        The distance from the target that is considered "arrived". A common value for this is 0.
     * @param shared
     *        Whether this {@link Steer} implementation can be shared between {@link SteerSubject}s.
     */
    public SteerArrive( Target<V> target, float caution, float arrived, boolean shared )
    {
        this.target = target;
        this.caution = caution;
        this.arrived = arrived;
        this.shared = shared;
    }

    @Override
    public void getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        V targetPosition = target.getTarget( subject );

        if ( targetPosition != null )
        {
            out.directi( subject.getPosition(), targetPosition );

            float distance = out.length();

            if ( distance > arrived )
            {
                float factor = Math.min( distance / caution, 1 );

                out.divi( distance );
                out.muli( subject.getAccelerationMax() );
                out.muli( factor * factor );
                out.subi( subject.getVelocity() );
                out.divi( elapsed );
            }
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
        return new SteerArrive<V>( target, caution, arrived, shared );
    }

}
