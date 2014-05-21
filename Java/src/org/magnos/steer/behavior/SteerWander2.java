package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec2;


/**
 * A steering behavior that can smoothly move the subject in a random direction
 * at maximum acceleration.
 */
public class SteerWander2 extends AbstractSteer<Vec2>
{

	public float theta;
	public float radius;
	public float distance;
	public float wander;
	
	public SteerWander2(float theta, float radius, float distance, float wander)
	{
		this.theta = theta;
		this.radius = radius;
		this.distance = distance;
		this.wander = wander;
	}
	
	@Override
	public void getForce( float elapsed, SteerSubject<Vec2> subject, Vec2 out )
	{
		theta += ( SteerMath.randomFloat( wander ) - (wander * 0.5f) ) * elapsed;
		
		out.angle( theta, radius );
		out.addsi( subject.getDirection(), distance );
		maximize( subject, out );
	}

	@Override
	public boolean isShared()
	{
		return false;
	}

	@Override
	public Steer<Vec2> clone()
	{
		return new SteerWander2( theta, radius, distance, wander );
	}

}
