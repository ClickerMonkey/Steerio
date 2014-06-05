
package org.magnos.steer.vec;

import org.magnos.steer.SteerMath;

/**
 * A 2d {@link Vec} implementation.
 * 
 * Unique properties of 2d unit (normalized) Vectors:
 * <ol>
 * <li>x = cos(A) where A is the angle of the Vector (returned by
 * {@link #angle()}).</li>
 * <li>y = sin(A) where A is the angle of the Vector (returned by
 * {@link #angle()}).</li>
 * <li>passing in {@link #angle()} to {@link #rotatei(float)} is the same as
 * passing the Vector into {@link #rotatei(V)} because of the two
 * properties mentioned above except the latter method is quicker since
 * {@link Math#cos(double)} and {@link Math#sin(double)} don't need to be
 * called.</li>
 * </ol>
 * 
 * @author Philip Diffenderfer
 *
 */
public class Vec2 extends AbstractVec<Vec2>
{

    /**
     * Returns a vector with all components set to zero. If this is directly
     * modified or passed to a function that may modify it, it will change for
     * all references of this value. This should strictly be used as a constant.
     */
    public static final Vec2 FACTORY = new Vec2( 0, 0 );

    /**
     * Returns a vector with all components set to zero. If this is directly
     * modified or passed to a function that may modify it, it will change for
     * all references of this value. This should strictly be used as a constant.
     */
    public static final Vec2 ZERO = new Vec2( 0, 0 );

    /**
     * Returns a vector with all components set to one. If this is directly
     * modified or passed to a function that may modify it, it will change for
     * all references of this value. This should strictly be used as a constant.
     */
    public static final Vec2 ONE = new Vec2( 1, 1 );

    /**
     * Returns a unit vector along the x-axis in the positive direction.
     */
    public static final Vec2 RIGHT = new Vec2( 1, 0 );

    /**
     * Returns a unit vector along the x-axis in the negative direction.
     */
    public static final Vec2 LEFT = new Vec2(-1, 0 );

    /*
     * Returns a unit vector along the y-axis in the positive direction.
     */
    public static final Vec2 TOP = new Vec2( 0, 1 );

    /**
     * Returns a unit vector along the y-axis in the negative direction.
     */
    public static final Vec2 BOTTOM = new Vec2( 0, -1 );

    /**
     * Constant used to fix the angle returned by {@link #angle()} and {@link #angleTo(Vec2)}.
     */
    private static final float ANGLE_FIX = (float)(Math.PI * 2.0f);

    /**
     * The x-coordinate of the Vector.
     */
    public float x;

    /**
     * The y-coordinate of the Vector.
     */
    public float y;

    /**
     * Instantiates a new Vector at the origin.
     */
    public Vec2()
    {
    }

    /**
     * Instantiates a new Vector at the specified coordinates.
     * 
     * @param x
     *        The initial x-coordinate of the vector.
     * @param y
     *        The initial y-coordinate of the vector.
     */
    public Vec2( float x, float y )
    {
        set( x, y );
    }

    /**
     * Instantiates a new Vector based on another Vector.
     * 
     * @param v
     *        The vector to copy x and y coordinates from.
     */
    public Vec2( Vec2 v )
    {
        set( v );
    }

    /**
     * Sets the coordinates of this vector and returns this.
     */
    public Vec2 set( float x, float y )
    {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public int size()
    {
        return 2;
    }
    
    @Override
    public float getComponent(int component)
    {
        switch (component) {
        case 0: return x;
        case 1: return y;
        }
        
        return 0;
    }
    
    @Override
    public void setComponent(int component, float value)
    {
        switch (component) {
        case 0: x = value; break;
        case 1: y = value; break;
        }
    }
    
    @Override
    public Vec2 set( Vec2 v )
    {
        x = v.x;
        y = v.y;
        return this;
    }

    @Override
    public void clear()
    {
        x = y = 0.0f;
    }

    @Override
    public Vec2 clear( float value )
    {
        x = y = value;
        
        return this;
    }

    @Override
    public Vec2 neg( Vec2 out )
    {
        out.x = -x;
        out.y = -y;
        return out;
    }

    @Override
    public Vec2 abs( Vec2 out )
    {
        out.x = x < 0 ? -x : x;
        out.y = y < 0 ? -y : y;

        return out;
    }

    @Override
    public Vec2 mul( float s, Vec2 out )
    {
        out.x = s * x;
        out.y = s * y;
        return out;
    }

    @Override
    public Vec2 div( float s, Vec2 out )
    {
        if ( s != 0.0f )
        {
            s = 1.0f / s;

            out.x = x * s;
            out.y = y * s;
        }
        return out;
    }

    @Override
    public Vec2 add( float s, Vec2 out )
    {
        out.x = x + s;
        out.y = y + s;
        return out;
    }

    @Override
    public Vec2 mul( Vec2 v, Vec2 out )
    {
        out.x = x * v.x;
        out.y = y * v.y;
        return out;
    }

    @Override
    public Vec2 div( Vec2 v, Vec2 out )
    {
        if ( v.x != 0.0f )
        {
            out.x = x / v.x;
        }
        if ( v.y != 0.0f )
        {
            out.y = y / v.y;
        }
        return out;
    }

    @Override
    public Vec2 add( Vec2 v, Vec2 out )
    {
        out.x = x + v.x;
        out.y = y + v.y;
        return out;
    }

    @Override
    public Vec2 adds( Vec2 v, float s, Vec2 out )
    {
        out.x = x + v.x * s;
        out.y = y + v.y * s;
        return out;
    }

    @Override
    public Vec2 sub( Vec2 v, Vec2 out )
    {
        out.x = x - v.x;
        out.y = y - v.y;
        return out;
    }
    
    @Override
    public Vec2 mod( float s, Vec2 out )
    {
        out.x = x % s;
        out.y = y % s;
        return out;
    }

    @Override
    public Vec2 mod( Vec2 v, Vec2 out )
    {
        out.x = x % v.x;
        out.y = y % v.y;
        return out;
    }

    @Override
    public Vec2 direct( Vec2 origin, Vec2 target, Vec2 out )
    {
        out.x = target.x - origin.x;
        out.y = target.y - origin.y;
        return out;
    }

    @Override
    public Vec2 interpolate( Vec2 start, Vec2 end, float delta, Vec2 out )
    {
        out.x = (end.x - start.x) * delta + start.x;
        out.y = (end.y - start.y) * delta + start.y;

        return out;
    }

    @Override
    public Vec2 interpolateTo( Vec2 end, float delta, Vec2 out )
    {
        out.x = (end.x - x) * delta + x;
        out.y = (end.y - y) * delta + y;

        return out;
    }
    
    @Override
    public Vec2 clamp( Vec2 min, Vec2 max, Vec2 out )
    {
        out.x = SteerMath.clamp( x, min.x, max.x );
        out.y = SteerMath.clamp( y, min.y, max.y );
        
        return out;
    }

    /**
     * Sets this to the vector with the given angle in radians with the given
     * magnitude, and returns this.
     */
    public Vec2 angle( float radians, float magnitude )
    {
        x = (float)Math.cos( radians ) * magnitude;
        y = (float)Math.sin( radians ) * magnitude;

        return this;
    }

    /**
     * Returns the angle in radians of this vector from the x-axis.
     */
    public float angle()
    {
        float a = (float)StrictMath.atan2( y, x );

        if ( a < 0 )
        {
            a += ANGLE_FIX;
        }

        return a;
    }

    /**
     * Returns the angle in radians that's between this vector and the given
     * vector and the x-axis.
     */
    public float angleTo( Vec2 to )
    {
        float a = (float)StrictMath.atan2( to.y - y, to.x - x );

        if ( a < 0 )
        {
            a += ANGLE_FIX;
        }

        return a;
    }

    @Override
    public float lengthSq()
    {
        return x * x + y * y;
    }

    @Override
    public float length()
    {
        return (float)Math.sqrt( x * x + y * y );
    }

    /**
     * Rotates this vector by the given radians and returns this.
     */
    public Vec2 rotatei( float radians )
    {
        return rotate( radians, this );
    }

    /**
     * Sets out to this vector rotated by the given radians and returns out.
     */
    public Vec2 rotate( float radians, Vec2 out )
    {
        float c = (float)Math.cos( radians );
        float s = (float)Math.sin( radians );

        float xp = x * c - y * s;
        float yp = x * s + y * c;

        out.x = xp;
        out.y = yp;
        return out;
    }

    /**
     * Returns a new vector that is this vector rotated by the given radians.
     */
    public Vec2 rotate( float radians )
    {
        return rotate( radians, new Vec2() );
    }

    @Override
    public Vec2 rotate( Vec2 cossin, Vec2 out )
    {
        final float ox = x, oy = y;
        out.x = (cossin.x * ox - cossin.y * oy);
        out.y = (cossin.x * oy + cossin.y * ox);
        return out;
    }

    @Override
    public Vec2 unrotate( Vec2 cossin, Vec2 out )
    {
        final float ox = x, oy = y;
        out.x = (cossin.x * ox + cossin.y * oy);
        out.y = (cossin.x * oy - cossin.y * ox);
        return out;
    }

    @Override
    public Vec2 floor( Vec2 out )
    {
        out.x = (float)Math.floor( x );
        out.y = (float)Math.floor( y );
        
        return out;
    }

    @Override
    public Vec2 ceil( Vec2 out )
    {
        out.x = (float)Math.ceil( x );
        out.y = (float)Math.ceil( y );
        
        return out;
    }

    @Override
    public Vec2 invert( Vec2 out )
    {
        out.x = x == 0.0f ? 0.0f : 1.0f / x;
        out.y = y == 0.0f ? 0.0f : 1.0f / y;
        return out;
    }
    
    /**
     * Rotates this vector around the origin the given number of times and
     * returns this.
     */
    public Vec2 rotate90i( int times )
    {
        return rotate90( times, ZERO, this );
    }

    /**
     * Rotates this vector around the given origin the given number of times and
     * returns this.
     */
    public Vec2 rotate90i( int times, Vec2 origin )
    {
        return rotate90( times, origin, this );
    }

    /**
     * Sets out to this vector rotated around the given origin a given number of
     * times and returns out.
     */
    public Vec2 rotate90( int times, Vec2 origin, Vec2 out )
    {
        float dx = x - origin.x;
        float dy = y - origin.y;

        switch ( times & 3 )
        {
        case 0:
            out.x = x;
            out.y = y;
            break;
        case 1:
            out.x = x - dy;
            out.y = y + dx;
            break;
        case 2:
            out.x = x - dx;
            out.y = y - dy;
            break;
        case 3:
            out.x = x + dy;
            out.y = y - dy;
            break;
        }

        return out;
    }

    /**
     * Returns a new vector rotated around the given origin a given number of
     * times.
     */
    public Vec2 rotate90( int times )
    {
        return rotate90( times, ZERO, new Vec2() );
    }

    /**
     * Returns a new vector rotated around the given origin a given number of
     * times.
     */
    public Vec2 rotate90( int times, Vec2 origin )
    {
        return rotate90( times, origin, new Vec2() );
    }

    @Override
    public Vec2 mini( Vec2 a, Vec2 b )
    {
        return min( a, b, this );
    }

    @Override
    public Vec2 maxi( Vec2 a, Vec2 b )
    {
        return max( a, b, this );
    }

    /**
     * Sets this vector to it's tangent on the left side.
     */
    public Vec2 lefti()
    {
        return left( this );
    }

    /**
     * Sets out to the tangent of this vector on the left side.
     */
    public Vec2 left( Vec2 out )
    {
        float oldx = x;
        out.x = -y;
        out.y = oldx;
        return out;
    }

    /**
     * Returns a new vector that is the tangent of this vector on the left side.
     */
    public Vec2 left()
    {
        return left( new Vec2() );
    }

    /**
     * Sets this vector to it's tangent on the right side.
     */
    public Vec2 righti()
    {
        return right( this );
    }

    /**
     * Sets out to the tangent of this vector on the right side.
     */
    public Vec2 right( Vec2 out )
    {
        float oldx = x;
        out.x = y;
        out.y = -oldx;
        return out;
    }

    /**
     * Returns a new vector that is the tangent of this vector on the right side.
     */
    public Vec2 right()
    {
        return right( new Vec2() );
    }

    @Override
    public float dot( Vec2 v )
    {
        return dot( this, v );
    }

    @Override
    public float distanceSq( Vec2 v )
    {
        return distanceSq( this, v );
    }

    @Override
    public float distance( Vec2 v )
    {
        return distance( this, v );
    }

    /**
     * Sets this vector to the cross between v and a and returns this.
     */
    public Vec2 cross( Vec2 v, float a )
    {
        return cross( v, a, this );
    }

    /**
     * Sets this vector to the cross between a and v and returns this.
     */
    public Vec2 cross( float a, Vec2 v )
    {
        return cross( a, v, this );
    }

    /**
     * Returns the scalar cross between this vector and v. This is essentially
     * the length of the cross product if this vector were 3d. This can also
     * indicate which way v is facing relative to this vector (left or right).
     */
    public float cross( Vec2 v )
    {
        return cross( this, v );
    }

    /**
     * Returns the scalar cross between this vector and v. This is essentially
     * the length of the cross product if this vector were 3d. This can also
     * indicate which way v is facing relative to this vector (left or right).
     */
    public float cross( float vx, float vy )
    {
        return cross( x, y, vx, vy );
    }

    @Override
    public boolean isParallel( Vec2 v, float epsilon )
    {
        return Math.abs( cross( v ) ) < epsilon;
    }

    /**
     * Clones this vector.
     */
    public Vec2 clone()
    {
        return new Vec2( x, y );
    }
    
    @Override
    public Vec2 ZERO()
    {
        return ZERO;
    }

    @Override
    public boolean isZero( float epsilon )
    {
        return isEqual( 0, 0, epsilon );
    }

    /**
     * Determines if this vector is equal to the vector {xx, yy}.
     */
    public boolean isEqual( float xx, float yy )
    {
        return (x == xx && y == yy);
    }

    @Override
    public boolean isEqual( Vec2 v, float epsilon )
    {
        return isEqual( v.x, v.y, epsilon );
    }

    /**
     * Determines if this vector is equal to the vector {xx, yy} within epsilon.
     */
    public boolean isEqual( float xx, float yy, float epsilon )
    {
        return Math.abs( xx - x ) < epsilon && Math.abs( yy - y ) < epsilon;
    }

    @Override
    public Vec2 defaultUnit()
    {
        return set( 1.0f, 0.0f );
    }

    @Override
    public boolean isBetween( Vec2 min, Vec2 max, float buffer )
    {
        return !(x < min.x + buffer || x > max.x - buffer || y < min.y + buffer || y > max.y - buffer);
    }

    @Override
    public Vec2 create()
    {
        return new Vec2();
    }

    @Override
    public Vec2 clone( Vec2 value )
    {
        return new Vec2( value );
    }

    @Override
    public Vec2 copy( Vec2 from, Vec2 to )
    {
        to.set( from );
        return to;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits( x );
        result = prime * result + Float.floatToIntBits( y );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Vec2 other = (Vec2)obj;
        if ( Float.floatToIntBits( x ) != Float.floatToIntBits( other.x ) ) return false;
        if ( Float.floatToIntBits( y ) != Float.floatToIntBits( other.y ) ) return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "{" + x + "," + y + "}";
    }

    /**
     * Returns and sets out to the minimum x and y coordinates from a and b.
     */
    public static Vec2 min( Vec2 a, Vec2 b, Vec2 out )
    {
        out.x = StrictMath.min( a.x, b.x );
        out.y = StrictMath.min( a.y, b.y );
        return out;
    }

    /**
     * Returns and sets out to the maximum x and y coordinates from a and b.
     */
    public static Vec2 max( Vec2 a, Vec2 b, Vec2 out )
    {
        out.x = StrictMath.max( a.x, b.x );
        out.y = StrictMath.max( a.y, b.y );
        return out;
    }

    /**
     * Return the dot product between the two vectors.
     */
    public static float dot( Vec2 a, Vec2 b )
    {
        return a.x * b.x + a.y * b.y;
    }

    /**
     * Return the distance (squared) between the two points.
     */
    public static float distanceSq( Vec2 a, Vec2 b )
    {
        float dx = a.x - b.x;
        float dy = a.y - b.y;

        return dx * dx + dy * dy;
    }

    /**
     * Return the distance between the two points.
     */
    public static float distance( Vec2 a, Vec2 b )
    {
        float dx = a.x - b.x;
        float dy = a.y - b.y;

        return (float)Math.sqrt( dx * dx + dy * dy );
    }

    /**
     * Returns and sets out to the cross between v and a.
     */
    public static Vec2 cross( Vec2 v, float a, Vec2 out )
    {
        out.x = v.y * a;
        out.y = v.x * -a;
        return out;
    }

    /**
     * Returns and sets out to the cross between a and v.
     */
    public static Vec2 cross( float a, Vec2 v, Vec2 out )
    {
        out.x = v.y * -a;
        out.y = v.x * a;
        return out;
    }

    /**
     * Returns the cross product between the two vectors a and b.
     */
    public static float cross( Vec2 a, Vec2 b )
    {
        return a.x * b.y - a.y * b.x;
    }

    /**
     * Returns the cross product between the two vectors {ax, ay} and {bx, by}.
     */
    public static float cross( float ax, float ay, float bx, float by )
    {
        return ax * by - ay * bx;
    }

    /**
     * Returns a vector that represents the given angle, where x = cos(angle) and y = sin(angle).
     */
    public static Vec2 fromAngle( float angle )
    {
        return new Vec2( (float)Math.cos( angle ), (float)Math.sin( angle ) );
    }

    /**
     * Returns a new array of instantiated Vectors of the given length.
     */
    public static Vec2[] arrayOf( int length )
    {
        Vec2[] array = new Vec2[length];

        while ( --length >= 0 )
        {
            array[length] = new Vec2();
        }

        return array;

    }

}
