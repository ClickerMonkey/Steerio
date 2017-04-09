
package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


/**
 * A steering behavior that moves the subject closer to a target with maximum acceleration but slows down to a complete stop once it comes within a
 * given distance.
 */
public class SteerArrive<V extends Vec<V>> extends AbstractSteer<V, SteerArrive<V>>
{

    public Target<V> target;
    public float caution;
    public float arrived;
    public boolean shared;

    /**
     * Instantiates a new SteerArrive that can be shared.
     * 
     * @param target
     *        The target to steer towards and arrive at.
     * @param caution
     *        The radius of the circle around the target to slow down once entered.
     * @param arrived
     *        The distance from the target that is considered "arrived". A common value for this is 0.
     */
    public SteerArrive( float maximum, Target<V> target, float caution, float arrived )
    {
        this( maximum, target, caution, arrived, DEFAULT_SHARED );
    }

    /**
     * Instantiates a new SteerArrive.
     * 
     * @param target
     *        The target to steer towards and arrive at.
     * @param caution
     *        The radius of the circle around the target to slow down once entered.
     * @param arrived
     *        The distance from the target that is considered "arrived". A common value for this is 0.
     * @param shared
     *        Whether this {@link Steer} implementation can be shared between {@link SteerSubject}s.
     */
    public SteerArrive( float maximum, Target<V> target, float caution, float arrived, boolean shared )
    {
        super( NONE, maximum );
        
        this.target = target;
        this.caution = caution;
        this.arrived = arrived;
        this.shared = shared;
    }

    @Override
    public float getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        SpatialEntity<V> targetEntity = target.getTarget( subject );

        if ( targetEntity != null )
        {
            out.directi( subject.getPosition(), targetEntity.getPosition() );

            float distance = out.length();

            if ( distance > arrived )
            {
                float factor = Math.min( distance / caution, 1 );

                out.divi( distance );
                out.muli( maximum );
                out.muli( factor * factor );
                out.subi( subject.getVelocity() );
                out.divi( elapsed );
                
                return forceFromVector( this, out );
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
        return new SteerArrive<V>( maximum, target, caution, arrived, shared );
    }

}
