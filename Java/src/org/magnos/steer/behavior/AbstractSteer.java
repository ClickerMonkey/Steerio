
package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec;
import org.magnos.steer.vec.Vec2;


/**
 * Abstract steering behavior implementation.
 */
public abstract class AbstractSteer<V extends Vec<V>, S extends Steer<V>> implements Steer<V>
{

    protected float maximum;
    protected float minimum;
    
    public AbstractSteer()
    {
        
    }
    
    public AbstractSteer( float magnitude )
    {
        this( magnitude, magnitude );
    }
    
    public AbstractSteer( float minimum, float maximum )
    {
        this.minimum = minimum;
        this.maximum = maximum;
    }
    
    public S withMagnitude( float magnitude )
    {
        return withRange( magnitude, magnitude );
    }
    
    public S withRange( float minimum, float maximum )
    {
        this.minimum = minimum;
        this.maximum = minimum;
        
        return (S)this;
    }
    
    protected float calculateMagnitude( float delta )
    {
        return (maximum - minimum) * SteerMath.clamp( delta, 0f, 1f ) + minimum;
    }

    @Override
    public float getMaximum()
    {
        return maximum;
    }

    @Override
    public void setMaximum( float maximum )
    {
        this.maximum = maximum;
    }

    @Override
    public float getMinimum()
    {
        return minimum;
    }

    @Override
    public void setMinimum( float minimum )
    {
        this.minimum = minimum;
    }

    @Override
    public Steer<V> clone()
    {
        // By default, a Steer cannot be cloned.
        return null;
    }

    public static <V extends Vec<V>> float forceFromDelta( Steer<V> steer, float delta )
    {
        return (steer.getMaximum() - steer.getMinimum()) * delta + steer.getMinimum();
    }

    public static <V extends Vec<V>> float forceFromVector( Steer<V> steer, V force )
    {
        float magnitude = force.normalize();
        
        if ( magnitude == 0 )
        {
            return 0;
        }
        
        return SteerMath.clamp( magnitude, steer.getMinimum(), steer.getMaximum() );
    }

    /**
     * Computes the force necessary to steer <code>subject</code> towards the <code>target</code> and stores that in <code>out</code>.
     * 
     * @param subject
     *        The {@link SteerSubject} desiring to move towards the target.
     * @param target
     *        The target to move towards.
     * @param out
     *        The necessary force required to move the subject to the target.
     * @param steer
     *        The steering behavior to calculate the force for.
     */
    public static <V extends Vec<V>> float towards( SteerSubject<V> subject, V target, V out, Steer<V> steer )
    {
        return forceFromVector( steer, out.directi( subject.getPosition(), target ) );
    }
    
    /**
     * Computes the force necessary to steer <code>subject</code> away from the <code>target</code> and stores that in <code>out</code>.
     * 
     * @param subject
     *        The {@link SteerSubject} desiring to move away from the target.
     * @param target
     *        The target to move away from.
     * @param out
     *        The necessary force required to move the subject away from the target.
     * @param steer
     *        The steering behavior to calculate the force for.
     */
    public static <V extends Vec<V>> float away( SteerSubject<V> subject, V target, V out, Steer<V> steer )
    {
        return forceFromVector( steer, out.directi( target, subject.getPosition() ) );
    }

    /**
     * Computes the force necessary to move in a negative direction based on <code>force</code>.
     * 
     * @param subject
     *        The {@link SteerSubject} desiring to move backwards.
     * @param force
     *        The force to use to compute the maximum backward force.
     * @param out
     *        The necessary force required to move the subject backwards.
     * @param steer
     *        The steering behavior to calculate the force for.
     */
    public static <V extends Vec<V>> float backward( SteerSubject<V> subject, V force, V out, Steer<V> steer )
    {
        return forceFromVector( steer, out.set( force ).negi() ); 
    }

    /**
     * Computes the force necessary to move in a positive direction based on <code>force</code>.
     * 
     * @param subject
     *        The {@link SteerSubject} desiring to move forwards.
     * @param force
     *        The force to use to compute the maximum forward force.
     * @param out
     *        The necessary force required to move the subject forwards.
     * @param steer
     *        The steering behavior to calculate the force for.
     */
    public static <V extends Vec<V>> float forward( SteerSubject<V> subject, V force, V out, Steer<V> steer )
    {
        return forceFromVector( steer, out.set( force ) );
    }

    /**
     * Determines whether the given <code>point</code> is in front of some object with the given position (<code>pos</code>) and direction
     * (<code>dir</code>).
     * 
     * @param pos
     *        The position of the object.
     * @param dir
     *        The direction of the object.
     * @param point
     *        The point to test whether it's in front of the object.
     * @return True if <code>point</code> is in front of the object determined by <code>pos</code> and <code>dir</code>.
     */
    public static <V extends Vec<V>> boolean inFront( V pos, V dir, V point )
    {
        return (dir.dot( point.sub( pos ) ) > 0);
    }

    /**
     * Determines whether the given <code>point</code> is in front of some object with the given position (<code>pos</code>) and direction
     * (<code>dir</code>).
     * 
     * @param pos
     *        The position of the object.
     * @param dir
     *        The direction of the object.
     * @param point
     *        The point to test whether it's in front of the object.
     * @return True if <code>point</code> is in front of the object determined by <code>pos</code> and <code>dir</code>.
     */
    public static <V extends Vec<V>> boolean inBack( V pos, V dir, V point )
    {
        return (dir.dot( pos.sub( point ) ) > 0);
    }

    /**
     * Calculates the intersection time between two {@link SteerSubject}s.
     * 
     * @param bullet
     *        The first {@link SteerSubject}.
     * @param target
     *        The second {@link SteerSubject}.
     * @return The intersection time in seconds, or -1 if no intersection will ever occur.
     */
    public static float intersectionTime( SteerSubject<Vec2> bullet, SteerSubject<Vec2> target )
    {
        return SteerMath.interceptTime( target.getPosition(), target.getVelocity().length(), bullet.getPosition(), bullet.getVelocity() );
    }

}
