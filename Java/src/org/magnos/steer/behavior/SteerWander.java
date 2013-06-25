package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;


/**
 * A steering behavior that can smoothly move the subject in a random direction
 * at maximum acceleration.
 */
public class SteerWander extends AbstractSteer
{

	public float theta;
	public float radius;
	public float distance;
	public float wander;
	
	public SteerWander(float theta, float radius, float distance, float wander)
	{
		this.theta = theta;
		this.radius = radius;
		this.distance = distance;
		this.wander = wander;
	}
	
	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		theta += ( SteerMath.randomFloat( wander ) - (wander * 0.5f) ) * elapsed;
		
		force.angle( theta, radius );
		force.addsi( subject.getDirection(), distance );
		maximize( subject, force );
		
		return force;
	}

	@Override
	public boolean isShared()
	{
		return false;
	}

	@Override
	public Steer clone()
	{
		return new SteerWander( theta, radius, distance, wander );
	}

}
