
package org.magnos.steer;

import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


/**
 * A 2d entity with position, direction, velocity, and acceleration.
 */
public interface SteerSubject<V extends Vec<V>> extends Target<V>, SpatialEntity<V>
{

    /**
     * The current position of the subject.
     */
    public V getPosition();

    /**
     * A normalized vector which is the direction of the subject. The direction is updated when velocity is changed internally, however if you
     * manually change the velocity of the subject you are responsible for updating the direction:
     * 
     * <pre>
     * ss.getVelocity().normal( ss.getDirection() );
     * </pre>
     */
    public V getDirection();

    /**
     * The current velocity of the subject.
     */
    public V getVelocity();

    /**
     * The current acceleration of the subject.
     */
    public V getAcceleration();

}
