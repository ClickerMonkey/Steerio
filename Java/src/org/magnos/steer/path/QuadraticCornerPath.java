package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.SteerMath;
import org.magnos.steer.Vector;

/**
 * 
 * @author Philip Diffenderfer
 *
 * @param <T>
 */
public class QuadraticCornerPath implements Path
{
	
	protected Vector[] points;
	protected final Vector temp0 = new Vector();
	protected final Vector temp1 = new Vector();
	protected boolean loops;
	protected float midpoint;
	
	public QuadraticCornerPath()
	{
	}
	
	public QuadraticCornerPath( float midpoint, boolean loops, Vector ... points ) 
	{
		this.midpoint = midpoint;
		this.loops = loops;
		this.points = points;
	}
	
	@Override
	public Vector set(Vector subject, float delta) 
	{
		final float negmidpoint = 1.0f - midpoint;
		final float halfmidpoint = midpoint * 0.5f;
		final int n = points.length - (loops ? 0 : 1);
		final float a = delta * n;
		final int i = SteerMath.clamp( (int)a, 0, n - 1 );
		float d = a - i;
		
		Vector p0 = points[ getActualIndex( i - 1 ) ];
		Vector p1 = points[ getActualIndex( i ) ];
		Vector p2 = points[ getActualIndex( i + 1 ) ];
		Vector p3 = points[ getActualIndex( i + 2 ) ];
		
		if ( d < midpoint )
		{
			d = (d / midpoint);
			
			temp0.interpolate( p0, p1, d * halfmidpoint + negmidpoint + halfmidpoint );
			temp1.interpolate( p1, p2, d * halfmidpoint + halfmidpoint );
			
			p1 = temp0;
			p2 = temp1;
			d = d * 0.5f + 0.5f;
		}
		else if ( d > negmidpoint )
		{
			d = (d - negmidpoint) / midpoint;
			
			temp0.interpolate( p1, p2, d * halfmidpoint + negmidpoint );
			temp1.interpolate( p2, p3, d * halfmidpoint );
			
			p1 = temp0;
			p2 = temp1;
			d = d * 0.5f;
		}
		
		subject.interpolate( p1, p2, d );
		
		return subject;
	}
	
	public int getActualIndex( int index )
	{
		final int n = points.length;
		
		return ( loops ? (index + n) % n : SteerMath.clamp( index, 0, n - 1 ) );
	}
	
}
