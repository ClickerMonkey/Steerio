package org.magnos.steer.behavior;

import org.magnos.steer.FieldOfView;
import org.magnos.steer.Steer;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;
import org.magnos.steer.Segment;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialEntityWall;

/**
 * A steering behavior that avoids obstacles in space by using a feeler (query)
 * to determine possible collisions and avoiding them. 
 */
public class SteerAvoidObstacles extends AbstractSteerSpatial
{

	public float lookahead;
	public float buffer;
	
	protected float lookaheadRadius;
	protected float lookaheadVelocity;
	protected final Segment lookaheadWall = new Segment();
	protected final Vector lookaheadPoint = new Vector();
	protected final Vector lookaheadCenter = new Vector();
	protected final Vector lookaheadClosest = new Vector();

	public SteerAvoidObstacles(SpatialDatabase space, float lookahead, float buffer)
	{
		this( space, lookahead, buffer, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, DEFAULT_FOV_ALL, DEFAULT_FOV_TYPE, DEFAULT_SHARED );
	}
	
	public SteerAvoidObstacles(SpatialDatabase space, float lookahead, float buffer, long groups, int max)
	{
		this( space, lookahead, buffer, groups, max, DEFAULT_FOV_ALL, DEFAULT_FOV_TYPE, DEFAULT_SHARED );
	}
	
	public SteerAvoidObstacles(SpatialDatabase space, float lookahead, float buffer, long groups, int max, float fov, FieldOfView fovType)
	{
		this( space, lookahead, buffer, groups, max, fov, fovType, DEFAULT_SHARED );
	}
	
	public SteerAvoidObstacles(SpatialDatabase space, float lookahead, float buffer, long groups, int max, float fov, FieldOfView fovType, boolean shared)
	{
		super(space, 0f, groups, max, fov, fovType, shared);
		
		this.lookahead = lookahead;
		this.buffer = buffer;
	}
	
	@Override
	protected int search( SteerSubject ss )
	{
		final Vector p = ss.getPosition();
		final Vector v = ss.getVelocity();
		final Vector d = ss.getDirection();
		final float r = ss.getRadius();
		
		subject = ss;
		
		lookaheadPoint.set( p );
		lookaheadPoint.addsi( d, lookahead );
		lookaheadCenter.interpolate( p, lookaheadPoint, 0.5f );
		lookaheadVelocity = v.length();
		lookaheadRadius = ( lookahead * 0.5f ) + r;
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
		if ( entity instanceof SteerSubject )
		{
			return handleSteerSubject( (SteerSubject)entity );
		}
		
		if ( entity instanceof SpatialEntityWall )
		{
			return handleWall( (SpatialEntityWall)entity );
		}

		return steerAway( entity.getPosition(), entity.getRadius() );
	}
	
	protected boolean handleSteerSubject( SteerSubject entity )
	{
		// If it's a steer subject, use the intercept position as the point to stay away from.
		Vector p = entity.getPosition();
		Vector v = entity.getVelocity();
		float intersectionTime = SteerMath.interceptTime( subject.getPosition(), lookaheadVelocity, p, v );
	
		if (intersectionTime > 0)
		{
			p = p.adds( v, intersectionTime );
		}
		
		return steerAway( p, entity.getRadius() );
	}
	
	protected boolean handleWall( SpatialEntityWall wall )
	{
		float r = subject.getRadius() + buffer;
		float distSq = wall.distanceSq( lookaheadWall, wall.getPosition(), null );
		
		boolean applicable = ( distSq != -1 && distSq < r * r );
		
		if ( applicable )
		{
			lookaheadClosest.set( wall.a, wall.b );
			maximize( subject, lookaheadClosest );
			
			force.addi( lookaheadClosest );
		}
		
		return applicable;
	}
	
	protected boolean steerAway( Vector from, float fromRadius )
	{
		float r = subject.getRadius() + fromRadius + buffer;
		
		lookaheadWall.closest( from, true, lookaheadClosest );

		boolean applicable = ( lookaheadClosest.distanceSq( from ) <= r * r );
		
		if ( applicable )
		{
			lookaheadClosest.subi( from );
			maximize( subject, lookaheadClosest );
			
			force.addi( lookaheadClosest );
		}
		
		return applicable;
	}
	
	@Override
	public Steer clone()
	{
		return new SteerAvoidObstacles( space, lookahead, buffer, groups, max, fov.angle(), fovType, shared );
	}

}
