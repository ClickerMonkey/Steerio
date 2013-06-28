package org.magnos.steer.spatial.prunesweep;

import org.magnos.steer.spatial.SpatialEntity;

public class SpatialPruneSweepNode
{

	public final SpatialEntity entity;
	public float min, max;
	public int spanMin, spanMax;
	
	public SpatialPruneSweepNode(SpatialEntity entity)
	{
		this.entity = entity;
	}
	
}
