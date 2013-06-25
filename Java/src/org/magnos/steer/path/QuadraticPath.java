
package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.Vector;


public class QuadraticPath implements Path
{

	public Vector p0;
	public Vector p1;
	public Vector p2;

	public QuadraticPath()
	{
	}

	public QuadraticPath( Vector p0, Vector p1, Vector p2 )
	{
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	public Vector set( Vector subject, float d1 )
	{
		float d2 = d1 * d1;
		float i1 = 1 - d1;
		float i2 = i1 * i1;

		subject.set( p0 );
		subject.muli( i2 );
		subject.addsi( p1, 2 * i1 * d1 );
		subject.addsi( p2, d2 );

		return subject;
	}

}
