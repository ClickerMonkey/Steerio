package org.magnos.steer.behavior;

import org.magnos.steer.Path;
import org.magnos.steer.Steer;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;

/**
 * A steering behavior which moves the subject along a path keeping within a
 * maximum distance away. The direction of the subject and a lookahead value
 * calculates the future position of the subject which is used to determine
 * if the subject needs to move closer to the path to stay within the maximum
 * distance. A direction is given (-1 or 1) to note which way along the path
 * the subject should move, if bounces is true and the subject reached the end 
 * of the path (or start) the direction will change automatically. A granularity
 * is given which dictates how much step along the path by to determine the 
 * closest point on the path to the future position of the subject. A skip
 * is given which is the amount to increase from the current position on the
 * path to find the closest point, every delta value is visited between the
 * current position and current position + skip.
 */
public class SteerPath extends AbstractSteer
{
	
	public Path path;
	public float granularity;
	public float lookahead;
	public float thickness;
	public float buffer;
	public float velocity;
	public int direction;
	public boolean cyclic;
	public boolean reset;
	
	public float delta = -1;
	public Vector future = new Vector();
	public Vector ahead = new Vector();
	public Vector target = new Vector();
	
	public SteerPath(Path path, float granularity, float lookahead, float thickness, float buffer, float velocity, int direction, boolean cyclic, boolean reset)
	{
		this.path = path;
		this.granularity = granularity;
		this.lookahead = lookahead;
		this.thickness = thickness;
		this.velocity = velocity;
		this.buffer = buffer;
		this.direction = direction;
		this.cyclic = cyclic;
		this.reset = reset;
	}
	
	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		force.clear();
		
		future.set( subject.getPosition() );
		future.addsi( subject.getDirection(), velocity );
		
		if (delta == -1)
		{
			calculateClosestDelta( 0, 1 );
		}
		else
		{
			float s = delta - (granularity * direction);
			float e = delta + (lookahead * direction);
			float sc = SteerMath.clamp( s, 0, 1 );
			float ec = SteerMath.clamp( e, 0, 1 );
			float min = Math.min( sc, ec );
			float max = Math.max( sc, ec );
			
			calculateClosestDelta( min, max );
		}
		
		calculateNextTarget( subject.getPosition() );
		
		target.set( future );
		target.subi( ahead );
		
		float offsetSq = target.lengthSq();
		
		if (offsetSq > thickness * thickness)
		{
			float offset = SteerMath.sqrt( offsetSq );
			
			target.divi( offset );
			target.muli( thickness - (offset - thickness) );
		}
		
		target.addi( ahead );
		
		towards( subject, target, force, this );
		
		return force;
	}

	protected void calculateClosestDelta(float min, float max)
	{
		Vector p = new Vector();
		float dsq = Float.MAX_VALUE;
		
		while (min <= max)
		{
			min = Math.min( min, max );
			
			path.set( p, min );
			
			float aheadSq = future.distanceSq( p ); 
			
			if (aheadSq < dsq)
			{
				delta = min;
				dsq = aheadSq;
			}
			
			min += granularity;
		}
	}
	
	protected void calculateNextTarget(Vector away)
	{
		Vector p = new Vector();
		float velocitySq = velocity * velocity;
		float awaySq = 0f;
		
		while (delta >= 0 && delta <= 1)
		{
			path.set( p, delta );
			
			awaySq = away.distanceSq( p );
			
			if (awaySq > velocitySq)
			{
				ahead.set( p );
				
				return;
			}
			
			delta += granularity * direction;
		}

		delta = SteerMath.clamp( delta, 0, 1 );
		path.set( ahead, delta );
		awaySq = away.distanceSq( ahead );
		
		if (awaySq < buffer * buffer)
		{
			if (reset)
			{
				delta = 1 - delta;
				path.set( ahead, delta );
			}
			else if (cyclic)
			{
				direction = -direction;	
			}
		}
	}
	
	@Override
	public boolean isShared()
	{
		return false;
	}

	@Override
	public Steer clone()
	{
		return new SteerPath( path, granularity, lookahead, thickness, buffer, velocity, direction, cyclic, reset );
	}

}
