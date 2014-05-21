
package org.magnos.steer;

import org.magnos.steer.vec.Vec;

/**
 * A steering constraint on a subject after the acceleration is calculated
 * but not yet applied to the velocity, and not yet applied to the position.
 */
public interface Constraint<V extends Vec<V>>
{

    /**
     * Constraints the subject in some way between acceleration calculation and velocity and position updating.
     * 
     * @param elapsed
     *      The amount of time that has elapsed in seconds since the last update.
     * @param subject
     *      The subject to constrain.
     */
    public void constrain( float elapsed, SteerSubject<V> subject );
    
}
