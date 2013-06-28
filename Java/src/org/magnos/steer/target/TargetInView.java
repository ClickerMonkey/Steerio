package org.magnos.steer.target;

import org.magnos.steer.FieldOfView;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.Vector;


public class TargetInView implements Target
{

	public Target target;
	public float targetRadius;
	public Vector fov;
	public FieldOfView fovType;
	
	public TargetInView( Target target, float targetRadius, float fov, FieldOfView fovType )
	{
		this.target = target;
		this.targetRadius = targetRadius;
		this.fov = Vector.fromAngle( fov );
		this.fovType = fovType;
	}
	
	@Override
	public Vector getTarget( SteerSubject subject )
	{
		Vector position = target.getTarget( subject );
		
		if ( SteerMath.isCircleInView( subject.getPosition(), subject.getDirection(), fov, position, targetRadius, fovType ) )
		{
			return position;
		}
		
		return null;
	}

}
