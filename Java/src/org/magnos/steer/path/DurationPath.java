package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.SteerMath;
import org.magnos.steer.Vector;


public class DurationPath implements Path
{

	public float[] durations;
	public Vector[] points;
	public float durationTotal;
	
	public DurationPath()
	{
	}
	
	public DurationPath( Vector initialPoint )
	{
		this( new Vector[] { initialPoint }, new float[] {} );
	}
	
	public DurationPath( Vector[] points, float[] durations )
	{
		this.points = points;
		this.durations = durations;
		this.updateDuration();
	}
	
	public void setPoints( Vector[] points )
	{
		this.points = points;
	}
	
	public void setDurations( float[] durations )
	{
		this.durations = durations;
		this.updateDuration();
	}
	
	public DurationPath withPoints( Vector ... points )
	{
		this.points = points;
		
		return this;
	}
	
	public DurationPath withDurations( float ... durations )
	{
		this.durations = durations;
		this.updateDuration();
		
		return this;
	}
	
	public DurationPath addPoint( Vector point, float duration )
	{
		points = SteerMath.add( point, points );
		durations = SteerMath.add( duration, durations );
		durationTotal += duration;
		
		return this;
	}
	
	public float updateDuration()
	{
		durationTotal = 0;
		
		for ( int i = 0; i < durations.length; i++ )
		{
			durationTotal += durations[ i ];
		}
		
		return durationTotal;
	}
	
	@Override
	public Vector set( Vector subject, float delta )
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
			int i = 0;
			float d = delta * durationTotal;
			
			while ( d > durations[i] ) {
				d -= durations[i++];
			}
			
			float q = d / durations[i];
			
			subject.interpolate( points[i], points[i + 1], q );
		}
		
		return subject;
	}

}
