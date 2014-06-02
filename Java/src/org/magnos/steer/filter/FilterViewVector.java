
package org.magnos.steer.filter;

import org.magnos.steer.Filter;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.util.FieldOfView;
import org.magnos.steer.vec.Vec;


public class FilterViewVector<V extends Vec<V>> implements Filter<V, V>
{
    
    public static <V extends Vec<V>> FilterViewVector<V> fromRadians( double radians, float radius, FieldOfView fovType, Class<V> vectorType )
    {
        return fromRadians( radians, radius, fovType );
    }
    
    public static <V extends Vec<V>> FilterViewVector<V> fromRadians( double radians, float radius, FieldOfView fovType )
    {
        double half = radians * 0.5;
        double tan = Math.tan( half );
        double cos = Math.cos( half );
        
        return new FilterViewVector<V>( tan, cos, radius, fovType );
    }
    
    public static <V extends Vec<V>> FilterViewVector<V> fromDegrees( double degrees, float radius, FieldOfView fovType, Class<V> vectorType )
    {
        return fromDegrees( degrees, radius, fovType );
    }

    public static <V extends Vec<V>> FilterViewVector<V> fromDegrees( double degrees, float radius, FieldOfView fovType )
    {
        double half = Math.toRadians( degrees * 0.5 );
        double tan = Math.tan( half );
        double cos = Math.cos( half );
        
        return new FilterViewVector<V>( tan, cos, radius, fovType );
    }

    public double fovTan;
    public double fovCos;
    public float radius;
    public FieldOfView fovType;
    
    public FilterViewVector( double fovTan, double fovCos, float radius, FieldOfView fovType )
    {
        this.fovTan = fovTan;
        this.fovCos = fovCos;
        this.radius = radius;
        this.fovType = fovType;
    }

    @Override
    public boolean isValid( SteerSubject<V> subject, V test )
    {
        return SteerMath.isCircleInView( subject.getPosition(), subject.getDirection(), fovTan, fovCos, test, radius, fovType );
    }

}
