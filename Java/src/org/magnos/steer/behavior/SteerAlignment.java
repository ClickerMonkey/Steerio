
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
public class SteerAlignment<V extends Vec<V>> extends AbstractSteerSpatial<V, SteerAlignment<V>>
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
    public SteerAlignment( float minimum, float maximum, SpatialDatabase<V> space, float query )
    {
        this( minimum, maximum, space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED );
    }

    /**
     * Instantiates a new SteerAlignment for all groups, maximum field of view, and up to {@link AbstractSteerSpatial#DEFAULT_MAX_RESULTS} affecting
     * entities.
     * 
     * @param space
     *        The spatial database containing the other {@link SteerSubject}s affecting this steering behavior.
     * @param query
     *        The radius of the query circle around the {@link SteerSubject}.
     */
    public SteerAlignment( float magnitude, SpatialDatabase<V> space, float query )
    {
        this( magnitude, magnitude, space, query, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED );
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
    public SteerAlignment( float minimum, float maximum, SpatialDatabase<V> space, float query, long groups, int max )
    {
        this( minimum, maximum, space, query, groups, max, null, DEFAULT_SHARED );
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
    public SteerAlignment( float magnitude, SpatialDatabase<V> space, float query, long groups, int max )
    {
        this( magnitude, magnitude, space, query, groups, max, null, DEFAULT_SHARED );
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
    public SteerAlignment( float minimum, float maximum, SpatialDatabase<V> space, float query, long groups, int max, Filter<V> filter )
    {
        this( minimum, maximum, space, query, groups, max, filter, DEFAULT_SHARED );
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
    public SteerAlignment( float magnitude, SpatialDatabase<V> space, float query, long groups, int max, Filter<V> filter )
    {
        this( magnitude, magnitude, space, query, groups, max, filter, DEFAULT_SHARED );
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
    public SteerAlignment( float minimum, float maximum, SpatialDatabase<V> space, float query, long groups, int max, Filter<V> filter, boolean shared )
    {
        super( minimum, maximum, space, query, query, groups, max, filter, shared );
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
    public SteerAlignment( float magnitude, SpatialDatabase<V> space, float minimumRadius, float maximumRadius, long groups, int max, Filter<V> filter, boolean shared )
    {
        super( magnitude, magnitude, space, minimumRadius, maximumRadius, groups, max, filter, shared );
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
    public SteerAlignment( float minimum, float maximum, SpatialDatabase<V> space, float minimumRadius, float maximumRadius, long groups, int max, Filter<V> filter, boolean shared )
    {
        super( minimum, maximum, space, minimumRadius, maximumRadius, groups, max, filter, shared );
    }

    @Override
    public float getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        force = out; 

        int total = search( subject );

        if ( total > 0 )
        {
            return forceFromVector( this, force );
        }
        
        return Steer.NONE;
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerAlignment<V>( minimum, maximum, space, minimumRadius, maximumRadius, groups, max, filter, shared );
    }

    @Override
    public boolean onFoundInView( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups, float delta )
    {
        // Only steer subjects are applicable (because only they have direction).
        boolean applicable = (entity instanceof SteerSubject);

        if ( applicable )
        {
            SteerSubject<V> ss = (SteerSubject<V>)entity;

            force.addsi( ss.getDirection(), calculateMagnitude( delta ) );
        }

        return applicable;
    }

}
