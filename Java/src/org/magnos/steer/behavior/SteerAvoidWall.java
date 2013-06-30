package org.magnos.steer.behavior;

import org.magnos.steer.FieldOfView;
import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialEntityWall;


public class SteerAvoidWall extends AbstractSteerSpatial
{

	public final Vector force = new Vector();
	public final Vector normal = new Vector();
	
	public SteerAvoidWall( SpatialDatabase space, float query, long groups, int max )
	{
		super( space, query, groups, max, DEFAULT_FOV_ALL, FieldOfView.IGNORE, true );
	}
	
	public SteerAvoidWall( SpatialDatabase space, float query, long groups, int max, float fov, FieldOfView fovType )
	{
		super( space, query, groups, max, fov, fovType, true );
	}

	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		force.clear();
		
		if ( search( subject ) > 0 )
		{
			maximize( subject, force );	
		}
		
		return force;
	}

	@Override
	public boolean onFoundInView( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
		boolean applicable = (entity instanceof SpatialEntityWall);
		
		if (applicable)
		{
			SpatialEntityWall wall = (SpatialEntityWall)entity;
			
			float signedDistance = wall.distance( queryOffset ); 
			
			if (Math.abs( signedDistance ) <= queryRadius)
			{
				wall.normal( queryOffset, true, normal );
				
				force.addi( normal );	
			}
		}
		
		return applicable;
	}

	@Override
	public Steer clone()
	{
		return new SteerAvoidWall( space, query, groups, max, fov.angle(), fovType );
	}

}
