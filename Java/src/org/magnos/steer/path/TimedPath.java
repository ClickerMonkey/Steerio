package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.Vector;


public class TimedPath implements Path
{

	public float[] times;
	public Vector[] points;
	
	public TimedPath()
	{
	}
	
	public TimedPath( Vector [] points, float[] times )
	{
		this.points = points;
		this.times = times;
	}
	
	protected void setPoints( Vector [] points )
	{
		this.points = points;
	}
	
	protected void setTimes( float[] times )
	{
		this.times = times;
	}
	
	@Override
	public Vector set( Vector  subject, float delta )
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
			int i = points.length - 1;
			while (times[i] > delta) --i;
			float q = (delta - times[i]) / (times[i + 1] - times[i]);
			
			subject.interpolate( points[i], points[i + 1], q );
		}
		
		return subject;
	}

}
