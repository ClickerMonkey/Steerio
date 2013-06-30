package org.magnos.steer.behavior;

import org.magnos.steer.FieldOfView;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;

/**
 * Abstract steering behavior that cares about other Steerables around it.
 */
public abstract class AbstractSteerSpatial extends AbstractSteer implements SearchCallback
{
	
	public static float DEFAULT_FOV_ALL = SteerMath.PI;
	public static FieldOfView DEFAULT_FOV_TYPE = FieldOfView.IGNORE;
	public static int DEFAULT_MAX_RESULTS = 16;
	public static boolean DEFAULT_SHARED = true;
	
	public boolean shared;
	public long groups;
	public float query;
	public int max;
	public SpatialDatabase space;
	public SteerSubject subject;
	public Vector fov;
	public FieldOfView fovType;
	
	public AbstractSteerSpatial(SpatialDatabase space, float query, long groups, int max, float fov, FieldOfView fovType, boolean shared)
	{
		this.space = space;
		this.query = query;
		this.groups = groups;
		this.max = max;
		this.shared = shared;
		this.fov = Vector.fromAngle( fov );
		this.fovType = fovType;
	}
	
	protected abstract boolean onFoundInView( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups );
	
	protected int search( SteerSubject ss )
	{
		subject = ss;
		
		return space.intersects( ss.getPosition(), query, max, groups, this );
	}
	
	@Override
	public boolean isShared()
	{
		return shared;
	}
	
	@Override
	public final boolean onFound( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
		boolean inView = SteerMath.isCircleInView( subject.getPosition(), subject.getDirection(), fov, entity.getPosition(), entity.getRadius(), fovType );
		
		if (inView)
		{
			inView = onFoundInView( entity, overlap, index, queryOffset, queryRadius, queryMax, queryGroups );
		}
		
		return inView;
	}
	
}
