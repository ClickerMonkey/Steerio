package org.magnos.steer.filter;

import org.magnos.steer.Filter;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


public class FilterOr<V extends Vec<V>, T> implements Filter<V>
{

    public Filter<V>[] filters;
    
    public FilterOr(Filter<V> ... filters)
    {
        this.filters = filters;
    }
    
    @Override
    public boolean isValid( SteerSubject<V> subject, SpatialEntity<V> test )
    {
        for (int i = 0; i < filters.length; i++)
        {
            if (filters[i].isValid( subject, test ))
            {
                return true;
            }
        }
        
        return false;
    }
    
}
