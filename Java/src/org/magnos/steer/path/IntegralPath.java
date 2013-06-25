package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.SteerMath;
import org.magnos.steer.Vector;

/**
 *                                     delta
 *                                       |
 * 0.0       0.25       0.5       0.75   V    1.0
 * |----------|----------|----------|----------|		(5)
 * 0          1          2          3          4
 * 
 * @param <T>
 */
public class IntegralPath<T> implements Path
{
	
	public Vector[] points;
	
	public IntegralPath()
	{
	}
	
	public IntegralPath(Vector ... points ) 
	{
		this.points = points;
	}
	
	@Override
	public Vector set(Vector subject, float delta) 
	{
		if (delta <= 0) 
		{
			subject.set( points[0] );
		}
		else if (delta >= 1) 
		{
			subject.set( points[points.length - 1] );
		}
		else 
		{
			float a = delta * (points.length - 1);
			int index = SteerMath.clamp( (int)a, 0, points.length - 2 );
			
			subject.interpolate( points[index], points[index + 1], a - index );	
		}
		
		return subject;
	}

}
