
package org.magnos.steer.behavior;

import org.magnos.steer.Accumulator;
import org.magnos.steer.Filter;
import org.magnos.steer.Steer;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.accum.AccumulateAverage;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


/**
 * Abstract steering behavior that cares about other {@link SteerSubject}s around it.
 */
public abstract class AbstractSteerSpatial<V extends Vec<V>, S extends Steer<V>> extends AbstractSteer<V, S> implements SearchCallback<V>
{

    public static int DEFAULT_MAX_RESULTS = 16;

    public float minimumRadius;
    public float maximumRadius;
    public boolean shared;
    public long groups;
    public int max;
    public SpatialDatabase<V> space;
    public SteerSubject<V> subject;
    public Filter<V> filter;
    
    public AbstractSteerSpatial( SpatialDatabase<V> space )
    {
        this.space = space;
        this.max = DEFAULT_MAX_RESULTS;
    }
    
    /**
     * Instantiates a new {@link AbstractSteerSpatial}.
     * 
     * @param space
     *        The spatial database containing the other {@link SpatialEntity}s affecting this steering behavior.
     * @param query
     *        The radius of the query circle around the {@link SteerSubject}.
     * @param groups
     *        The groups the {@link SpatialEntity}s must be in to be visible to this steering behavior.
     * @param max
     *        The maximum number of {@link SpatialEntity}s that can affect this steering behavior.
     * @param fov
     *        The field of view in radians that this steering behavior has. The maximum value this can be is PI, since it really represents half of
     *        the field of view.
     * @param fovType
     *        A flag used to determine whether an object is in the field of view of a subject.
     * @param shared
     *        Whether this {@link Steer} implementation can be shared between {@link SteerSubject}s.
     */
    public AbstractSteerSpatial( float minimum, float maximum, SpatialDatabase<V> space, float minimumRadius, float maximumRadius, long groups, int max, Filter<V> filter, boolean shared )
    {
        super( minimum, maximum );
        
        this.space = space;
        this.groups = groups;
        this.max = max;
        this.shared = shared;
        this.filter = filter;
        this.minimumRadius = minimumRadius;
        this.maximumRadius = maximumRadius;
    }
    
    public AbstractSteerSpatial()
    {
        
    }
    
    public S withDropOff( float minimumRadius, float maximumRadius )
    {
        this.minimumRadius = minimumRadius;
        this.maximumRadius = maximumRadius;
        
        return (S)this;
    }
    
    public S withDropOff( float maximumRadius )
    {
        this.maximumRadius = maximumRadius;
        this.minimumRadius = maximumRadius;
        
        return (S)this;
    }
    
    public S withDatabase( SpatialDatabase<V> space )
    {
        this.space = space;
        
        return (S)this;
    }
    
    public S withFilterGroups( long groups )
    {
        this.groups = groups;
        
        return (S)this;
    }
    
    public S withFilterMax( int max )
    {
        this.max = max;
        
        return (S)this;
    }
    
    public S withFilter( Filter<V> filter )
    {
        this.filter = filter;
        
        return (S)this;
    }

    public S withShare( boolean share )
    {
        this.shared = share;
        
        return (S)this;
    }
    
    public Accumulator<V> getAccumulator()
    {
        return AccumulateAverage.INSTANCE;
    }
    
    /**
     * The method invoked when a {@link SpatialEntity} is found in the field of view of the subject meeting all specified criteria.
     * 
     * @param entity
     *        The entity found in view.
     * @param overlap
     *        The amount of overlap between the entity and the subject.
     * @param index
     *        The number of entities found in view (and accepted) before this find.
     * @param queryOffset
     *        The position of the subject.
     * @param queryRadius
     *        The radius of the query circle.
     * @param queryMax
     *        The maximum number of {@link SpatialEntity}s that can affect this steering behavior.
     * @param queryGroups
     *        The groups the {@link SpatialEntity} must be in to be visible to this steering behavior.
     * @return True if the given entity was valid (affects how many more entities are looked for).
     */
    protected abstract boolean onFoundInView( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups, float delta );

    /**
     * Runs a search for all intersecting {@link SpatialEntity}s with the circle centered on {@link SteerSubject} with radius {@link #query}.
     * 
     * @param steerSubject
     *        The {@link SteerSubject} to search around.
     * @return The number of {@link SpatialEntity}s found around the given subject that met the criteria.
     */
    protected int search( SteerSubject<V> steerSubject )
    {
        subject = steerSubject;

        return space.intersects( steerSubject.getPosition(), minimumRadius, max, groups, this );
    }

    @Override
    public boolean isShared()
    {
        return shared;
    }

    @Override
    public final boolean onFound( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups )
    {
        // The entity shouldn't find itself.
        if ( entity == subject )
        {
            return false;
        }
        
        // Check if the entity is valid.
        if ( filter != null && !filter.isValid( subject, entity ) )
        {
            return false;
        }
        
        float radiusDifference = minimumRadius - maximumRadius;
        float radiusInside = overlap - maximumRadius;
        float delta = SteerMath.clamp( radiusInside / radiusDifference, 0, 1 );
        
        return onFoundInView( entity, overlap, index, queryOffset, queryRadius, queryMax, queryGroups, delta );
    }

}
