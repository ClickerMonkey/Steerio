package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.SteerMath;
import org.magnos.steer.vec.Vec;


public class CatmullRomPath<V extends Vec<V>> implements Path<V>
{

	public static final float WEIGHT = 0.5f;
	public static final float[][] MATRIX = {
		{ 0, 2, 0, 0},
		{-1, 0, 1, 0},
		{ 2,-5, 4,-1},
		{-1, 3,-3, 1}
	};
	
	public V[] points;
	
	public CatmullRomPath()
	{
	}
	
	public CatmullRomPath(V ... points)
	{
		this.points = points;
	}

	@Override
	public V set( V subject, float delta )
	{
		return SteerMath.parametricCubicCurve( delta, points, MATRIX, WEIGHT, subject );
	}

}
