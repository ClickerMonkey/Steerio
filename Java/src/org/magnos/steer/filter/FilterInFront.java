
package org.magnos.steer.filter;

import org.magnos.steer.Filter;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.behavior.AbstractSteer;
import org.magnos.steer.vec.Vec;


public class FilterInFront<V extends Vec<V>> implements Filter<V, SteerSubject<V>>
{

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
    public boolean isValid( SteerSubject<V> subject, SteerSubject<V> test )
    {
        return (front == AbstractSteer.inFront( test.getPosition(), test.getDirection(), subject.getPosition() ));
    }

}
