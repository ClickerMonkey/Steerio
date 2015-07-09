package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Filter;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;

/**
 * A steering behavior that moves the subject away from the subjects around it.
 * The resulting force is normalized.
 */
public class SteerSeparation<V extends Vec<V>> extends AbstractSteerSpatial<V, SteerSeparation<V>>
{

    protected V towards;
    protected V force;
    
    public SteerSeparation(float minimum, float maximum, SpatialDatabase<V> space, float query, V template)
    {
        this( minimum, maximum, space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED, template );
    }
    
    public SteerSeparation(float minimum, float maximum, SpatialDatabase<V> space, float minimumRadius, float maximumRadius, V template)
    {
        this( minimum, maximum, space, minimumRadius, maximumRadius, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED, template );
    }
    
    public SteerSeparation(float magnitude, SpatialDatabase<V> space, float query, V template)
    {
        this( magnitude, magnitude, space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED, template );
    }
    
    public SteerSeparation(float minimum, float maximum, SpatialDatabase<V> space, float query, long groups, int max, V template)
    {
        this( minimum, maximum, space, query, groups, max, null, DEFAULT_SHARED, template );
    }
    
    public SteerSeparation(float magnitude, SpatialDatabase<V> space, float query, long groups, int max, V template)
    {
        this( magnitude, magnitude, space, query, groups, max, null, DEFAULT_SHARED, template );
    }
    
    public SteerSeparation(float minimum, float maximum, SpatialDatabase<V> space, float query, long groups, int max, Filter<V> filter, V template)
    {
        this( minimum, maximum, space, query, groups, max, filter, DEFAULT_SHARED, template );
    }
    
    public SteerSeparation(float magnitude, SpatialDatabase<V> space, float query, long groups, int max, Filter<V> filter, V template)
    {
        this( magnitude, magnitude, space, query, groups, max, filter, DEFAULT_SHARED, template );
    }
    
    public SteerSeparation(float magnitude, SpatialDatabase<V> space, float query, long groups, int max, Filter<V> filter, boolean shared, V template)
    {
        this( magnitude, magnitude, space, query, groups, max, filter, shared, template );
    }
    
    public SteerSeparation(float minimum, float maximum, SpatialDatabase<V> space, float query, long groups, int max, Filter<V> filter, boolean shared, V template)
    {
        this( minimum, maximum, space, query, query, groups, max, filter, shared, template );
    }
    
    public SteerSeparation(float minimum, float maximum, SpatialDatabase<V> space, float minimumRadius, float maximumRadius, long groups, int max, Filter<V> filter, boolean shared, V template)
    {
        super( minimum, maximum, space, minimumRadius, maximumRadius, groups, max, filter, shared );

        this.towards = template.create();
        this.force = template.create();
    }
	
	@Override
	public float getForce( float elapsed, SteerSubject<V> subject, V out )
	{
	    force = out;
	    
		int total = search( subject );
		
		if (total > 0)
		{
		    return forceFromVector( this, force );
		}
		
		return Steer.NONE;
	}

	@Override
	public boolean onFoundInView( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups, float delta )
	{
		towards.directi( entity.getPosition(), queryOffset );
		towards.normalize();
		
		force.addsi( towards, delta );
		
		return true;
	}
	
	@Override
	public Steer<V> clone()
	{
		return new SteerSeparation<V>( minimum, maximum, space, minimumRadius, maximumRadius, groups, max, filter, shared, force );
	}
	
}
