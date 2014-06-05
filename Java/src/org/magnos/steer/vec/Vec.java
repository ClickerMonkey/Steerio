
package org.magnos.steer.vec;

import org.magnos.steer.Target;

/**
 * An interface to a Vector implementation of any dimension.
 * 
 * Operations in this class are in the following format:
 * 
 * <pre>
 *      // The operation is performed and the result is set to the out V which is then returned.
 *      public V operation( <i>parameters</i>, V out );
 *      
 *      // The operation is performed and the result is set to this Vector which is then returned.
 *      // This is the same as "return operation( <i>parameters</i>, this );"
 *      public V operationi( <i>parameters</i> );
 *      
 *      // The operation is performed and the result is set to a new Vector which is then returned.
 *      // This is the same as "return operation( <i>parameters</i>, new V() );"
 *      public V operation( <i>parameters</i> );
 * </pre>
 * 
 * Properties of unit (normalized) Vectors:
 * <ol>
 * <li>{@link #length()} and {@link #lengthSq()} return 1.0f</li>
 * <li>{@link #isUnit()} and {@link #isUnit(float)} return true</li>
 * <li>It's an efficient way of storing an angle.</li>
 * <li>Can be used as the normal parameter in {@link #reflect(V)} or {@link #refract(V)} methods.</li>
 * </ol>
 * 
 * @author Philip Diffenderfer
 * 
 */
public interface Vec<V extends Vec<V>> extends Target<V>
{

    /**
     * Returns the number of components in this Vector.
     */
    public int size();
    
    /**
     * Returns the component at the given index in this Vector.
     */
    public float getComponent(int component);
    
    /**
     * Sets the component at the given index in this Vector.
     */
    public void setComponent(int component, float value);
    
    /**
     * <p><code>this = v</code></p>
     * Sets the coordinates of this Vector and returns this.
     */
    public V set( V v );

    /**
     * <p><code>this = 0</code></p>
     * Clears this Vector's components by setting them to zero.
     */
    public void clear();

    /**
     * <p><code>this = value</code></p>
     * Clears this Vector's components by setting them to the given value.
     */
    public V clear( float value );

    /**
     * <p><code>this = -this</code></p>
     * Negates this Vector and returns this.
     */
    public V negi();

    /**
     * <p><code>out = -this</code></p>
     * Sets out to the negation of this Vector and returns out.
     */
    public V neg( V out );

    /**
     * <p><code>new (-this)</code></p>
     * Returns a new Vector that is the negation to this Vector.
     */
    public V neg();

    /**
     * <p><code>this = |this|</code></p>
     * Sets this Vector to it's absolute value and returns this.
     */
    public V absi();

    /**
     * <p><code>out = |this|</code></p>
     * Sets out to the absolute value of this Vector and returns out.
     */
    public V abs( V out );

    /**
     * <p><code>new (|this|)</code></p>
     * Returns a new Vector that is the absolute value of this Vector.
     */
    public V abs();

    /**
     * <p><code>this *= s</code></p>
     * Multiplies this Vector by s and returns this.
     */
    public V muli( float s );

    /**
     * <p><code>out = this * s</code></p>
     * Sets out to this Vector multiplied by s and returns out.
     */
    public V mul( float s, V out );

    /**
     * <p><code>new (this * s)</code></p>
     * Returns a new Vector that is a multiplication of this Vector and s.
     */
    public V mul( float s );

    /**
     * <p><code>this /= s</code></p>
     * Divides this Vector by s and returns this.
     */
    public V divi( float s );

    /**
     * <p><code>out = this / s</code></p>
     * Sets out to the division of this Vector and s and returns out.
     */
    public V div( float s, V out );

    /**
     * <p><code>new (this / s)</code></p>
     * Returns a new Vector that is a division between this Vector and s.
     */
    public V div( float s );

    /**
     * <p><code>this += s</code></p>
     * Adds s to this Vector and returns this.
     */
    public V addi( float s );

    /**
     * <p><code>out = this + s</code></p>
     * Sets out to the sum of this Vector and s and returns out.
     */
    public V add( float s, V out );

    /**
     * <p><code>new (this + s)</code></p>
     * Returns a new Vector that is the sum between this Vector and s.
     */
    public V add( float s );

    /**
     * <p><code>this *= v</code></p>
     * Multiplies this Vector by v and returns this.
     */
    public V muli( V v );

    /**
     * <p><code>out = this * v</code></p>
     * Sets out to the product of this Vector and v and returns out.
     */
    public V mul( V v, V out );

    /**
     * <p><code>new (this * v)</code></p>
     * Returns a new Vector that is the product of this Vector and v.
     */
    public V mul( V v );

    /**
     * <p><code>this /= v</code></p>
     * Divides this Vector by v and returns this.
     */
    public V divi( V v );

    /**
     * <p><code>out = this / v</code></p>
     * Sets out to the division of this Vector and v and returns out.
     */
    public V div( V v, V out );

    /**
     * <p><code>new (this / v)</code></p>
     * Returns a new Vector that is the division of this Vector by v.
     */
    public V div( V v );

    /**
     * <p><code>this += v</code></p>
     * Adds v to this Vector and returns this.
     */
    public V addi( V v );

    /**
     * <p><code>out = this + v</code></p>
     * Sets out to the addition of this Vector and v and returns out.
     */
    public V add( V v, V out );

    /**
     * <p><code>new (this + v)</code></p>
     * Returns a new Vector that is the addition of this Vector and v.
     */
    public V add( V v );

    /**
     * <p><code>this += v * s</code></p>
     * Adds v * s to this Vector and returns this.
     */
    public V addsi( V v, float s );

    /**
     * <p><code>out = this + v * s</code></p>
     * Sets out to the addition of this Vector and v * s and returns out.
     */
    public V adds( V v, float s, V out );

    /**
     * <p><code>new (this + v * s)</code></p>
     * Returns a new Vector that is the addition of this Vector and v * s.
     */
    public V adds( V v, float s );

    /**
     * <p><code>this -= v</code></p>
     * Subtracts v from this Vector and returns this.
     */
    public V subi( V v );

    /**
     * <p><code>out = this - v</code></p>
     * Sets out to the subtraction of v from this Vector and returns out.
     */
    public V sub( V v, V out );

    /**
     * <p><code>new (this - v)</code></p>
     * Returns a new Vector that is the subtraction of v from this Vector.
     */
    public V sub( V v );

    /**
     * <p><code>this = this % s</code></p>
     * Sets this to the remainder from this divided by s and returns this.
     */
    public V modi( float s );

    /**
     * <p><code>out = this % s</code></p>
     * Sets out to the remainder of this divided by s and returns out.
     */
    public V mod( float s, V out );

    /**
     * <p><code>new (this % s)</code></p>
     * Returns a new Vector that is the remainder of the division of this by s.
     */
    public V mod( float s );

    /**
     * <p><code>this = this % v</code></p>
     * Sets this to the remainder from this divided by v and returns this.
     */
    public V modi( V v );

    /**
     * <p><code>out = this % v</code></p>
     * Sets out to the remainder of this divided by v and returns out.
     */
    public V mod( V v, V out );

    /**
     * <p><code>new (this % v)</code></p>
     * Returns a new Vector that is the remainder of the division of this by v.
     */
    public V mod( V v );

    /**
     * <p><code>this = target - origin</code></p>
     * Sets this to the V starting at origin and ending at target, and
     * returns this.
     */
    public V directi( V origin, V target );

    /**
     * <p><code>out = target - origin</code></p>
     * Sets out to the V starting at origin and ending at target, and
     * returns out.
     */
    public V direct( V origin, V target, V out );

    /**
     * <p><code>new (target - origin)</code></p>
     * Returns a new Vector starting at origin and ending at target.
     */
    public V direct( V origin, V target );

    /**
     * <p><code>this = (end - start) * delta + start</code></p>
     * Sets this to the V between start and end based on delta where delta
     * is 0.0 for the start, 0.5 for the middle, and 1.0 for the end, and returns
     * this.
     */
    public V interpolatei( V start, V end, float delta );

    /**
     * <p><code>out = (end - start) * delta + start</code></p>
     * Sets out to the V between start and end based on delta where delta is
     * 0.0 for the start, 0.5 for the middle, and 1.0 for the end, and returns
     * out.
     */
    public V interpolate( V start, V end, float delta, V out );

    /**
     * <p><code>new ((end - start) * delta + start)</code></p>
     * Returns a new Vector between start and end based on delta where delta is
     * 0.0 for the start, 0.5 for the middle, and 1.0 for the end.
     */
    public V interpolate( V start, V end, float delta );

    /**
     * <p><code>this = (end - start) * delta + start</code></p>
     * Sets this to the V between start and end based on delta where delta
     * is 0.0 for the start, 0.5 for the middle, and 1.0 for the end, and returns
     * this.
     */
    public V interpolateToi( V end, float delta );

    /**
     * <p><code>out = (end - start) * delta + start</code></p>
     * Sets out to the V between start and end based on delta where delta is
     * 0.0 for the start, 0.5 for the middle, and 1.0 for the end, and returns
     * out.
     */
    public V interpolateTo( V end, float delta, V out );

    /**
     * <p><code>new ((end - start) * delta + start)</code></p>
     * Returns a new Vector between start and end based on delta where delta is
     * 0.0 for the start, 0.5 for the middle, and 1.0 for the end.
     */
    public V interpolateTo( V end, float delta );

    /**
     * <p><code>this = clamp(this, min, max)</code></p>
     * Sets this to the Vector that lies between min and max that is the closest 
     * to this Vector, and returns this.
     */
    public V clampi( V min, V max );

    /**
     * <p><code>out = clamp(this, min, max)</code></p>
     * Sets out to the Vector that lies between min and max that is the closest 
     * to this Vector, and returns out.
     */
    public V clamp( V min, V max, V out );

    /**
     * <p><code>new (clamp(this, min, max))</code></p>
     * Returns a new Vector that lies between min and max that is closest to this Vector.
     */
    public V clamp( V min, V max );

    /**
     * Sets the length of this Vector and returns the previous length. If this
     * Vector has no length, nothing changes.
     */
    public float length( float length );

    /**
     * Clamps the length of this Vector between a minimum and maximum and returns
     * this Vector. If this Vector has no length, nothing changes.
     */
    public V clamp( float min, float max );

    /**
     * If the length of this Vector is less than min, it's length will be set to
     * min and this will be returned.
     */
    public V min( float min );

    /**
     * If the length of this Vector is greater than max, it's length will be set
     * to max and this will be returned.
     */
    public V max( float max );

    /**
     * Rotates this Vector by the given unit V and returns this.
     */
    public V rotatei( V cossin );

    /**
     * Sets out to this Vector unit V by the normal and returns out.
     */
    public V rotate( V cossin, V out );

    /**
     * Returns a new Vector that is this Vector rotated by the unit V.
     */
    public V rotate( V cossin );

    /**
     * Un-Rotates this Vector by the given unit V and returns this.
     */
    public V unrotatei( V cossin );

    /**
     * Sets out to this Vector unit V by the normal and returns out.
     */
    public V unrotate( V cossin, V out );

    /**
     * Returns a new Vector that is this Vector unrotated by the unit V.
     */
    public V unrotate( V cossin );

    /**
     * <p><code>this = this reflected across normal</code></p>
     * Reflects this Vector across the normal and returns this.
     */
    public V reflecti( V normal );

    /**
     * <p><code>out = this reflected across normal</code></p>
     * Sets out to this Vector reflected across the normal and returns out.
     */
    public V reflect( V normal, V out );

    /**
     * <p><code>new (this reflected across normal)</code></p>
     * Returns a new Vector that is this Vector reflected across the normal.
     */
    public V reflect( V normal );

    /**
     * <p><code>this = this refracted across normal</code></p>
     * Reflects this Vector across the normal and returns this.
     */
    public V refracti( V normal );

    /**
     * <p><code>out = this refracted across normal</code></p>
     * Sets out to this Vector reflected across the normal and returns out.
     */
    public V refract( V normal, V out );

    /**
     * <p><code>new (this refracted across normal)</code></p>
     * Returns a new Vector that is this Vector reflected across the normal.
     */
    public V refract( V normal );

    /**
     * <p><code>this = Math.floor(this)</code></p>
     * Floors the components of this Vector and returns this.
     */
    public V floori();

    /**
     * <p><code>new (Math.floor(this))</code></p>
     * Returns a new Vector that is this Vector with floored components.
     */
    public V floor();

    /**
     * <p><code>out = Math.floor(this)</code></p>
     * Sets out to this Vector with its components floored and returns out.
     */
    public V floor( V out );

    /**
     * <p><code>this = Math.ceil(this)</code></p>
     * Ceilings the components of this Vector and returns this.
     */
    public V ceili();

    /**
     * <p><code>new (Math.ceil(this))</code></p>
     * Returns a new Vector that is this Vector with ceiling components.
     */
    public V ceil();

    /**
     * <p><code>out = Math.ceil(this)</code></p>
     * Sets out to this Vector with its components ceiling and returns out.
     */
    public V ceil( V out );
    
    /**
     * <p><code>this = 1 / this</code></p>
     * Reflects this Vector across the normal and returns this.
     */
    public V inverti();

    /**
     * <p><code>out = 1 / this</code></p>
     * Sets out to this Vector reflected across the normal and returns out.
     */
    public V invert( V out );
    
    /**
     * <p><code>new (1 / this)</code></p>
     * Returns a new Vector that is this Vector reflected across the normal.
     */
    public V invert();

    /**
     * <p><code>this = min(a, b)</code></p>
     * Sets this Vector to the minimum between a and b.
     */
    public V mini( V a, V b );

    /**
     * <p><code>this = max(a, b)</code></p>
     * Sets this Vector to the maximum between a and b.
     */
    public V maxi( V a, V b );

    /**
     * Normalizes this Vector into a unit Vector and returns the length. A unit
     * Vector has a length of 1.0 and the x-component represents COS(A) and the
     * y-component represents SIN(A) where A is the angle between this Vector and
     * the x-axis (in 2d space).
     */
    public float normalize();

    /**
     * <p><code>this = ^this</code></p>
     * Sets this Vector to it's normal and returns this.
     */
    public V normali();

    /**
     * <p><code>out = ^this</code></p>
     * Sets out to the normal of this Vector and returns out.
     */
    public V normal( V out );

    /**
     * <p><code>new (^this)</code></p>
     * Returns a new Vector which is the normal of this Vector.
     */
    public V normal();

    /**
     * <p><code>length of this == 1</code></p>
     * Determines whether this Vector is an exact unit V.
     */
    public boolean isUnit();

    /**
     * <p><code>length of this = 1 within epsilon</code></p>
     * Determines whether this Vector is a unit V within epsilon.
     */
    public boolean isUnit( float epsilon );

    /**
     * Returns the squared length of this Vector.
     */
    public float lengthSq();

    /**
     * Returns the length of this Vector.
     */
    public float length();

    /**
     * Returns the dot product between this Vector and v.
     */
    public float dot( V v );

    /**
     * Returns the squared distance between this Vector and v.
     */
    public float distanceSq( V v );

    /**
     * Returns the distance between this Vector and v.
     */
    public float distance( V v );

    /**
     * Returns whether the given V is perfectly parallel to this Vector.
     */
    public boolean isParallel( V v );

    /**
     * Returns whether the given V is parallel to this Vector within
     * epsilon.
     */
    public boolean isParallel( V v, float epsilon );

    /**
     * Returns a reference to a Vector which is located at the origin. Returning
     * a constant is desired, therefore the returned vector should not be modified. 
     */
    public V ZERO();
    
    /**
     * <p><code>this == 0</code></p>
     * Determines whether this Vector's components are both exactly zero.
     */
    public boolean isZero();

    /**
     * <p><code>this == 0 within epsilon</code></p>
     * Determines whether this Vector's components are both at least epsilon away
     * from zero.
     */
    public boolean isZero( float epsilon );

    /**
     * <p><code>this == v</code></p>
     * Determines if this Vector is equal to v.
     */
    public boolean isEqual( V v );

    /**
     * <p><code>this == v within epsilon</code></p>
     * Determines if this Vector is equal to v within epsilon.
     */
    public boolean isEqual( V v, float epsilon );

    /**
     * Sets this vector to the default unit vector.
     */
    public V defaultUnit();
    
    /**
     * Returns whether this Vector lies between the two vectors given.
     */
    public boolean isBetween( V min, V max, float buffer );
    
    /**
     * Returns a new Vector that is located at zero.
     */
    public V create();

    /**
     * Clones this Vector.
     */
    public V clone();

    /**
     * Clones the given Vector and returns the newly instantiated clone.
     */
    public V clone( V value );

    /**
     * <p><code>to = from</code></p>
     * Copies from one Vector to another.
     */
    public V copy( V from, V to );

}
