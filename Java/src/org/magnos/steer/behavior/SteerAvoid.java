
package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec;


/**
 * A steering behavior that moves the subject away when a target is within a given distance and the target is facing the subject.
 */
public class SteerAvoid<V extends Vec<V>> extends AbstractSteer<V>
{

    public SteerSubject<V> target;
    public float distance;
    public boolean shared;

    /**
     * Instantiates a new SteerAvoid that can be shared.
     * 
     * @param target
     *        The target to be avoided.
     * @param distance
     *        The radius of the circle around the subject which determines whether this
     */
    public SteerAvoid( SteerSubject<V> target, float distance )
    {
        this( target, distance, true );
    }

    /**
     * 
     * @param target
     * @param distance
     * @param shared
     */
    public SteerAvoid( SteerSubject<V> target, float distance, boolean shared )
    {
        this.target = target;
        this.distance = distance;
        this.shared = shared;
    }

    @Override
    public void getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        SteerSubject<V> target = getTarget();
        V targetPosition = target.getPosition();
        V targetDirection = target.getDirection();
        V subjectPosition = subject.getPosition();

        if ( distance == INFINITE || targetPosition.distanceSq( subjectPosition ) < distance * distance )
        {
            if ( inFront( targetPosition, targetDirection, subjectPosition ) )
            {
                away( subject, targetPosition, out, this );
            }
        }
    }

    @Override
    public boolean isShared()
    {
        return shared;
    }

    public SteerSubject<V> getTarget()
    {
        return target;
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerAvoid<V>( target, distance, shared );
    }

}
