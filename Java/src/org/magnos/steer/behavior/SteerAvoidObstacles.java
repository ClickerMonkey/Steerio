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
 * A steering behavior that avoids obstacles in space by using a feeler (query)
 * to determine possible collisions and avoiding them. 
 */
public class SteerAvoidObstacles extends AbstractSteerSpatial
{

	public float lookaheadTime;
	
	protected float lookaheadRadius;
	protected float lookaheadVelocity;
	protected final Segment lookaheadWall = new Segment();
	protected final Vector lookaheadPoint = new Vector();
	protected final Vector lookaheadCenter = new Vector();
	protected final Vector lookaheadClosest = new Vector();

	public SteerAvoidObstacles(SpatialDatabase space, float lookaheadTime)
	{
		this( space, lookaheadTime, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, DEFAULT_FOV_ALL, DEFAULT_FOV_TYPE, DEFAULT_SHARED );
	}
	
	public SteerAvoidObstacles(SpatialDatabase space, float lookaheadTime, long groups, int max)
	{
		this( space, lookaheadTime, groups, max, DEFAULT_FOV_ALL, DEFAULT_FOV_TYPE, DEFAULT_SHARED );
	}
	
	public SteerAvoidObstacles(SpatialDatabase space, float lookaheadTime, long groups, int max, float fov, FieldOfView fovType)
	{
		this( space, lookaheadTime, groups, max, fov, fovType, DEFAULT_SHARED );
	}
	
	public SteerAvoidObstacles(SpatialDatabase space, float lookaheadTime, long groups, int max, float fov, FieldOfView fovType, boolean shared)
	{
		super(space, 0f, groups, max, fov, fovType, shared);
		
		this.lookaheadTime = lookaheadTime;
	}
	
	@Override
	protected int search( SteerSubject ss )
	{
		final Vector p = ss.getPosition();
		final Vector v = ss.getVelocity();
		final float r = ss.getRadius();
		
		subject = ss;
		
		lookaheadPoint.set( p );
		lookaheadPoint.addsi( v, lookaheadTime );
		lookaheadCenter.interpolate( p, lookaheadPoint, 0.5f );
		lookaheadVelocity = v.length();
		lookaheadRadius = ( lookaheadVelocity * lookaheadTime * 0.5f ) + r;
		lookaheadWall.set( p.x, p.y, lookaheadPoint.x, lookaheadPoint.y );
		
		return space.intersects( lookaheadCenter, lookaheadRadius, max, groups, this );
	}
	
	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		force.clear();

		int found = search( subject );
		
		if ( found > 0 )
		{
			maximize( subject, force );
		}
	
		return force;
	}

	@Override
	public boolean onFoundInView( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
		final float r = subject.getRadius() + entity.getRadius();
		final Vector p = new Vector( entity.getPosition() );
		
		// If it's a steer subject, use the intercept position as the point to stay away from.
		if ( entity instanceof SteerSubject )
		{
			final SteerSubject ss = (SteerSubject)entity;
			final Vector v = ss.getVelocity();
			final float intersectionTime = SteerMath.interceptTime( subject.getPosition(), lookaheadVelocity, p, v );
			
			// If an intersection may occur, use the time!
			if (intersectionTime > 0)
			{
				p.addsi( v, intersectionTime );
			}	
		}
		
		// If it's a wall, avoid it!
		if ( entity instanceof Segment )
		{
			Segment ss = (Segment)entity;
			
			if ( !ss.intersection( lookaheadWall, true, p ) )
			{
				return false;
			}
		}
		
		lookaheadWall.closest( p, true, lookaheadClosest );

		boolean applicable = ( lookaheadClosest.distanceSq( p ) <= r * r );
		
		if ( applicable )
		{
			lookaheadClosest.subi( p );
			maximize( subject, lookaheadClosest );
			
			force.addi( lookaheadClosest );
		}
		
		return applicable;
	}
	
	@Override
	public Steer clone()
	{
		return new SteerAvoidObstacles( space, lookaheadTime, groups, max, fov.angle(), fovType, shared );
	}

}
