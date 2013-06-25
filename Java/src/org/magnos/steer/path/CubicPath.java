package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.Vector;

public class CubicPath<T> implements Path 
{

	public Vector p0;
	public Vector p1;
	public Vector p2;
	public Vector p3;

	public CubicPath()
	{
	}
	
	public CubicPath(Vector p0, Vector p1, Vector p2, Vector p3)
	{
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}
	 
	@Override
	public Vector set(Vector subject, float d1) 
	{
		float d2 = d1 * d1;
		float d3 = d1 * d2;
		float i1 = 1 - d1;
		float i2 = i1 * i1;
		float i3 = i1 * i2;
	
		subject.set( p0 );
		subject.muli( i3 );
		subject.addsi( p1, 3 * i2 * d1 );
		subject.addsi( p2, 3 * i1 * d2 );
		subject.addsi( p3, d3 );
		
		return subject;
	}

}
