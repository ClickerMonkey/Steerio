package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.SteerMath;
import org.magnos.steer.Vector;


public class ParametricCubicPath implements Path
{

	public float[][] matrix;
	public float weight;
	public Vector[] points;

	public ParametricCubicPath()
	{
	}
	
	public ParametricCubicPath(float weight, float[][] matrix, Vector ... points)
	{
		this.weight = weight;
		this.matrix = matrix;
		this.points = points;
	}
	
	@Override
	public Vector set( Vector subject, float delta )
	{
		return SteerMath.parametricCubicCurve( delta, points, matrix, weight, subject );
	}

}
