
package org.magnos.steer;

import org.magnos.steer.spatial.BaseSpatialEntity;
import org.magnos.steer.vec.Vec2;

public class AssertSpatialEntity extends BaseSpatialEntity<Vec2>
{

    public Vec2 position = new Vec2();
    public float radius;
    
    public AssertSpatialEntity()
    {
        this.groups = -1;
        this.collisionGroups = -1;
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

}
