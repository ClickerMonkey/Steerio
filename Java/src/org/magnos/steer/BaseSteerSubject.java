package org.magnos.steer;

import org.magnos.steer.spatial.SpatialDatabase;


public class BaseSteerSubject implements SteerSubject
{

	public Vector position = new Vector();
	public Vector direction = new Vector(1, 0);
	public Vector velocity = new Vector();
	public float velocityMax;
	public Vector acceleration = new Vector();
	public float accelerationMax;
	public float radius;
	public long groups = SpatialDatabase.ALL_GROUPS;
	public boolean dynamic = true;
	public boolean inert = false;
	public SteerController controller;
	
	public BaseSteerSubject(float radius, float max)
	{
		this( radius, max, max, null );
	}
	
	public BaseSteerSubject(float radius, float velocityMax, float accelerationMax)
	{
		this( radius, velocityMax, accelerationMax, null );
	}
	
	public BaseSteerSubject(float radius, float velocityMax, float accelerationMax, Steer steer)
	{
		this.radius = radius;
		this.velocityMax = velocityMax;
		this.accelerationMax = accelerationMax;
		
		if (steer != null)
		{
			this.controller = new SteerController( this, steer );	
		}
	}
	
	public void update(float elapsed)
	{
		if (controller != null)
		{
			controller.update( elapsed );
		}
	}
	
	@Override
	public Vector getTarget( SteerSubject subject )
	{
		return position;
	}

	@Override
	public float getRadius()
	{
		return radius;
	}

	@Override
	public long getSpatialGroups()
	{
		return groups;
	}

	@Override
	public boolean isStatic()
	{
		return !dynamic;
	}

	@Override
	public boolean isInert()
	{
		return inert;
	}

	@Override
	public Vector getPosition()
	{
		return position;
	}

	@Override
	public Vector getDirection()
	{
		return direction;
	}

	@Override
	public Vector getVelocity()
	{
		return velocity;
	}

	@Override
	public float getVelocityMax()
	{
		return velocityMax;
	}

	@Override
	public Vector getAcceleration()
	{
		return acceleration;
	}

	@Override
	public float getAccelerationMax()
	{
		return accelerationMax;
	}

}
