package org.magnos.steer.behavior;

import org.magnos.steer.FieldOfView;
import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;

/**
 * A steering behavior that moves the subject to the average position of the
 * objects around it. The resulting force is normalized.
 */
public class SteerCohesion extends AbstractSteerSpatial
{
	
	private final Vector center = new Vector();
	
	public SteerCohesion(SpatialDatabase space, float query)
	{
		this( space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, DEFAULT_FOV_ALL, DEFAULT_FOV_TYPE, DEFAULT_SHARED );
	}
	
	public SteerCohesion(SpatialDatabase space, float query, long groups, int max)
	{
		this( space, query, groups, max, DEFAULT_FOV_ALL, DEFAULT_FOV_TYPE, DEFAULT_SHARED );
	}
	
	public SteerCohesion(SpatialDatabase space, float query, long groups, int max, float fov, FieldOfView fovType)
	{
		this( space, query, groups, max, fov, fovType, DEFAULT_SHARED );
	}
		
	public SteerCohesion(SpatialDatabase space, float query, long groups, int max, float fov, FieldOfView fovType, boolean shared)
	{
		super(space, query, groups, max, fov, fovType, shared);
	}
	
	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		force.clear();
		center.clear();
		
		int total = search( subject );
		
		if (total > 0)
		{
			center.divi( total );
			
			towards( subject, center, force );
		}
		
		return force;
	}

	@Override
	public void onFoundInView( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
		center.addi( entity.getPosition() );
	}
	
	@Override
	public Steer clone()
	{
		return new SteerCohesion( space, query, groups, max, fov.angle(), fovType, shared );
	}
	
}
