
package org.magnos.steer.constraint;

import org.magnos.steer.Constraint;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec;


public class ConstraintZeroVelocityThreshold<V extends Vec<V>> implements Constraint<V>
{

    public float threshold;

    public ConstraintZeroVelocityThreshold( float threshold )
    {
        this.threshold = threshold;
    }

    @Override
    public void constrain( float elapsed, SteerSubject<V> subject )
    {
        final V vel = subject.getVelocity();
        final V acc = subject.getAcceleration();
        
        if ( acc.dot( vel ) < 0 && vel.lengthSq() < threshold * threshold )
        {
            vel.clear();
        }
    }

}
