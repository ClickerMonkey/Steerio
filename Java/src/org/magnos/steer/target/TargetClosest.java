package org.magnos.steer.target;

import org.magnos.steer.FieldOfView;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.Vector;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;


public class TargetClosest implements Target
{
	public static float DEFAULT_FOV_ALL = SteerMath.PI;
	public static FieldOfView DEFAULT_FOV_TYPE = FieldOfView.IGNORE;
	public static int DEFAULT_FOV_CHECK = 1;
	
	public SpatialDatabase space;
	public float maximum;
	public long groups;
	public int fovCheck;
	public Vector fov;
	public FieldOfView fovType;
	public SpatialEntity chosen;
	public SpatialEntity[] closest;
	public float[] closestDistance;
	
	public TargetClosest(SpatialDatabase space, float maximum, long groups)
	{
		this( space, maximum, groups, DEFAULT_FOV_ALL, DEFAULT_FOV_TYPE, DEFAULT_FOV_CHECK );
	}
	
	public TargetClosest(SpatialDatabase space, float maximum, long groups, float fov, FieldOfView fovType, int fovCheck)
	{
		this.space = space;
		this.maximum = maximum;
		this.groups = groups;
		this.fov = Vector.fromAngle( fov );
		this.fovType = fovType;
		this.fovCheck = fovCheck;
		this.closest = new SpatialEntity[ fovCheck ];
		this.closestDistance = new float[ fovCheck ];
	}
	
	@Override
	public Vector getTarget( SteerSubject subject )
	{
		chosen = null;
		
		int found = space.knn( subject.getPosition(), fovCheck, groups, closest, closestDistance );

		if ( found == 0 )
		{
			return null;
		}
		
		for (int i = 0; i < found; i++)
		{
			SpatialEntity c = closest[i];
			
			if ( SteerMath.isCircleInView( subject.getPosition(), subject.getDirection(), fov, c.getPosition(), c.getRadius(), fovType ) )
			{
				chosen = c;
				
				break;
			}
		}
		
		return ( chosen == null  ? null : chosen.getPosition() );
	}

}
