package org.magnos.steer;


/**
 * A controller which applies the force from a steering behavior to a
 * subject and updates the acceleration, velocity, position, and direction
 * of the subject.
 */
public class SteerController
{
	public SteerSubject subject;
	public Steer force;
	public Constraint constraint;
	public boolean immediate;
	
	public SteerController(SteerSubject subject, Steer force )
	{
		this( subject, force, null );
	}
	
	public SteerController(SteerSubject subject, Steer force, Constraint constraint )
	{
		this.subject = subject;
		this.force = force;
		this.constraint = constraint;
		this.immediate = false;
		this.updateDirection();
	}
	
	public void updateDirection()
	{
		Vector v = subject.getVelocity();
		Vector d = subject.getDirection();
		
		if (!v.isZero(SteerMath.EPSILON)) 
		{
			d.set( v ).normali();
		}
		else if (d.isZero(SteerMath.EPSILON))
		{
			d.set( 1, 0 );
		}
	}
	
	public void update( float elapsed )
	{
		Vector a = subject.getAcceleration();
		Vector v = subject.getVelocity();
		Vector p = subject.getPosition();
		float amax = subject.getAccelerationMax();
		float vmax = subject.getVelocityMax();
		
		if ( immediate )
		{
			v.clear();	
		}
		
		a.set( force.getForce( elapsed, subject ) );
		
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
