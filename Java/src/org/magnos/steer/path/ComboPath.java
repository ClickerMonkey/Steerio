package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.Vector;


public class ComboPath implements Path
{

	public Path[] paths;
	public float[] times;

	public ComboPath( Path[] paths, Vector point, int points )
	{
		this( paths, getPathTimes( paths, point, points ) );
	}
	
	public ComboPath( Path[] paths, float[] times )
	{
		this.paths = paths;
		this.times = times;
	}
	
	@Override
	public Vector set( Vector subject, float delta )
	{
		if (delta <= 0)
		{
			paths[0].set( subject, 0 );
		}
		else if (delta >= 1)
		{
			paths[ paths.length - 1 ].set( subject, 1 );
		}
		else
		{
			int i = paths.length - 1;
			while (times[i] > delta) --i;
			float q = (delta - times[i]) / (times[i + 1] - times[i]);
			
			paths[i].set( subject, q );
		}
		
		return subject;
	}

	public static <T> float[] getPathTimes(Path[] paths, Vector point, int points)
	{
		int n = paths.length;
		float[] times = new float[ n + 1 ];
		
		times[0] = 0;
		for (int k = 1; k <= n; k++)
		{
			times[k] = times[k - 1] + getPathLength( paths[k - 1], point, points ); 
		}
		
		float scale = 1.0f / times[n];
		
		times[n] = 1.0f;
		for (int k = 1; k < n; k++)
		{
			times[k] *= scale;
		}
		
		return times;
	}
	
	public static <T> float getPathLength(Path path, Vector point, int points)
	{
		Vector start = new Vector();
		Vector end = new Vector();
		float length = 0;
		
		path.set( start, 0 );
		
		for (int i = 1; i <= points; i++)
		{
			path.set( end, (float)i / points );
			length += end.distance( start );
			start = end;
		}
		
		return length;
	}
	
}
