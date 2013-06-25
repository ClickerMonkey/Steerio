package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;


/**
 * A steering behavior that applies deceleration and simple thrusting, turning, 
 * and braking forces if certain flags on the behavior are true.
 */
public class SteerDrive extends AbstractSteer
{
	public float thrust;
	public float turn;
	public float brake;
	public float deceleration;
	public boolean shared;
	
	public boolean thrusting = false;
	public boolean turnLeft = false;
	public boolean turnRight = false;
	public boolean braking = false;
	
	public SteerDrive(float thrust, float turn, float brake, float deceleration)
	{
		this( thrust, turn, brake, deceleration, true );
	}
	
	public SteerDrive(float thrust, float turn, float brake, float deceleration, boolean shared)
	{
		this.thrust = thrust;
		this.turn = turn;
		this.brake = brake;
		this.deceleration = deceleration;
		this.shared = shared;
	}
	
	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		Vector dir = subject.getDirection();
		Vector vel = subject.getVelocity();
		float speed = vel.length();
		
		force.clear();
		
		if (thrusting) 
		{
			force.addsi(dir, thrust);
		}
		
		if (braking) 
		{
			force.addsi(dir, -Math.min(brake, speed));
		}
		
		if (turnLeft) 
		{
			force.x += dir.y * turn;
			force.y -= dir.x * turn;
		}
		
		if (turnRight) 
		{
			force.x -= dir.y * turn;
			force.y += dir.x * turn;
		}
		
		if ( speed > 0.001f ) 
		{
			force.addsi( vel, -Math.max(speed, deceleration * elapsed) / speed );
		}
		
		return force;
	}

	@Override
	public boolean isShared()
	{
		return shared;
	}

	@Override
	public Steer clone()
	{
		return new SteerDrive( thrust, turn, brake, deceleration, shared );
	}

}
