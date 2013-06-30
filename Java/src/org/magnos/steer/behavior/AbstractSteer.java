package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;

/**
 * Abstract steering behavior.
 */
public abstract class AbstractSteer implements Steer
{
	public final Vector force = new Vector();
	
	public Steer clone()
	{
		return null;
	}

	public static void towards(SteerSubject subject, Vector target, Vector out)
	{
		out.directi( subject.getPosition(), target );

		maximize( subject, out );
	}
	
	public static void away(SteerSubject subject, Vector target, Vector out)
	{
		out.directi( target, subject.getPosition() );
		
		maximize( subject, out );
	}
	
	public static void backward(SteerSubject subject, Vector force, Vector out)
	{
		out.set( -force.x, -force.y );
		
		maximize( subject, out );
	}
	
	public static void forward(SteerSubject subject, Vector force, Vector out)
	{
		out.set( force );
		
		maximize( subject, out );
	}
	
	public static void maximize(SteerSubject subject, Vector force)
	{
		if (force.length( subject.getAccelerationMax() ) > 0) 
		{
			force.subi( subject.getVelocity() );
		}
	}
	
	public static boolean inFront(Vector pos, Vector dir, Vector point)
	{
		return ( dir.dot( point.sub( pos ) ) > 0 );
	}
	
	public static boolean inBack(Vector pos, Vector dir, Vector point)
	{
		return ( dir.dot( pos.sub( point ) ) > 0 );
	}
	
	public static float intersectionTime(SteerSubject bullet, SteerSubject target)
	{
		return SteerMath.interceptTime( target.getPosition(), target.getVelocity().length(), bullet.getPosition(), bullet.getVelocity() );
	}
	
}
