package org.magnos.steer.behavior;

import org.magnos.steer.FieldOfView;
import org.magnos.steer.Steer;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;
import org.magnos.steer.Segment;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;

/**
 * A steering behavior that aligns the subject to the average direction of the
 * objects around it. The resulting force is normalized.
 */
public class SteerDodge extends AbstractSteerSpatial
{
	
	public SteerDodge(SpatialDatabase space, float query)
	{
		this( space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, DEFAULT_FOV_ALL, DEFAULT_FOV_TYPE, DEFAULT_SHARED );
	}
	
	public SteerDodge(SpatialDatabase space, float query, long groups, int max)
	{
		this( space, query, groups, max, DEFAULT_FOV_ALL, DEFAULT_FOV_TYPE, DEFAULT_SHARED );
	}
	
	public SteerDodge(SpatialDatabase space, float query, long groups, int max, float fov, FieldOfView fovType)
	{
		this( space, query, groups, max, fov, fovType, DEFAULT_SHARED );
	}
		
	public SteerDodge(SpatialDatabase space, float query, long groups, int max, float fov, FieldOfView fovType, boolean shared)
	{
		super(space, query, groups, max, fov, fovType, shared);
	}
	
	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		force.clear();
		
		int total = search( subject );
		
		if (total > 0) 
		{
			maximize( subject, force );
		}
		
		return force;
	}
	
	@Override
	public Steer clone()
	{
		return new SteerDodge( space, query, groups, max, fov.angle(), fovType, shared );
	}

	@Override
	public boolean onFoundInView( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups)
	{
		boolean applicable = (entity instanceof SteerSubject);
		
		if ( applicable )
		{
			final SteerSubject ss = (SteerSubject)entity;
			final Vector org = subject.getPosition();
			final float rad = subject.getRadius() + ss.getRadius();
			final Vector pos = ss.getPosition();
			final Vector vel = ss.getVelocity();
			
			float intersectionTime = SteerMath.interceptTime( org, subject.getVelocityMax(), pos, vel );
			
			if ( applicable = ( intersectionTime > 0 ) )
			{
				Segment wall = new Segment( pos.x, pos.y, pos.x + vel.x * intersectionTime, pos.y + vel.y * intersectionTime );
				
				Vector closest = wall.closest( org, true, new Vector() );
				
				if ( applicable = ( closest.distanceSq( org ) < rad * rad ) )
				{
					away( subject, closest, closest, this );
					
					force.addi( closest );
				}
			}
		}
		
		return applicable;
	}
	
}
