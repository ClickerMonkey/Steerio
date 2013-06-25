package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.Vector;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;


public class TargetClosest implements Target
{

	public SpatialDatabase space;
	public float maximum;
	public long groups;

	public final SpatialEntity[] closest = {null};
	public final float[] closestDistance = {0.0f};
	
	public TargetClosest(SpatialDatabase space, float maximum, long groups)
	{
		this.space = space;
		this.maximum = maximum;
		this.groups = groups;
	}
	
	@Override
	public Vector getTarget( SteerSubject subject )
	{
		int found = space.knn( subject.getPosition(), 1, groups, closest, closestDistance );

		return ( found == 1 ? closest[0].getPosition() : null );
	}

}
