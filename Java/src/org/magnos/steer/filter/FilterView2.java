
package org.magnos.steer.filter;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.SteerSubjectFilter;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.util.FieldOfView;
import org.magnos.steer.vec.Vec2;


public class FilterView2 implements SteerSubjectFilter<Vec2, SpatialEntity<Vec2>>
{

    public Vec2 fov;
    public FieldOfView fovType;

    public FilterView2( float halfFOV, FieldOfView fovType )
    {
        this.fov = Vec2.fromAngle( halfFOV );
        this.fovType = fovType;
    }

    public FilterView2( Vec2 fov, FieldOfView fovType )
    {
        this.fov = fov;
        this.fovType = fovType;
    }

    @Override
    public boolean isValid( SteerSubject<Vec2> subject, SpatialEntity<Vec2> test )
    {
        return SteerMath.isCircleInView( subject.getPosition(), subject.getDirection(), fov, test.getPosition(), test.getRadius(), fovType );
    }

}
