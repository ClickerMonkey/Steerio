package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;

/**
 * A steering behavior that moves the subject away from the subjects around it.
 * The resulting force is normalized.
 */
public class SteerSeparation extends AbstractSteerSpatial
{
	private final Vector towards = new Vector();
	
	public SteerSeparation(SpatialDatabase space, float query)
	{
		this( space, query, SpatialDatabase.ALL_GROUPS, SpatialDatabase.MAX_RESULTS, true );
	}
	
	public SteerSeparation(SpatialDatabase space, float query, long groups, int max)
	{
		this( space, query, groups, max, true );
	}
	
	public SteerSeparation(SpatialDatabase space, float query, long groups, int max, boolean shared)
	{
		super(space, query, groups, max, shared);
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
	public boolean onFound( SpatialEntity entity, float overlap, int index, Vector queryOffset, float queryRadius, int queryMax, long queryGroups )
	{
		towards.directi( entity.getPosition(), queryOffset );
		towards.normalize();
		
		force.addi( towards );
		
		return true;
	}
	
	@Override
	public Steer clone()
	{
		return new SteerSeparation( space, query, groups, max, shared );
	}
	
}
