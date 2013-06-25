package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;

/**
 * A steering behavior that avoids obstacles in space by using a feeler (query)
 * to determine possible collisions and avoiding them. 
 */
public class SteerAvoidObstacles extends AbstractSteer
{

	public SpatialDatabase space;
	public float query;
	public long groups;
	public boolean shared;
	
	private final SpatialEntity[] closest = {null};
	private final float[] closestDistance = {0.0f};
	
	public SteerAvoidObstacles(SpatialDatabase space, float query, long groups, boolean shared)
	{
		this.space = space;
		this.query = query;
		this.groups = groups;
		this.shared = shared;
	}
	
	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		force.clear();

		int found = space.knn( subject.getPosition(), 1, groups, closest, closestDistance );

		if ( found == 1 && closestDistance[0] < query )
		{
			float intersection = query - closestDistance[0];
			
			force.directi( closest[0].getPosition(), subject.getPosition() );
			force.length( intersection );
		}
		
		return force;
	}

	@Override
	public boolean isShared()
	{
		return shared;
	}

	@Override
	public Steer clone()
	{
		return new SteerAvoidObstacles( space, query, groups, shared );
	}

}
