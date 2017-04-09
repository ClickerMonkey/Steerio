
package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


public class TargetOffset<V extends Vec<V>> implements Target<V>
{

    public Target<V> target;
    public V offset;
    public boolean relative;

    public final V actual;

    public TargetOffset( Target<V> target, V offset, boolean relative )
    {
        this.target = target;
        this.offset = offset;
        this.relative = relative;
        this.actual = offset.create();
    }

    @Override
    public SpatialEntity<V> getTarget( SteerSubject<V> subject )
    {
        SpatialEntity<V> found = target.getTarget( subject );
        
        if ( found == null )
        {
            return null;
        }
        
        actual.set( offset );

        if ( relative )
        {
            V direction = found.getDirection();
            
            if ( direction.isUnit() )
            {
                actual.rotatei( direction );
            }
            else
            {
                actual.rotatei( direction.normal() );
            }
        }

        actual.addi( found.getPosition() );

        return actual;
    }

}
