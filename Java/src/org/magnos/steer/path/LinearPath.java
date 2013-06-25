package org.magnos.steer.path;

import org.magnos.steer.Vector;

public class LinearPath extends TimedPath
{
	
	public LinearPath()
	{
	}
	
	public LinearPath( Vector ... points) 
	{
		super( points, getTimes( points ) );
	}
	
	public static <T> float[] getTimes(Vector[] points)
	{
		int n = points.length;
		float[] distances = new float[n--];
		
		distances[0] = 0;
		for (int i = 1; i <= n; i++) 
		{
			distances[i] = distances[i - 1] + points[i - 1].distance( points[i] );
		}
		
		float length = 1f / distances[ n ];
		for (int i = 1; i < n; i++) 
		{
			distances[i] *= length;
		}
		distances[n] = 1f;
		
		return distances;
	}
	
}
