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
public class SteerDodge<V extends Vec<V>> extends AbstractSteerSpatial<V, SteerDodge<V>>
{
    
    protected V force;
    protected float forceMagnitude;
    
    public SteerDodge(float minimum, float maximum, SpatialDatabase<V> space, float query)
    {
        this( minimum, maximum, space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED );
    }
    
    public SteerDodge(float magnitude, SpatialDatabase<V> space, float query)
    {
        this( magnitude, magnitude, space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED );
    }
    
    public SteerDodge(float minimum, float maximum, SpatialDatabase<V> space, float query, long groups, int max)
    {
        this( minimum, maximum, space, query, groups, max, null, DEFAULT_SHARED );
    }
    
    public SteerDodge(float magnitude, SpatialDatabase<V> space, float query, long groups, int max)
    {
        this( magnitude, magnitude, space, query, groups, max, null, DEFAULT_SHARED );
    }
    
    public SteerDodge(float minimum, float maximum, SpatialDatabase<V> space, float query, long groups, int max, Filter<V> filter)
    {
        this( minimum, maximum, space, query, groups, max, filter, DEFAULT_SHARED );
    }
    
    public SteerDodge(float magnitude, SpatialDatabase<V> space, float query, long groups, int max, Filter<V> filter)
    {
        this( magnitude, magnitude, space, query, groups, max, filter, DEFAULT_SHARED );
    }
    
    public SteerDodge(float magnitude, SpatialDatabase<V> space, float query, long groups, int max, Filter<V> filter, boolean shared)
    {
        this( magnitude, magnitude, space, query, groups, max, filter, shared );
    }
    
    public SteerDodge(float minimum, float maximum, SpatialDatabase<V> space, float query, long groups, int max, Filter<V> filter, boolean shared)
    {
        super( minimum, maximum, space, query, query, groups, max, filter, shared);
    }
    
    public SteerDodge(float minimum, float maximum, SpatialDatabase<V> space, float minimumRadius, float maximumRadius, long groups, int max, Filter<V> filter, boolean shared)
    {
        super( minimum, maximum, space, minimumRadius, maximumRadius, groups, max, filter, shared);
    }
	
	@Override
	public float getForce( float elapsed, SteerSubject<V> subject, V out )
	{
	    forceMagnitude = Steer.NONE;
	    force = out;
		
		int total = search( subject );
		
		if (total > 0) 
		{
		    force.normalize();
		    
		    return forceMagnitude;
		}
		
		return Steer.NONE;
	}
	
	@Override
	public Steer<V> clone()
	{
		return new SteerDodge<V>( minimum, maximum, space, minimumRadius, maximumRadius, groups, max, filter, shared );
	}

	@Override
	public boolean onFoundInView( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups, float delta)
	{
		boolean applicable = (entity instanceof SteerSubject);
		
		if ( applicable )
		{
			final SteerSubject<V> ss = (SteerSubject<V>)entity;
			final V org = subject.getPosition();
			final float rad = subject.getRadius() + ss.getRadius();
			final V pos = ss.getPosition();
			final V vel = ss.getVelocity();
			
			float intersectionTime = SteerMath.interceptTime( org, subject.getMaximumVelocity(), pos, vel );
			
			if ( applicable = ( intersectionTime > 0 ) )
			{
			    V future = pos.adds( vel, intersectionTime );
			    V closest = SteerMath.closest( pos, future, org, pos.create() );
			    
				if ( applicable = ( closest.distanceSq( org ) < rad * rad ) )
				{
				    float closestMagnitude = away( subject, closest, closest, this );
				    
				    forceMagnitude += closestMagnitude * delta;
					force.addsi( closest, delta );
				}
			}
		}
		
		return applicable;
	}
	
}
