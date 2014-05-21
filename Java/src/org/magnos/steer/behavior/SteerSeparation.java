package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.SteerSubjectFilter;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;

/**
 * A steering behavior that moves the subject away from the subjects around it.
 * The resulting force is normalized.
 */
public class SteerSeparation<V extends Vec<V>> extends AbstractSteerSpatial<V>
{

    protected V towards;
    protected V force;
	
	public SteerSeparation(SpatialDatabase<V> space, float query, V template)
	{
		this( space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED, template );
	}
	
	public SteerSeparation(SpatialDatabase<V> space, float query, long groups, int max, V template)
	{
		this( space, query, groups, max, null, DEFAULT_SHARED, template );
	}
	
	public SteerSeparation(SpatialDatabase<V> space, float query, long groups, int max, SteerSubjectFilter<V, SpatialEntity<V>> filter, V template)
	{
		this( space, query, groups, max, filter, DEFAULT_SHARED, template );
	}
		
	public SteerSeparation(SpatialDatabase<V> space, float query, long groups, int max, SteerSubjectFilter<V, SpatialEntity<V>> filter, boolean shared, V template)
	{
		super(space, query, groups, max, filter, shared);

        this.towards = template.create();
        this.force = template.create();
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
	public boolean onFoundInView( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
		towards.directi( entity.getPosition(), queryOffset );
		towards.normalize();
		
		force.addi( towards );
		
		return true;
	}
	
	@Override
	public Steer<V> clone()
	{
		return new SteerSeparation<V>( space, query, groups, max, filter, shared, force );
	}
	
}
