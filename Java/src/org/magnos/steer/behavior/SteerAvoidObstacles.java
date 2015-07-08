
package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Filter;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


/**
 * A steering behavior that avoids obstacles in space by using a feeler (query)
 * to determine possible collisions and avoiding them.
 */
public class SteerAvoidObstacles<V extends Vec<V>> extends AbstractSteerSpatial<V, SteerAvoidObstacles<V>>
{

    public float lookahead;
    public float buffer;

    protected V force;
    protected float lookaheadRadius;
    protected float lookaheadVelocity;
    protected V lookaheadWallStart;
    protected V lookaheadWallEnd;
    protected final V lookaheadPoint;
    protected final V lookaheadCenter;
    protected final V lookaheadClosest;
    protected final V lookaheadNormal;

    public SteerAvoidObstacles( float minimum, float maximum, SpatialDatabase<V> space, float lookahead, float buffer, V template )
    {
        this( minimum, maximum, space, lookahead, buffer, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED, template );
    }

    public SteerAvoidObstacles( float magnitude, SpatialDatabase<V> space, float lookahead, float buffer, V template )
    {
        this( magnitude, magnitude, space, lookahead, buffer, SpatialDatabase.ALL_GROUPS, DEFAULT_MAX_RESULTS, null, DEFAULT_SHARED, template );
    }

    public SteerAvoidObstacles( float minimum, float maximum, SpatialDatabase<V> space, float lookahead, float buffer, long groups, int max, V template )
    {
        this( minimum, maximum, space, lookahead, buffer, groups, max, null, DEFAULT_SHARED, template );
    }

    public SteerAvoidObstacles( float magnitude, SpatialDatabase<V> space, float lookahead, float buffer, long groups, int max, V template )
    {
        this( magnitude, magnitude, space, lookahead, buffer, groups, max, null, DEFAULT_SHARED, template );
    }

    public SteerAvoidObstacles( float minimum, float maximum, SpatialDatabase<V> space, float lookahead, float buffer, long groups, int max, Filter<V, SpatialEntity<V>> filter, V template )
    {
        this( minimum, maximum, space, lookahead, buffer, groups, max, filter, DEFAULT_SHARED, template );
    }

    public SteerAvoidObstacles( float magnitude, SpatialDatabase<V> space, float lookahead, float buffer, long groups, int max, Filter<V, SpatialEntity<V>> filter, V template )
    {
        this( magnitude, magnitude, space, lookahead, buffer, groups, max, filter, DEFAULT_SHARED, template );
    }

    public SteerAvoidObstacles( float magnitude, SpatialDatabase<V> space, float lookahead, float buffer, long groups, int max, Filter<V, SpatialEntity<V>> filter, boolean shared, V template )
    {
        this( magnitude, magnitude, space, lookahead, buffer, groups, max, filter, shared, template );
    }

    public SteerAvoidObstacles( float minimum, float maximum, SpatialDatabase<V> space, float lookahead, float buffer, long groups, int max, Filter<V, SpatialEntity<V>> filter, boolean shared, V template )
    {
        super( minimum, maximum, space, 0f, 0f, groups, max, filter, shared );

        this.lookahead = lookahead;
        this.buffer = buffer;
        this.lookaheadPoint = template.create();
        this.lookaheadCenter = template.create();
        this.lookaheadClosest = template.create();
        this.lookaheadNormal = template.create();
    }

    @Override
    protected int search( SteerSubject<V> ss )
    {
        final V p = ss.getPosition();
        final V v = ss.getVelocity();
        final V d = ss.getDirection();
        final float r = ss.getRadius();

        subject = ss;

        lookaheadPoint.set( p );
        lookaheadPoint.addsi( d, lookahead );
        lookaheadCenter.interpolatei( p, lookaheadPoint, 0.5f );
        lookaheadVelocity = v.length();
        lookaheadRadius = (lookahead * 0.5f) + r + buffer;

        lookaheadWallStart = p;
        lookaheadWallEnd = lookaheadPoint;

        return space.intersects( lookaheadCenter, lookaheadRadius, max, groups, this );
    }

    @Override
    public float getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        force = out;

        int found = search( subject );

        if ( found > 0 )
        {
            return forceFromVector( this, force );
        }
        
        return Steer.NONE;
    }

    @Override
    public boolean onFoundInView( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups, float delta )
    {
        float distance = entity.getDistanceAndNormal( lookaheadWallStart, lookaheadWallEnd, lookaheadNormal );

        boolean found = distance < buffer;

        if ( found )
        {
            float magnitude = forceFromVector( this, lookaheadNormal );
            
            force.addsi( lookaheadNormal, magnitude * delta );
        }

        return found;
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerAvoidObstacles<V>( minimum, maximum, space, lookahead, buffer, groups, max, filter, shared, lookaheadPoint );
    }
    
}
