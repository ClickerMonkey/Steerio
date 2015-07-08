
package org.magnos.steer;

import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec2;

public class AssertSpatialEntity implements SpatialEntity<Vec2>
{

    public long groups = -1;
    public long collisionGroups = -1;
    public Vec2 position = new Vec2();
    public float radius;

    @Override
    public float getDistanceAndNormal( Vec2 origin, Vec2 lookahead, Vec2 outNormal )
    {
        return SteerMath.closest( origin, lookahead, position, outNormal ).subi( position ).normalize() - radius;
    }

    @Override
    public Vec2 getPosition( Vec2 out )
    {
        return out.set( position );
    }

    @Override
    public Vec2 getPosition()
    {
        return position;
    }

    @Override
    public float getRadius()
    {
        return radius;
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
        return false;
    }

    @Override
    public boolean isInert()
    {
        return false;
    }

    @Override
    public void attach( Object attachment )
    {

    }

    @Override
    public <T> T attachment()
    {
        return null;
    }

    @Override
    public <T> T attachment( Class<T> type )
    {
        return null;
    }

}
