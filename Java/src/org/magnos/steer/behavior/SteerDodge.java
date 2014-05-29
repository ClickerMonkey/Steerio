package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Filter;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;

/**
 * A steering behavior that aligns the subject to the average direction of the
 * objects around it. The resulting force is normalized.
 */
public class SteerDodge<V extends Vec<V>> extends AbstractSteerSpatial<V>
{
    
    protected V force;
	
	public SteerDodge(SpatialDatabase<V> space, float query)
	{
		this( space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED );
	}
	
	public SteerDodge(SpatialDatabase<V> space, float query, long groups, int max)
	{
		this( space, query, groups, max, null, DEFAULT_SHARED );
	}
	
	public SteerDodge(SpatialDatabase<V> space, float query, long groups, int max, Filter<V, SpatialEntity<V>> filter)
	{
		this( space, query, groups, max, filter, DEFAULT_SHARED );
	}
		
	public SteerDodge(SpatialDatabase<V> space, float query, long groups, int max, Filter<V, SpatialEntity<V>> filter, boolean shared)
	{
		super(space, query, groups, max, filter, shared);
	}
	
	@Override
	public void getForce( float elapsed, SteerSubject<V> subject, V out )
	{
	    force = out;
		
		int total = search( subject );
		
		if (total > 0) 
		{
			maximize( subject, force );
		}
	}
	
	@Override
	public Steer<V> clone()
	{
		return new SteerDodge<V>( space, query, groups, max, filter, shared );
	}

	@Override
	public boolean onFoundInView( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups)
	{
		boolean applicable = (entity instanceof SteerSubject);
		
		if ( applicable )
		{
			final SteerSubject<V> ss = (SteerSubject<V>)entity;
			final V org = subject.getPosition();
			final float rad = subject.getRadius() + ss.getRadius();
			final V pos = ss.getPosition();
			final V vel = ss.getVelocity();
			
			float intersectionTime = SteerMath.interceptTime( org, subject.getVelocityMax(), pos, vel );
			
			if ( applicable = ( intersectionTime > 0 ) )
			{
			    V future = pos.adds( vel, intersectionTime );
			    V closest = SteerMath.closest( pos, future, org, pos.create() );
			    
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
