
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
 * <li>x = cos(A) where A is the angle of the Vector (returned by
 * {@link #angle()}).</li>
 * <li>y = sin(A) where A is the angle of the Vector (returned by
 * {@link #angle()}).</li>
 * <li>passing in {@link #angle()} to {@link #rotatei(float)} is the same as
 * passing the Vector into {@link #rotatei(V)} because of the two
 * properties mentioned above except the latter method is quicker since
 * {@link Math#cos(double)} and {@link Math#sin(double)} don't need to be
 * called.</li>
 * <li>It's an efficient way of storing an angle.</li>
 * <li>Can be used as the normal parameter in {@link #reflect(V)} methods.</li>
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
     * Sets the coordinates of this Vector and returns this.
     */
    public V set( V v );

    /**
     * Clears this Vector's components by setting them to zero.
     */
    public void clear();

    /**
     * Clears this Vector's components by setting them to the given value.
     */
    public V clear( float value );

    /**
     * Negates this Vector and returns this.
     */
    public V negi();

    /**
     * Sets out to the negation of this Vector and returns out.
     */
    public V neg( V out );

    /**
     * Returns a new Vector that is the negation to this Vector.
     */
    public V neg();

    /**
     * Sets this Vector to it's absolute value and returns this.
     */
    public V absi();

    /**
     * Sets out to the absolute value of this Vector and returns out.
     */
    public V abs( V out );

    /**
     * Returns a new Vector that is the absolute value of this Vector.
     */
    public V abs();

    /**
     * Multiplies this Vector by s and returns this.
     */
    public V muli( float s );

    /**
     * Sets out to this Vector multiplied by s and returns out.
     */
    public V mul( float s, V out );

    /**
     * Returns a new Vector that is a multiplication of this Vector and s.
     */
    public V mul( float s );

    /**
     * Divides this Vector by s and returns this.
     */
    public V divi( float s );

    /**
     * Sets out to the division of this Vector and s and returns out.
     */
    public V div( float s, V out );

    /**
     * Returns a new Vector that is a division between this Vector and s.
     */
    public V div( float s );

    /**
     * Adds s to this Vector and returns this.
     */
    public V addi( float s );

    /**
     * Sets out to the sum of this Vector and s and returns out.
     */
    public V add( float s, V out );

    /**
     * Returns a new Vector that is the sum between this Vector and s.
     */
    public V add( float s );

    /**
     * Multiplies this Vector by v and returns this.
     */
    public V muli( V v );

    /**
     * Sets out to the product of this Vector and v and returns out.
     */
    public V mul( V v, V out );

    /**
     * Returns a new Vector that is the product of this Vector and v.
     */
    public V mul( V v );

    /**
     * Divides this Vector by v and returns this.
     */
    public V divi( V v );

    /**
     * Sets out to the division of this Vector and v and returns out.
     */
    public V div( V v, V out );

    /**
     * Returns a new Vector that is the division of this Vector by v.
     */
    public V div( V v );

    /**
     * Adds v to this Vector and returns this.
     */
    public V addi( V v );

    /**
     * Sets out to the addition of this Vector and v and returns out.
     */
    public V add( V v, V out );

    /**
     * Returns a new Vector that is the addition of this Vector and v.
     */
    public V add( V v );

    /**
     * Adds v * s to this Vector and returns this.
     */
    public V addsi( V v, float s );

    /**
     * Sets out to the addition of this Vector and v * s and returns out.
     */
    public V adds( V v, float s, V out );

    /**
     * Returns a new Vector that is the addition of this Vector and v * s.
     */
    public V adds( V v, float s );

    /**
     * Subtracts v from this Vector and returns this.
     */
    public V subi( V v );

    /**
     * Sets out to the subtraction of v from this Vector and returns out.
     */
    public V sub( V v, V out );

    /**
     * Returns a new Vector that is the subtraction of v from this Vector.
     */
    public V sub( V v );

    /**
     * Sets this to the V starting at origin and ending at target, and
     * returns this.
     */
    public V directi( V origin, V target );

    /**
     * Sets out to the V starting at origin and ending at target, and
     * returns out.
     */
    public V direct( V origin, V target, V out );

    /**
     * Returns a new Vector starting at origin and ending at target.
     */
    public V direct( V origin, V target );

    /**
     * Sets this to the V between start and end based on delta where delta
     * is 0.0 for the start, 0.5 for the middle, and 1.0 for the end, and returns
     * this.
     */
    public V interpolatei( V start, V end, float delta );

    /**
     * Sets out to the V between start and end based on delta where delta is
     * 0.0 for the start, 0.5 for the middle, and 1.0 for the end, and returns
     * out.
     */
    public V interpolate( V start, V end, float delta, V out );

    /**
     * Returns a new Vector between start and end based on delta where delta is
     * 0.0 for the start, 0.5 for the middle, and 1.0 for the end.
     */
    public V interpolate( V start, V end, float delta );

    /**
     * Determines whether this Vector is an exact unit V.
     */
    public boolean isUnit();

    /**
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
     * Returns a new Vector that is this Vector with floored components.
     */
    public V floor();

    /**
     * Floors the components of this Vector and returns this.
     */
    public V floori();

    /**
     * Sets out to this Vector with its components floored and returns out.
     */
    public V floor( V out );

    /**
     * Returns a new Vector that is this Vector with ceiling components.
     */
    public V ceil();

    /**
     * Ceilings the components of this Vector and returns this.
     */
    public V ceili();

    /**
     * Sets out to this Vector with its components ceiling and returns out.
     */
    public V ceil( V out );
    
    /**
     * Reflects this Vector across the normal and returns this.
     */
    public V inverti();

    /**
     * Sets out to this Vector reflected across the normal and returns out.
     */
    public V invert( V out );
    
    /**
     * Returns a new Vector that is this Vector reflected across the normal.
     */
    public V invert();

    /**
     * Reflects this Vector across the normal and returns this.
     */
    public V reflecti( V normal );

    /**
     * Sets out to this Vector reflected across the normal and returns out.
     */
    public V reflect( V normal, V out );

    /**
     * Returns a new Vector that is this Vector reflected across the normal.
     */
    public V reflect( V normal );

    /**
     * Reflects this Vector across the normal and returns this.
     */
    public V refracti( V normal );

    /**
     * Sets out to this Vector reflected across the normal and returns out.
     */
    public V refract( V normal, V out );

    /**
     * Returns a new Vector that is this Vector reflected across the normal.
     */
    public V refract( V normal );

    /**
     * Normalizes this Vector into a unit Vector and returns the length. A unit
     * Vector has a length of 1.0 and the x-component represents COS(A) and the
     * y-component represents SIN(A) where A is the angle between this Vector and
     * the x-axis (in 2d space).
     */
    public float normalize();

    /**
     * Sets this Vector to it's normal and returns this.
     */
    public V normali();

    /**
     * Sets out to the normal of this Vector and returns out.
     */
    public V normal( V out );

    /**
     * Returns a new Vector which is the normal of this Vector.
     */
    public V normal();

    /**
     * Sets this Vector to the minimum between a and b.
     */
    public V mini( V a, V b );

    /**
     * Sets this Vector to the maximum between a and b.
     */
    public V maxi( V a, V b );

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
     * Clones this Vector.
     */
    public V clone();

    /**
     * Determines whether this Vector's components are both exactly zero.
     */
    public boolean isZero();

    /**
     * Determines whether this Vector's components are both at least epsilon away
     * from zero.
     */
    public boolean isZero( float epsilon );

    /**
     * Determines if this Vector is equal to v.
     */
    public boolean isEqual( V v );

    /**
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
     * Clones the given Vector and returns the newly instantiated clone.
     */
    public V clone( V value );

    /**
     * Copies from one Vector to another.
     */
    public V copy( V from, V to );

}
