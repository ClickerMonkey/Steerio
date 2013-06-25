package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.Vector;


public class Tween implements Path
{
	
	public Vector start;
	public Vector end;

	public Tween()
	{
	}
	
	public Tween(Vector start, Vector end) 
	{
		this.start = start;
		this.end = end;
	}
	
	@Override
	public Vector set(Vector subject, float delta) 
	{
		subject.interpolate(start, end, delta);
		
		return subject;
	}
	
}