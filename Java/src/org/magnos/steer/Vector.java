
package org.magnos.steer;



public class Vector implements Target
{

	public float x, y;

	public Vector()
	{
	}

	public Vector( float x, float y )
	{
		set( x, y );
	}

	public Vector( Vector v )
	{
		set( v );
	}

	public Vector set( float x, float y )
	{
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector set( Vector v )
	{
		x = v.x;
		y = v.y;
		return this;
	}

	/**
	 * Negates this vector and returns this.
	 */
	public Vector negi()
	{
		return neg( this );
	}

	/**
	 * Sets out to the negation of this vector and returns out.
	 */
	public Vector neg( Vector out )
	{
		out.x = -x;
		out.y = -y;
		return out;
	}

	/**
	 * Returns a new vector that is the negation to this vector.
	 */
	public Vector neg()
	{
		return neg( new Vector() );
	}

	/**
	 * Multiplies this vector by s and returns this.
	 */
	public Vector muli( float s )
	{
		return mul( s, this );
	}

	/**
	 * Sets out to this vector multiplied by s and returns out.
	 */
	public Vector mul( float s, Vector out )
	{
		out.x = s * x;
		out.y = s * y;
		return out;
	}

	/**
	 * Returns a new vector that is a multiplication of this vector and s.
	 */
	public Vector mul( float s )
	{
		return mul( s, new Vector() );
	}

	/**
	 * Divides this vector by s and returns this.
	 */
	public Vector divi( float s )
	{
		return div( s, this );
	}

	/**
	 * Sets out to the division of this vector and s and returns out.
	 */
	public Vector div( float s, Vector out )
	{
		if ( s != 0.0f )
		{
			out.x = x / s;
			out.y = y / s;	
		}
		return out;
	}

	/**
	 * Returns a new vector that is a division between this vector and s.
	 */
	public Vector div( float s )
	{
		return div( s, new Vector() );
	}

	/**
	 * Adds s to this vector and returns this.
	 */
	public Vector addi( float s )
	{
		return add( s, this );
	}

	/**
	 * Sets out to the sum of this vector and s and returns out.
	 */
	public Vector add( float s, Vector out )
	{
		out.x = x + s;
		out.y = y + s;
		return out;
	}

	/**
	 * Returns a new vector that is the sum between this vector and s.
	 */
	public Vector add( float s )
	{
		return add( s, new Vector() );
	}

	/**
	 * Multiplies this vector by v and returns this.
	 */
	public Vector muli( Vector v )
	{
		return mul( v, this );
	}

	/**
	 * Sets out to the product of this vector and v and returns out.
	 */
	public Vector mul( Vector v, Vector out )
	{
		out.x = x * v.x;
		out.y = y * v.y;
		return out;
	}

	/**
	 * Returns a new vector that is the product of this vector and v.
	 */
	public Vector mul( Vector v )
	{
		return mul( v, new Vector() );
	}

	/**
	 * Divides this vector by v and returns this.
	 */
	public Vector divi( Vector v )
	{
		return div( v, this );
	}

	/**
	 * Sets out to the division of this vector and v and returns out.
	 */
	public Vector div( Vector v, Vector out )
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

	/**
	 * Returns a new vector that is the division of this vector by v.
	 */
	public Vector div( Vector v )
	{
		return div( v, new Vector() );
	}

	/**
	 * Adds v to this vector and returns this.
	 */
	public Vector addi( Vector v )
	{
		return add( v, this );
	}

	/**
	 * Sets out to the addition of this vector and v and returns out.
	 */
	public Vector add( Vector v, Vector out )
	{
		out.x = x + v.x;
		out.y = y + v.y;
		return out;
	}

	/**
	 * Returns a new vector that is the addition of this vector and v.
	 */
	public Vector add( Vector v )
	{
		return add( v, new Vector() );
	}

	/**
	 * Adds v * s to this vector and returns this.
	 */
	public Vector addsi( Vector v, float s )
	{
		return adds( v, s, this );
	}

	/**
	 * Sets out to the addition of this vector and v * s and returns out.
	 */
	public Vector adds( Vector v, float s, Vector out )
	{
		out.x = x + v.x * s;
		out.y = y + v.y * s;
		return out;
	}

	/**
	 * Returns a new vector that is the addition of this vector and v * s.
	 */
	public Vector adds( Vector v, float s )
	{
		return adds( v, s, new Vector() );
	}

	/**
	 * Subtracts v from this vector and returns this.
	 */
	public Vector subi( Vector v )
	{
		return sub( v, this );
	}

	/**
	 * Sets out to the subtraction of v from this vector and returns out.
	 */
	public Vector sub( Vector v, Vector out )
	{
		out.x = x - v.x;
		out.y = y - v.y;
		return out;
	}

	/**
	 * Returns a new vector that is the subtraction of v from this vector.
	 */
	public Vector sub( Vector v )
	{
		return sub( v, new Vector() );
	}

	public Vector directi( Vector origin, Vector target )
	{
		return direct( origin, target, this );
	}

	public Vector direct( Vector origin, Vector target, Vector out )
	{
		out.x = target.x - origin.x;
		out.y = target.y - origin.y;
		return out;
	}

	public Vector direct( Vector origin, Vector target )
	{
		return direct( origin, target, new Vector() );
	}
	
	public void interpolate( Vector start, Vector end, float delta )
	{
		x = (end.x - start.x) * delta + start.x;
		y = (end.y - start.y) * delta + start.y;
	}

	/**
	 * Returns the squared length of this vector.
	 */
	public float lengthSq()
	{
		return x * x + y * y;
	}

	/**
	 * Returns the length of this vector.
	 */
	public float length()
	{
		return SteerMath.sqrt( x * x + y * y );
	}

	public float length( float length )
	{
		float sq = (x * x) + (y * y);
		float actual = length;

		if (sq != 0.0 && sq != length * length)
		{
			actual = SteerMath.sqrt( sq );
			muli( length / actual );
		}

		return actual;
	}

	public Vector clone()
	{
		return new Vector( x, y );
	}

	public void clear()
	{
		x = y = 0.0f;
	}

	public boolean isZero()
	{
		return (x == 0f && y == 0f);
	}

	public boolean isZero( float epsilon )
	{
		return SteerMath.equals( x, 0.0f, epsilon ) &&
				 SteerMath.equals( y, 0.0f, epsilon );
	}

	public Vector clamp( float min, float max )
	{
		float sq = (x * x) + (y * y);

		if (sq != 0)
		{
			if (sq < min * min)
			{
				muli( min / SteerMath.sqrt( sq ) );
			}
			else if (sq > max * max)
			{
				muli( max / SteerMath.sqrt( sq ) );
			}
		}
		return this;
	}

	public Vector min( float min )
	{
		float sq = (x * x) + (y * y);

		if (sq != 0 && sq < min * min)
		{
			muli( min / SteerMath.sqrt( sq ) );
		}
		return this;
	}

	public Vector max( float max )
	{
		float sq = (x * x) + (y * y);

		if (sq != 0 && sq > max * max)
		{
			muli( max / SteerMath.sqrt( sq ) );
		}
		return this;
	}

	public Vector angle( float radians, float magnitude )
	{
		x = SteerMath.cos( radians ) * magnitude;
		y = SteerMath.sin( radians ) * magnitude;
		return this;
	}

	public float angle()
	{
		float a = (float)StrictMath.atan2( y, x );
		
		if (a < 0)
		{
			a = SteerMath.PI2 + a;
		}
		return a;
	}

	public float angleTo( Vector to )
	{
		float a = (float)StrictMath.atan2( to.y - y, to.x - x );
		
		if (a < 0)
		{
			a = SteerMath.PI2 + a;
		}
		return a;
	}

	/**
	 * Rotates this vector by the given radians.
	 */
	public void rotate( float radians )
	{
		float c = SteerMath.cos( radians );
		float s = SteerMath.sin( radians );

		float xp = x * c - y * s;
		float yp = x * s + y * c;

		x = xp;
		y = yp;
	}

	/**
	 * Normalizes this vector, making it a unit vector. A unit vector has a
	 * length of 1.0.
	 */
	public void normalize()
	{
		float lenSq = lengthSq();

		if (lenSq != 0.0f)
		{
			float invLen = 1.0f / SteerMath.sqrt( lenSq );
			x *= invLen;
			y *= invLen;
		}
	}

	/**
	 * Sets this vector to the minimum between a and b.
	 */
	public Vector mini( Vector a, Vector b )
	{
		return min( a, b, this );
	}

	/**
	 * Sets this vector to the maximum between a and b.
	 */
	public Vector maxi( Vector a, Vector b )
	{
		return max( a, b, this );
	}

	/**
	 * Returns the dot product between this vector and v.
	 */
	public float dot( Vector v )
	{
		return dot( this, v );
	}

	/**
	 * Returns the squared distance between this vector and v.
	 */
	public float distanceSq( Vector v )
	{
		return distanceSq( this, v );
	}

	/**
	 * Returns the distance between this vector and v.
	 */
	public float distance( Vector v )
	{
		return distance( this, v );
	}

	/**
	 * Sets this vector to the cross between v and a and returns this.
	 */
	public Vector cross( Vector v, float a )
	{
		return cross( v, a, this );
	}

	/**
	 * Sets this vector to the cross between a and v and returns this.
	 */
	public Vector cross( float a, Vector v )
	{
		return cross( a, v, this );
	}

	/**
	 * Returns the scalar cross between this vector and v. This is essentially
	 * the length of the cross product if this vector were 3d. This can also
	 * indicate which way v is facing relative to this vector.
	 */
	public float cross( Vector v )
	{
		return cross( this, v );
	}

	@Override
	public Vector getTarget( SteerSubject subject )
	{
		return this;
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
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Vector other = (Vector)obj;
		if (Float.floatToIntBits( x ) != Float.floatToIntBits( other.x )) return false;
		if (Float.floatToIntBits( y ) != Float.floatToIntBits( other.y )) return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "{" + x + "," + y + "}";
	}

	public static Vector min( Vector a, Vector b, Vector out )
	{
		out.x = (float)StrictMath.min( a.x, b.x );
		out.y = (float)StrictMath.min( a.y, b.y );
		return out;
	}

	public static Vector max( Vector a, Vector b, Vector out )
	{
		out.x = (float)StrictMath.max( a.x, b.x );
		out.y = (float)StrictMath.max( a.y, b.y );
		return out;
	}

	public static float dot( Vector a, Vector b )
	{
		return a.x * b.x + a.y * b.y;
	}

	public static float distanceSq( Vector a, Vector b )
	{
		float dx = a.x - b.x;
		float dy = a.y - b.y;

		return dx * dx + dy * dy;
	}

	public static float distance( Vector a, Vector b )
	{
		float dx = a.x - b.x;
		float dy = a.y - b.y;

		return SteerMath.sqrt( dx * dx + dy * dy );
	}

	public static Vector cross( Vector v, float a, Vector out )
	{
		out.x = v.y * a;
		out.y = v.x * -a;
		return out;
	}

	public static Vector cross( float a, Vector v, Vector out )
	{
		out.x = v.y * -a;
		out.y = v.x * a;
		return out;
	}

	public static float cross( Vector a, Vector b )
	{
		return a.x * b.y - a.y * b.x;
	}

	/**
	 * Returns an array of allocated Vec2 of the requested length.
	 */
	public static Vector[] arrayOf( int length )
	{
		Vector[] array = new Vector[length];

		while (--length >= 0)
		{
			array[length] = new Vector();
		}

		return array;
	}

}
