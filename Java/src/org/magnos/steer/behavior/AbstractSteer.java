
package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;


/**
 * Abstract steering behavior implementation.
 */
public abstract class AbstractSteer implements Steer
{

    public final Vector force = new Vector();
    
    protected boolean maximized = true;


    @Override
    public boolean isMaximized()
    {
        return maximized;
    }
    
    @Override
    public void setMaximized(boolean maximize)
    {
        this.maximized = maximize;
    }
    
    /**
     * By default, a Steer cannot be cloned.
     */
    public Steer clone()
    {
        return null;
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
    public static void towards( SteerSubject subject, Vector target, Vector out, Steer steer )
    {
        out.directi( subject.getPosition(), target );

        if ( steer.isMaximized() )
        {
            maximize( subject, out );    
        }
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
    public static void away( SteerSubject subject, Vector target, Vector out, Steer steer )
    {
        out.directi( target, subject.getPosition() );

        if ( steer.isMaximized() )
        {
            maximize( subject, out );    
        }
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
    public static void backward( SteerSubject subject, Vector force, Vector out, Steer steer )
    {
        out.set( -force.x, -force.y );

        if ( steer.isMaximized() )
        {
            maximize( subject, out );
        }
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
    public static void forward( SteerSubject subject, Vector force, Vector out, Steer steer )
    {
        out.set( force );

        if ( steer.isMaximized() )
        {
            maximize( subject, out );
        }
    }

    /**
     * Maximums the given force based on the {@link SteerSubject}'s maximum acceleration and current velocity.
     * 
     * @param subject
     *        The subject to obtain a velocity and maximum acceleration from to maximize the given force.
     * @param force
     *        The force to maximize.
     */
    public static void maximize( SteerSubject subject, Vector force )
    {
        if ( force.length( subject.getAccelerationMax() ) > 0 )
        {
            force.subi( subject.getVelocity() );
        }
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
    public static boolean inFront( Vector pos, Vector dir, Vector point )
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
    public static boolean inBack( Vector pos, Vector dir, Vector point )
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
    public static float intersectionTime( SteerSubject bullet, SteerSubject target )
    {
        return SteerMath.interceptTime( target.getPosition(), target.getVelocity().length(), bullet.getPosition(), bullet.getVelocity() );
    }

}
