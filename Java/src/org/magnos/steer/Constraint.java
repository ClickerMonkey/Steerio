package org.magnos.steer;

/**
 * A steering constraint on a subject after the acceleration is calculated
 * but not yet applied to the velocity, and not yet applied to the position.
 */
public interface Constraint
{
	public void constrain(float elapsed, SteerSubject subject);
}
