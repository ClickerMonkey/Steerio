package org.magnos.steer;

import org.magnos.steer.vec.Vec;

/**
 * A {@link Steer} is considered a steering behavior or a steering force which 
 * gets applied to some {@link SteerSubject}.
 */
public interface Steer<V extends Vec<V>>
{
	
	/**
	 * Infinite steering force.
	 */
	public static final float INFINITE = Float.MAX_VALUE;
	
	/**
	 * Gets the amount of force caused by this steering behavior.
	 * 
	 * @param elapsed
	 * 	The elapsed time since the last call.
	 * @param subject
	 * 	The subject to get the steering force of.
	 * @return
	 * 	The reference to the force to apply.
	 */
	public void getForce( float elapsed, SteerSubject<V> subject, V out );
	
	/**
	 * Whether the steer can be/is shared by several subjects. If this is true
	 * then this steer won't be copied for each subject it affects. Some steering
	 * implementations cannot be shared, some can be.
	 * 
	 * @return
	 * 	True if the steering behavior can be/is shared.
	 */
	public boolean isShared();
	
	/**
	 * TODO
	 * @return
	 */
	public boolean isMaximized();
	
	/**
	 * TODO
	 * @param maximize
	 */
	public void setMaximized(boolean maximize);
	
	/**
	 * Returns a clone of this steering behavior. A behavior would be cloned
	 * if it can not be shared ({@link #isShared()} returns false).
	 * 
	 * @return
	 * 	A newly instantiated Steer that is a clone of this.
	 */
	public Steer<V> clone();
	
}
