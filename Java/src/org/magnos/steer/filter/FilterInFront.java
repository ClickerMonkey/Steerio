
package org.magnos.steer.filter;

import org.magnos.steer.Filter;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.behavior.AbstractSteer;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


public class FilterInFront<V extends Vec<V>> implements Filter<V>
{

    @SuppressWarnings ("rawtypes")
    public static final FilterInFront FRONT = new FilterInFront( true );

    @SuppressWarnings ("rawtypes")
    public static final FilterInFront BACK = new FilterInFront( false );
    
    public boolean front;

    public FilterInFront()
    {
        this( true );
    }

    public FilterInFront( boolean front )
    {
        this.front = front;
    }

    @Override
    public boolean isValid( SteerSubject<V> subject, SpatialEntity<V> test )
    {
        return (front == AbstractSteer.inFront( test.getPosition(), test.getDirection(), subject.getPosition() ));
    }

}
