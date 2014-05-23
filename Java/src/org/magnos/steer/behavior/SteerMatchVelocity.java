
package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Filter;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


public class SteerMatchVelocity<V extends Vec<V>> extends AbstractSteerSpatial<V>
{

    protected V force;

    public SteerMatchVelocity( SpatialDatabase<V> space, float query )
    {
        this( space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED );
    }

    public SteerMatchVelocity( SpatialDatabase<V> space, float query, long groups, int max )
    {
        this( space, query, groups, max, null, DEFAULT_SHARED );
    }

    public SteerMatchVelocity( SpatialDatabase<V> space, float query, long groups, int max, Filter<V, SpatialEntity<V>> filter )
    {
        this( space, query, groups, max, filter, DEFAULT_SHARED );
    }

    public SteerMatchVelocity( SpatialDatabase<V> space, float query, long groups, int max, Filter<V, SpatialEntity<V>> filter, boolean shared )
    {
        super( space, query, groups, max, filter, shared );
    }

    @Override
    public void getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        force = out;

        int total = search( subject );

        if ( total > 0 )
        {
            force.divi( total );

            maximize( subject, force );
        }
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerMatchVelocity<V>( space, query, groups, max, filter, shared );
    }

    @Override
    public boolean onFoundInView( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups )
    {
        boolean applicable = (entity instanceof SteerSubject);

        if ( applicable )
        {
            SteerSubject<V> ss = (SteerSubject<V>)entity;

            force.addi( ss.getVelocity() );
        }

        return applicable;
    }

}
