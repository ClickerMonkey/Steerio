package org.magnos.steer;

import org.magnos.steer.spatial.SpatialEntity;


/**
 * Anything that is steerable by a steering behavior.
 */
public interface SteerSubject extends Target, SpatialEntity
{
	
	public Vector getPosition();
	public Vector getDirection();
	public Vector getVelocity();
	public float getVelocityMax();
	public Vector getAcceleration();
	public float getAccelerationMax();
	
}