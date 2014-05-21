package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec2;


/**
 * A steering behavior that applies deceleration and simple thrusting, turning, 
 * and braking forces if certain flags on the behavior are true.
 */
public class SteerDrive2 extends AbstractSteer<Vec2>
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
	
	public SteerDrive2(float thrust, float turn, float brake, float deceleration)
	{
		this( thrust, turn, brake, deceleration, true );
	}
	
	public SteerDrive2(float thrust, float turn, float brake, float deceleration, boolean shared)
	{
		this.thrust = thrust;
		this.turn = turn;
		this.brake = brake;
		this.deceleration = deceleration;
		this.shared = shared;
	}
	
	@Override
	public void getForce( float elapsed, SteerSubject<Vec2> subject, Vec2 out )
	{
		Vec2 dir = subject.getDirection();
		Vec2 vel = subject.getVelocity();
		float speed = vel.length();
		
		if (thrusting) 
		{
		    out.addsi(dir, thrust);
		}
		
		if (braking) 
		{
		    out.addsi(dir, -Math.min(brake, speed));
		}
		
		if (turnLeft) 
		{
		    out.x += dir.y * turn;
		    out.y -= dir.x * turn;
		}
		
		if (turnRight) 
		{
		    out.x -= dir.y * turn;
		    out.y += dir.x * turn;
		}
		
		if ( speed > 0.001f ) 
		{
		    out.addsi( vel, -Math.max(speed, deceleration * elapsed) / speed );
		}
	}

	@Override
	public boolean isShared()
	{
		return shared;
	}

	@Override
	public Steer<Vec2> clone()
	{
		return new SteerDrive2( thrust, turn, brake, deceleration, shared );
	}

}
