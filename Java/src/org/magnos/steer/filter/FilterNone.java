package org.magnos.steer.filter;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.SteerSubjectFilter;
import org.magnos.steer.vec.Vec;

public class FilterNone<V extends Vec<V>, T> implements SteerSubjectFilter<V, T>
{
    
    @SuppressWarnings ("rawtypes")
    private static final FilterNone INSTANCE = new FilterNone();
    
    public static <V extends Vec<V>, T, F extends SteerSubjectFilter<V, T>> F get()
    {
        return (F)INSTANCE;
    }
    
    @Override
    public boolean isValid( SteerSubject<V> subject, T test )
    {
        return true;
    }
    
}
