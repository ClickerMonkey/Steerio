
package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.vec.Vec;


/**
 * A steering behavior that makes the subject face a target.
 */
public class SteerFace<V extends Vec<V>> extends AbstractSteer<V, SteerFace<V>>
{

    public static float DEFAULT_THRESHOLD = 0.05f;
    
    public Target<V> target;
    public boolean shared;
    public float threshold;

    public SteerFace( float minimum, float maximum, Target<V> target )
    {
        this( minimum, maximum, DEFAULT_THRESHOLD, target, DEFAULT_SHARED );
    }

    public SteerFace( float magnitude, Target<V> target )
    {
        this( NONE, magnitude, DEFAULT_THRESHOLD, target, DEFAULT_SHARED );
    }

    public SteerFace( float magnitude, Target<V> target, boolean shared )
    {
        this( NONE, magnitude, DEFAULT_THRESHOLD, target, shared );
    }

    public SteerFace( float minimum, float maximum, float threshold, Target<V> target, boolean shared )
    {
        super( minimum, maximum );
        
        this.threshold = threshold;
        this.target = target;
        this.shared = shared;
    }

    @Override
    public float getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        V position = subject.getPosition();
        V targetPosition = target.getTarget( subject );

        if ( targetPosition != null )
        {
            float distance = targetPosition.distance( position );
            
            out.set( position );
            out.addsi( subject.getDirection(), distance );
            out.subi( targetPosition );
            out.negi();
            
            float force = out.normalize();
            
            if ( force > threshold * distance )
            {
                return SteerMath.clamp( force, getMinimum(), getMaximum() );
            }
        }
        
        return Steer.NONE;
    }

    @Override
    public boolean isShared()
    {
        return shared;
    }

    @Override
    public Steer<V> clone()
    {
        return new SteerFace<V>( minimum, maximum, threshold, target, shared );
    }

}
