package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.SteerMath;
import org.magnos.steer.vec.Vec;


public class ParametricCubicPath<V extends Vec<V>> implements Path<V>
{

	public float[][] matrix;
	public float weight;
	public V[] points;

	public ParametricCubicPath()
	{
	}
	
	public ParametricCubicPath(float weight, float[][] matrix, V ... points)
	{
		this.weight = weight;
		this.matrix = matrix;
		this.points = points;
	}
	
	@Override
	public V set( V subject, float delta )
	{
		return SteerMath.parametricCubicCurve( delta, points, matrix, weight, subject );
	}

}
