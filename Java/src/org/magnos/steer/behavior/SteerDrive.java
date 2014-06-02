package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec;


/**
 * A steering behavior that applies deceleration and simple thrusting, turning, 
 * and braking forces if certain flags on the behavior are true.
 */
public class SteerDrive<V extends Vec<V>> extends AbstractSteer<V>
{
    
	public float thrust;
	public float brake;
	public float deceleration;
	public boolean shared;
	public boolean thrusting = false;
	public boolean braking = false;
    public V[] turnForce;
    public boolean[] turn;
	
	public SteerDrive(float thrust, float brake, float deceleration, V ... turnForce)
	{
		this( thrust, brake, deceleration, true, turnForce );
	}
	
	public SteerDrive(float thrust, float brake, float deceleration, boolean shared, V ... turnForce)
	{
		this.thrust = thrust;
		this.brake = brake;
		this.deceleration = deceleration;
		this.shared = shared;
		this.turnForce = turnForce;
		this.turn = new boolean[ turnForce.length ];
	}
	
	@Override
	public void getForce( float elapsed, SteerSubject<V> subject, V out )
	{
		V dir = subject.getDirection();
		V vel = subject.getVelocity();
		float speed = vel.length();
		
		if (thrusting) 
		{
		    out.addsi(dir, thrust);
		}
		
		if (braking) 
		{
		    out.addsi(dir, -Math.min(brake, speed));
		}
		
		for (int i = 0; i < turn.length; i++)
		{
		    if (turn[i])
		    {
		        out.addi( turnForce[i].rotate( dir ) );
		    }
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
	public Steer<V> clone()
	{
		return new SteerDrive<V>( thrust, brake, deceleration, shared, turnForce );
	}

}
