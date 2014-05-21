package org.magnos.steer.path;

import org.magnos.steer.vec.Vec;

public class LinearPath<V extends Vec<V>> extends TimedPath<V>
{
	
	public LinearPath()
	{
	}
	
	public LinearPath( V ... points) 
	{
		super( points, getTimes( points ) );
	}
	
	public static <V extends Vec<V>> float[] getTimes(V[] points)
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
