
package org.magnos.steer.spatial;

import org.magnos.steer.Obstacle;
import org.magnos.steer.vec.Vec;


public class SpatialEntityObstacle<V extends Vec<V>> implements SpatialEntity<V>
{

    public static final boolean DEFAULT_FIXED = true;

    public Obstacle<V> obstacle;
    public V position;
    public boolean inert;
    public boolean fixed;
    public long groups;
    public long collisionGroups;
    public Object attachment;

    public SpatialEntityObstacle()
    {
    }

    public SpatialEntityObstacle( Obstacle<V> obstacle, V position, long groups, long collisionGroups )
    {
        this( obstacle, position, groups, collisionGroups, DEFAULT_FIXED );
    }

    public SpatialEntityObstacle( Obstacle<V> obstacle, V position, long groups, long collisionGroups, boolean fixed )
    {
        this.obstacle = obstacle;
        this.position = position;
        this.groups = groups;
        this.collisionGroups = collisionGroups;
        this.fixed = fixed;
        this.inert = false;
    }

    @Override
    public V getPosition()
    {
        return obstacle.getPosition( position );
    }

    @Override
    public float getRadius()
    {
        return obstacle.getRadius();
    }

    @Override
    public float getDistanceAndNormal( V origin, V lookahead, V outNormal )
    {
        return obstacle.getDistanceAndNormal( origin, lookahead, outNormal );
    }

    @Override
    public V getPosition( V out )
    {
        return obstacle.getPosition( out );
    }

    @Override
    public long getSpatialGroups()
    {
        return groups;
    }

    @Override
    public long getSpatialCollisionGroups()
    {
        return collisionGroups;
    }

    @Override
    public boolean isStatic()
    {
        return fixed;
    }

    @Override
    public boolean isInert()
    {
        return inert;
    }

    @Override
    public void attach( Object attachment )
    {
        this.attachment = attachment;
    }

    @Override
    public <T> T attachment()
    {
        return (T)attachment;
    }

    @Override
    public <T> T attachment( Class<T> type )
    {
        return attachment != null && type.isAssignableFrom( attachment.getClass() ) ? (T)attachment : null;
    }

}
