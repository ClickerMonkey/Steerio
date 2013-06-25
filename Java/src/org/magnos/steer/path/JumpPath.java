package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.SteerMath;
import org.magnos.steer.Vector;

/**
 *                                     delta
 *                                       |
 * 0.0       0.25       0.5       0.75   V    1.0
 * |----------|----------|----------|----------	(4)
 * 0          1          2          3          
 * |________  |________  |________  |__________
 * 0 0 0 0 0  1 1 1 1 1  2 2 2 2 2  3 3 3 3 3 3 
 * 
 * @param <T>
 */
public class JumpPath implements Path 
{
	
	public Vector[] points;
	
	public JumpPath()
	{
	}
	
	public JumpPath( Vector ... jumps ) 
	{
		this.points = jumps;
	}
	
	@Override
	public Vector set(Vector subject, float delta) 
	{
		float a = delta * points.length;
		int index = SteerMath.clamp( (int)a, 0, points.length - 1 );
		
		subject.set( points[ index ] );
		
		return subject;
	}
	
}
