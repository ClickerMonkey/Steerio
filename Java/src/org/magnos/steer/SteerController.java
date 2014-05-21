package org.magnos.steer;

import org.magnos.steer.vec.Vec;


/**
 * A controller which applies the force from a steering behavior to a
 * subject and updates the acceleration, velocity, position, and direction
 * of the subject.
 */
public class SteerController<V extends Vec<V>>
{
	public SteerSubject<V> subject;
	public Steer<V> force;
	public Constraint<V> constraint;
	public boolean immediate;
	
	public SteerController(SteerSubject<V> subject, Steer<V> force )
	{
		this( subject, force, null );
	}
	
	public SteerController(SteerSubject<V> subject, Steer<V> force, Constraint<V> constraint )
	{
		this.subject = subject;
		this.force = force;
		this.constraint = constraint;
		this.immediate = false;
		this.updateDirection();
	}
	
	public void updateDirection()
	{
		V v = subject.getVelocity();
		V d = subject.getDirection();
		
		if (!v.isZero(SteerMath.EPSILON)) 
		{
			d.set( v ).normali();
		}
		else if (d.isZero(SteerMath.EPSILON))
		{
		    d.defaultUnit();
		}
	}
	
	public void update( float elapsed )
	{
		V a = subject.getAcceleration();
		V v = subject.getVelocity();
		V p = subject.getPosition();
		float amax = subject.getAccelerationMax();
		float vmax = subject.getVelocityMax();
		
		if ( immediate )
		{
			v.clear();	
		}
		
		a.clear();
		force.getForce( elapsed, subject, a );
		
		if (amax != Steer.INFINITE)
		{
			a.max( amax );
		}

		if ( constraint != null )
		{
			constraint.constrain( elapsed, subject );
		}
		
		v.addsi( a, immediate ? 1.0f : elapsed );
		
		if (vmax != Steer.INFINITE)
		{
			v.max( vmax );
		}
		
		p.addsi( v, elapsed );
		
		updateDirection();
	}
	
}
