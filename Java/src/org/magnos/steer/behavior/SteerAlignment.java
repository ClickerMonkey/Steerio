
package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Filter;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


/**
 * A steering behavior that aligns the subject to the average direction of the objects around it.
 */
public class SteerAlignment<V extends Vec<V>> extends AbstractSteerSpatial<V>
{
    
    protected V force;

    /**
     * Instantiates a new SteerAlignment for all groups, maximum field of view, and up to {@link AbstractSteerSpatial#DEFAULT_MAX_RESULTS} affecting
     * entities.
     * 
     * @param space
     *        The spatial database containing the other {@link SteerSubject}s affecting this steering behavior.
     * @param query
     *        The radius of the query circle around the {@link SteerSubject}.
     */
    public SteerAlignment( SpatialDatabase<V> space, float query )
    {
        this( space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED );
    }

    /**
     * Instantiates a new SteerAlignment for maximum field of view.
     * 
     * @param space
     *        The spatial database containing the other {@link SteerSubject}s affecting this steering behavior.
     * @param query
     *        The radius of the query circle around the {@link SteerSubject}.
     * @param groups
     *        The groups the {@link SteerSubject}s must be in to be visible to this steering behavior.
     * @param max
     *        The maximum number of {@link SteerSubject}s that can affect this steering behavior.
     */
    public SteerAlignment( SpatialDatabase<V> space, float query, long groups, int max )
    {
        this( space, query, groups, max, null, DEFAULT_SHARED );
    }

    /**
     * Instantiates a new SteerAlignment.
     * 
     * @param space
     *        The spatial database containing the other {@link SteerSubject}s affecting this steering behavior.
     * @param query
     *        The radius of the query circle around the {@link SteerSubject}.
     * @param groups
     *        The groups the {@link SteerSubject}s must be in to be visible to this steering behavior.
     * @param max
     *        The maximum number of {@link SteerSubject}s that can affect this steering behavior.
     * @param fov
     *        The field of view in radians that this steering behavior has. The maximum value this can be is PI, since it really represents half of
     *        the field of view.
     * @param fovType
     *        A flag used to determine whether an object is in the field of view of a subject.
     */
    public SteerAlignment( SpatialDatabase<V> space, float query, long groups, int max, Filter<V, SpatialEntity<V>> filter )
    {
        this( space, query, groups, max, filter, DEFAULT_SHARED );
    }

    /**
     * Instantiates a new SteerAlignment.
     * 
     * @param space
     *        The spatial database containing the other {@link SteerSubject}s affecting this steering behavior.
     * @param query
     *        The radius of the query circle around the {@link SteerSubject}.
     * @param groups
     *        The groups the {@link SteerSubject}s must be in to be visible to this steering behavior.
     * @param max
     *        The maximum number of {@link SteerSubject}s that can affect this steering behavior.
     * @param fov
     *        The field of view in radians that this steering behavior has. The maximum value this can be is PI, since it really represents half of
     *        the field of view.
     * @param fovType
     *        A flag used to determine whether an object is in the field of view of a subject.
     * @param shared
     *        Whether this {@link Steer} implementation can be shared between {@link SteerSubject}s.
     */
    public SteerAlignment( SpatialDatabase<V> space, float query, long groups, int max, Filter<V, SpatialEntity<V>> filter, boolean shared )
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
            maximize( subject, force );
        }
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerAlignment<V>( space, query, groups, max, filter, shared );
    }

    @Override
    public boolean onFoundInView( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups )
    {
        // Only steer subjects are applicable (because only they have direction).
        boolean applicable = (entity instanceof SteerSubject);

        if ( applicable )
        {
            SteerSubject<V> ss = (SteerSubject<V>)entity;

            force.addi( ss.getDirection() );
        }

        return applicable;
    }

}
