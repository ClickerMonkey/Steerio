package org.magnos.steer.filter;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Filter;
import org.magnos.steer.util.FieldOfView;
import org.magnos.steer.vec.Vec2;

public class FilterViewCustomRadius2 implements Filter<Vec2, Vec2>
{
    
    public Vec2 fov;
    public FieldOfView fovType;
    public float radius;

    @Override
    public boolean isValid( SteerSubject<Vec2> subject, Vec2 test )
    {
        return SteerMath.isCircleInView( subject.getPosition(), subject.getDirection(), fov, test, radius, fovType );
    }
    
}
