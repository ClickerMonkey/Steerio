package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.Vector;


public class PointPath implements Path
{
	
	public Vector value;
	
	public PointPath(Vector value)
	{
		this.value = value;
	}

	@Override
	public Vector set( Vector subject, float delta )
	{
		subject.set( value );
		
		return subject;
	}

}
