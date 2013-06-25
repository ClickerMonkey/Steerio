package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.Vector;


public class SubPath<T> implements Path
{

	public float start;
	public float end;
	public Path path;
	
	public SubPath( float start, float end, Path path )
	{
		this.start = start;
		this.end = end;
		this.path = path;
	}

	@Override
	public Vector set( Vector subject, float delta )
	{
		path.set( subject, (end - start) * delta + start );
		
		return subject;
	}
	
}
