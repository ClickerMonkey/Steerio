
package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Filter;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


/**
 * A steering behavior that moves the subject to the average position of the
 * objects around it. The resulting force is normalized.
 */
public class SteerCohesion<V extends Vec<V>> extends AbstractSteerSpatial<V, SteerCohesion<V>>
{

    private final V center;
    private float centerMagnitude;

    public SteerCohesion( float minimum, float maximum, SpatialDatabase<V> space, float query, V template )
    {
        this( minimum, maximum, space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED, template );
    }

    public SteerCohesion( float magnitude, SpatialDatabase<V> space, float query, V template )
    {
        this( magnitude, magnitude, space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED, template );
    }

    public SteerCohesion( float minimum, float maximum, SpatialDatabase<V> space, float query, long groups, int max, V template )
    {
        this( minimum, maximum, space, query, groups, max, null, DEFAULT_SHARED, template );
    }

    public SteerCohesion( float magnitude, SpatialDatabase<V> space, float query, long groups, int max, V template )
    {
        this( magnitude, magnitude, space, query, groups, max, null, DEFAULT_SHARED, template );
    }

    public SteerCohesion( float minimum, float maximum, SpatialDatabase<V> space, float query, long groups, int max, Filter<V, SpatialEntity<V>> filter, V template )
    {
        this( minimum, maximum, space, query, groups, max, filter, DEFAULT_SHARED, template );
    }

    public SteerCohesion( float magnitude, SpatialDatabase<V> space, float query, long groups, int max, Filter<V, SpatialEntity<V>> filter, V template )
    {
        this( magnitude, magnitude, space, query, groups, max, filter, DEFAULT_SHARED, template );
    }

    public SteerCohesion( float magnitude, SpatialDatabase<V> space, float query, long groups, int max, Filter<V, SpatialEntity<V>> filter, boolean shared, V template )
    {
        this( magnitude, magnitude, space, query, groups, max, filter, shared, template );
    }

    public SteerCohesion( float minimum, float maximum, SpatialDatabase<V> space, float query, long groups, int max, Filter<V, SpatialEntity<V>> filter, boolean shared, V template )
    {   
        this( minimum, maximum, space, query, query, groups, max, filter, shared, template );
    }

    public SteerCohesion( float minimum, float maximum, SpatialDatabase<V> space, float minimumRadius, float maximumRadius, long groups, int max, Filter<V, SpatialEntity<V>> filter, boolean shared, V template )
    {
        super( minimum, maximum, space, minimumRadius, maximumRadius, groups, max, filter, shared );

        this.center = template.create();
    }

    @Override
    public float getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        center.clear();
        centerMagnitude = 0;

        search( subject );

        if ( centerMagnitude > 0 )
        {
            center.divi( centerMagnitude );

            return towards( subject, center, out, this );
        }
        
        return Steer.NONE;
    }

    @Override
    public boolean onFoundInView( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups, float delta )
    {
        center.addsi( entity.getPosition(), delta );
        centerMagnitude += delta;

        return true;
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerCohesion<V>( minimum, maximum, space, minimumRadius, maximumRadius, groups, max, filter, shared, center );
    }

}
