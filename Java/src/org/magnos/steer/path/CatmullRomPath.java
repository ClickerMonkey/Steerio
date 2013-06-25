package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.SteerMath;
import org.magnos.steer.Vector;


public class CatmullRomPath implements Path
{

	public static final float WEIGHT = 0.5f;
	public static final float[][] MATRIX = {
		{ 0, 2, 0, 0},
		{-1, 0, 1, 0},
		{ 2,-5, 4,-1},
		{-1, 3,-3, 1}
	};
	
	public Vector[] points;
	
	public CatmullRomPath()
	{
	}
	
	public CatmullRomPath(Vector ... points)
	{
		this.points = points;
	}

	@Override
	public Vector set( Vector subject, float delta )
	{
		return SteerMath.parametricCubicCurve( delta, points, MATRIX, WEIGHT, subject );
	}

}
