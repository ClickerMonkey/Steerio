
package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.SteerSubjectFilter;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


/**
 * A steering behavior that moves the subject to the average position of the
 * objects around it. The resulting force is normalized.
 */
public class SteerCohesion<V extends Vec<V>> extends AbstractSteerSpatial<V>
{

    private final V center;

    public SteerCohesion( SpatialDatabase<V> space, float query, V template )
    {
        this( space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED, template );
    }

    public SteerCohesion( SpatialDatabase<V> space, float query, long groups, int max, V template )
    {
        this( space, query, groups, max, null, DEFAULT_SHARED, template );
    }

    public SteerCohesion( SpatialDatabase<V> space, float query, long groups, int max, SteerSubjectFilter<V, SpatialEntity<V>> filter, V template )
    {
        this( space, query, groups, max, filter, DEFAULT_SHARED, template );
    }

    public SteerCohesion( SpatialDatabase<V> space, float query, long groups, int max, SteerSubjectFilter<V, SpatialEntity<V>> filter, boolean shared, V template )
    {
        super( space, query, groups, max, filter, shared );

        this.center = template.create();
    }

    @Override
    public void getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        center.clear();

        int total = search( subject );

        if ( total > 0 )
        {
            center.divi( total );

            towards( subject, center, out, this );
        }
    }

    @Override
    public boolean onFoundInView( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups )
    {
        center.addi( entity.getPosition() );

        return true;
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerCohesion<V>( space, query, groups, max, filter, shared, center );
    }

}
