
package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Filter;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


public class SteerMatchVelocity<V extends Vec<V>> extends AbstractSteerSpatial<V, SteerMatchVelocity<V>>
{

    protected V force;
    protected float forceMagnitude;

    public SteerMatchVelocity( float minimum, float maximum, SpatialDatabase<V> space, float query )
    {
        this( minimum, maximum, space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED );
    }

    public SteerMatchVelocity( float magnitude, SpatialDatabase<V> space, float query )
    {
        this( magnitude, magnitude, space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED );
    }

    public SteerMatchVelocity( float minimum, float maximum, SpatialDatabase<V> space, float query, long groups, int max )
    {
        this( minimum, maximum, space, query, groups, max, null, DEFAULT_SHARED );
    }

    public SteerMatchVelocity( float magnitude, SpatialDatabase<V> space, float query, long groups, int max )
    {
        this( magnitude, magnitude, space, query, groups, max, null, DEFAULT_SHARED );
    }

    public SteerMatchVelocity( float minimum, float maximum, SpatialDatabase<V> space, float query, long groups, int max, Filter<V, SpatialEntity<V>> filter )
    {
        this( minimum, maximum, space, query, groups, max, filter, DEFAULT_SHARED );
    }

    public SteerMatchVelocity( float magnitude, SpatialDatabase<V> space, float query, long groups, int max, Filter<V, SpatialEntity<V>> filter )
    {
        this( magnitude, magnitude, space, query, groups, max, filter, DEFAULT_SHARED );
    }

    public SteerMatchVelocity( float magnitude, SpatialDatabase<V> space, float query, long groups, int max, Filter<V, SpatialEntity<V>> filter, boolean shared )
    {
        this( magnitude, magnitude, space, query, groups, max, filter, shared );
    }

    public SteerMatchVelocity( float minimum, float maximum, SpatialDatabase<V> space, float query, long groups, int max, Filter<V, SpatialEntity<V>> filter, boolean shared )
    {
        super( minimum, maximum, space, query, query, groups, max, filter, shared );
    }

    public SteerMatchVelocity( float minimum, float maximum, SpatialDatabase<V> space, float minimumRadius, float maximumRadius, long groups, int max, Filter<V, SpatialEntity<V>> filter, boolean shared )
    {
        super( minimum, maximum, space, minimumRadius, maximumRadius, groups, max, filter, shared );
    }

    @Override
    public float getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        force = out;
        forceMagnitude = 0;

        search( subject );

        if ( forceMagnitude > 0 )
        {
            force.divi( forceMagnitude );

            return forceFromVector( this, force );
        }
        
        return Steer.NONE;
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerMatchVelocity<V>( minimum, maximum, space, minimumRadius, maximumRadius, groups, max, filter, shared );
    }

    @Override
    public boolean onFoundInView( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups, float delta )
    {
        boolean applicable = (entity instanceof SteerSubject);

        if ( applicable )
        {
            SteerSubject<V> ss = (SteerSubject<V>)entity;

            force.addi( ss.getVelocity() );
            forceMagnitude += delta;
        }

        return applicable;
    }

}
