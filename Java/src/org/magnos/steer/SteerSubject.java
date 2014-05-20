
package org.magnos.steer;

import org.magnos.steer.spatial.SpatialEntity;


/**
 * A 2d entity with position, direction, velocity, and acceleration.
 */
public interface SteerSubject extends Target, SpatialEntity
{

    /**
     * The current position of the subject.
     */
    public Vector getPosition();

    /**
     * A normalized vector which is the direction of the subject. The direction is updated when velocity is changed internally, however if you
     * manually change the velocity of the subject you are responsible for updating the direction:
     * 
     * <pre>
     * ss.getVelocity().normal( ss.getDirection() );
     * </pre>
     */
    public Vector getDirection();

    /**
     * The current velocity of the subject.
     */
    public Vector getVelocity();

    /**
     * The maximum allowed velocity for this subject.
     */
    public float getVelocityMax();

    /**
     * The current acceleration of the subject.
     */
    public Vector getAcceleration();

    /**
     * The maximum allowed acceleration for this subject.
     */
    public float getAccelerationMax();

}
