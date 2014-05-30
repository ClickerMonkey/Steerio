package org.magnos.steer.filter;

import org.magnos.steer.Filter;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec;


public class FilterAnd<V extends Vec<V>, T> implements Filter<V, T>
{

    public Filter<V, T>[] filters;
    
    public FilterAnd(Filter<V, T> ... filters)
    {
        this.filters = filters;
    }
    
    @Override
    public boolean isValid( SteerSubject<V> subject, T test )
    {
        for (int i = 0; i < filters.length; i++)
        {
            if (!filters[i].isValid( subject, test ))
            {
                return false;
            }
        }
        
        return true;
    }
    
}
