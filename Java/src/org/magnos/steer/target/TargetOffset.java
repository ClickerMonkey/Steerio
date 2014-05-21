
package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.vec.Vec;


public class TargetOffset<V extends Vec<V>> implements Target<V>
{

    public V direction;
    public V position;
    public V offset;
    public boolean relative;

    public final V actual;

    public TargetOffset( SteerSubject<V> target, V offset, boolean relative )
    {
        this( target.getPosition(), target.getDirection(), offset, relative );
    }

    public TargetOffset( V position, V direction, V offset, boolean relative )
    {
        this.position = position;
        this.direction = direction;
        this.offset = offset;
        this.relative = relative;
        this.actual = offset.create();
    }

    @Override
    public V getTarget( SteerSubject<V> subject )
    {
        actual.set( offset );

        if ( relative )
        {
            // TODO better
            if ( direction.isUnit() )
            {
                actual.rotatei( direction );
            }
            else
            {
                actual.rotatei( direction.normal() );
            }
        }

        actual.addi( position );

        return actual;
    }

}
