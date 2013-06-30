
package org.magnos.steer.behavior;

import org.magnos.steer.FieldOfView;
import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;

public class SteerMatchVelocity extends AbstractSteerSpatial
{

	public SteerMatchVelocity( SpatialDatabase space, float query )
	{
		this( space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, DEFAULT_FOV_ALL, DEFAULT_FOV_TYPE, DEFAULT_SHARED );
	}

	public SteerMatchVelocity( SpatialDatabase space, float query, long groups, int max )
	{
		this( space, query, groups, max, DEFAULT_FOV_ALL, DEFAULT_FOV_TYPE, DEFAULT_SHARED );
	}

	public SteerMatchVelocity( SpatialDatabase space, float query, long groups, int max, float fov, FieldOfView fovType )
	{
		this( space, query, groups, max, fov, fovType, DEFAULT_SHARED );
	}

	public SteerMatchVelocity( SpatialDatabase space, float query, long groups, int max, float fov, FieldOfView fovType, boolean shared )
	{
		super( space, query, groups, max, fov, fovType, shared );
	}

	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		force.clear();

		int total = search( subject );

		if (total > 0)
		{
			force.divi( total );

			maximize( subject, force );
		}

		return force;
	}

	@Override
	public Steer clone()
	{
		return new SteerMatchVelocity( space, query, groups, max, fov.angle(), fovType, shared );
	}

	@Override
	public boolean onFoundInView( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
		boolean applicable = (entity instanceof SteerSubject);

		if (applicable)
		{
			SteerSubject ss = (SteerSubject)entity;

			force.addi( ss.getVelocity() );
		}

		return applicable;
	}

}
