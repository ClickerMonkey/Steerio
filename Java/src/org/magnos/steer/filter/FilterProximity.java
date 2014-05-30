package org.magnos.steer.filter;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Filter;
import org.magnos.steer.vec.Vec;

public class FilterProximity<V extends Vec<V>> implements Filter<V, SteerSubject<V>>
{
    
    public float minimum;
    public float maximum;

    public FilterProximity()
    {
        this( 0, Float.MAX_VALUE );
    }
    
    public FilterProximity(float max)
    {
        this( 0, max );
    }
    
    public FilterProximity(float min, float max)
    {
        this.minimum = min;
        this.maximum = max;
    }

    @Override
    public boolean isValid( SteerSubject<V> subject, SteerSubject<V> test )
    {
        float distanceSq = test.getPosition().distanceSq( subject.getPosition() );
        
        if (minimum != 0 && distanceSq < minimum * minimum)
        {
            return false;
        }
        
        if (maximum != Float.MAX_VALUE && distanceSq > maximum * maximum)
        {
            return false;
        }
        
        return true;
    }
    
}
