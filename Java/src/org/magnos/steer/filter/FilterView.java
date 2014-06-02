
package org.magnos.steer.filter;

import org.magnos.steer.Filter;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.util.FieldOfView;
import org.magnos.steer.vec.Vec;


public class FilterView<V extends Vec<V>> implements Filter<V, SpatialEntity<V>>
{

    public static <V extends Vec<V>> FilterView<V> fromRadians( double radians, FieldOfView fovType, Class<V> vectorType )
    {
        return fromRadians( radians, fovType );
    }
    
    public static <V extends Vec<V>> FilterView<V> fromRadians( double radians, FieldOfView fovType )
    {
        double half = radians * 0.5;
        double tan = Math.tan( half );
        double cos = Math.cos( half );
        
        return new FilterView<V>( tan, cos, fovType );
    }
    
    public static <V extends Vec<V>> FilterView<V> fromDegrees( double degrees, FieldOfView fovType, Class<V> vectorType )
    {
        return fromDegrees( degrees, fovType );
    }

    public static <V extends Vec<V>> FilterView<V> fromDegrees( double degrees, FieldOfView fovType )
    {
        double half = Math.toRadians( degrees * 0.5 );
        double tan = Math.tan( half );
        double cos = Math.cos( half );
        
        return new FilterView<V>( tan, cos, fovType );
    }

    public double fovTan;
    public double fovCos;
    public FieldOfView fovType;
    
    public FilterView( double fovTan, double fovCos, FieldOfView fovType )
    {
        this.fovTan = fovTan;
        this.fovCos = fovCos;
        this.fovType = fovType;
    }

    @Override
    public boolean isValid( SteerSubject<V> subject, SpatialEntity<V> test )
    {
        return SteerMath.isCircleInView( subject.getPosition(), subject.getDirection(), fovTan, fovCos, test.getPosition(), test.getRadius(), fovType );
    }

}
