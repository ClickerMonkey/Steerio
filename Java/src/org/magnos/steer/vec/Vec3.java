
package org.magnos.steer.vec;

import org.magnos.steer.SteerMath;

/**
 * A 3d {@link Vec} implementation.
 * 
 * @author Philip Diffenderfer
 *
 */
public class Vec3 extends AbstractVec<Vec3> 
{

   /**
    * Returns a vector with all components set to zero. If this is directly
    * modified or passed to a function that may modify it, it will change for
    * all references of this value. This should strictly be used as a constant.
    */
   public static final Vec3 ZERO = new Vec3( 0, 0, 0 );

   /**
    * Returns a vector with all components set to one. If this is directly
    * modified or passed to a function that may modify it, it will change for
    * all references of this value. This should strictly be used as a constant.
    */
   public static final Vec3 ONE = new Vec3( 1, 1, 1 );

   /**
    * Returns a unit vector along the x-axis in the positive direction.
    */
   public static final Vec3 RIGHT = new Vec3( 1, 0, 0 );

   /**
    * Returns a unit vector along the x-axis in the negative direction.
    */
   public static final Vec3 LEFT = new Vec3( 1, 0, 0 );

   /*
    * Returns a unit vector along the y-axis in the positive direction.
    */
   public static final Vec3 UP = new Vec3( 0, 1, 0 );

   /**
    * Returns a unit vector along the y-axis in the negative direction.
    */
   public static final Vec3 DOWN = new Vec3( 0, -1, 0 );

   /*
    * Returns a unit vector along the z-axis in the positive direction.
    */
   public static final Vec3 FAR = new Vec3( 0, 0, 1 );

   /**
    * Returns a unit vector along the z-axis in the negative direction.
    */
   public static final Vec3 NEAR = new Vec3( 0, 0, -1 );

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
    * The z-coordinate of the Vector.
    */
   public float z;

   /**
    * Instantiates a new Vector at the origin.
    */
   public Vec3()
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
   public Vec3( float x, float y, float z )
   {
      set( x, y, z );
   }

   /**
    * Instantiates a new Vector based on another Vector.
    * 
    * @param v
    *        The vector to copy x and y coordinates from.
    */
   public Vec3( Vec3 v )
   {
      set( v );
   }

   /**
    * Sets the coordinates of this vector and returns this.
    */
   public Vec3 set( float x, float y, float z )
   {
      this.x = x;
      this.y = y;
      this.z = z;
      return this;
   }

   @Override
   public int size()
   {
       return 3;
   }
   
   @Override
   public float getComponent(int component)
   {
       switch (component) {
       case 0: return x;
       case 1: return y;
       case 2: return z;
       }
       
       return 0;
   }
   
   @Override
   public void setComponent(int component, float value)
   {
       switch (component) {
       case 0: x = value; break;
       case 1: y = value; break;
       case 2: z = value; break;
       }
   }

   @Override
   public Vec3 set( Vec3 v )
   {
      x = v.x;
      y = v.y;
      z = v.z;
      return this;
   }

   @Override
   public void clear()
   {
      x = y = z = 0.0f;
   }

   @Override
   public Vec3 clear( float value )
   {
       x = y = z = value;
       
       return this;
   }

   @Override
   public Vec3 neg( Vec3 out )
   {
      out.x = -x;
      out.y = -y;
      out.z = -z;
      return out;
   }

   @Override
   public Vec3 abs( Vec3 out )
   {
      out.x = x < 0 ? -x : x;
      out.y = y < 0 ? -y : y;
      out.z = z < 0 ? -z : z;
      
      return out;
   }

   @Override
   public Vec3 mul( float s, Vec3 out )
   {
      out.x = s * x;
      out.y = s * y;
      out.z = s * z;
      return out;
   }

   @Override
   public Vec3 div( float s, Vec3 out )
   {
      if (s != 0.0f)
      {
         s = 1.0f / s;
         
         out.x = x * s;
         out.y = y * s;
         out.z = z * s;
      }
      return out;
   }

   @Override
   public Vec3 add( float s, Vec3 out )
   {
      out.x = x + s;
      out.y = y + s;
      out.z = z + s;
      return out;
   }

   @Override
   public Vec3 mul( Vec3 v, Vec3 out )
   {
      out.x = x * v.x;
      out.y = y * v.y;
      out.z = z * v.z;
      return out;
   }

   @Override
   public Vec3 div( Vec3 v, Vec3 out )
   {
      if (v.x != 0.0f)
      {
         out.x = x / v.x;
      }
      if (v.y != 0.0f)
      {
         out.y = y / v.y;
      }
      if (v.z != 0.0f)
      {
          out.z = z / v.z;
      }
      return out;
   }

   @Override
   public Vec3 add( Vec3 v, Vec3 out )
   {
      out.x = x + v.x;
      out.y = y + v.y;
      out.z = z + v.z;
      return out;
   }

   @Override
   public Vec3 adds( Vec3 v, float s, Vec3 out )
   {
      out.x = x + v.x * s;
      out.y = y + v.y * s;
      out.z = z + v.z * s;
      return out;
   }

   @Override
   public Vec3 sub( Vec3 v, Vec3 out )
   {
      out.x = x - v.x;
      out.y = y - v.y;
      out.z = z - v.z;
      return out;
   }
   
   @Override
   public Vec3 mod( float s, Vec3 out )
   {
       out.x = x % s;
       out.y = y % s;
       out.z = z % s;
       return out;
   }

   @Override
   public Vec3 mod( Vec3 v, Vec3 out )
   {
       out.x = x % v.x;
       out.y = y % v.y;
       out.z = z % v.z;
       return out;
   }

   @Override
   public Vec3 direct( Vec3 origin, Vec3 target, Vec3 out )
   {
      out.x = target.x - origin.x;
      out.y = target.y - origin.y;
      out.z = target.z - origin.z;
      return out;
   }

   @Override
   public Vec3 interpolate( Vec3 start, Vec3 end, float delta, Vec3 out )
   {
      out.x = (end.x - start.x) * delta + start.x;
      out.y = (end.y - start.y) * delta + start.y;
      out.z = (end.z - start.z) * delta + start.z;
      
      return out;
   }

   @Override
   public Vec3 interpolateTo( Vec3 end, float delta, Vec3 out )
   {
       out.x = (end.x - x) * delta + x;
       out.y = (end.y - y) * delta + y;
       out.z = (end.z - z) * delta + z;

       return out;
   }
   
   @Override
   public Vec3 clamp( Vec3 min, Vec3 max, Vec3 out )
   {
       out.x = SteerMath.clamp( x, min.x, max.x );
       out.y = SteerMath.clamp( y, min.y, max.y );
       out.z = SteerMath.clamp( z, min.z, max.z );
       
       return out;
   }

   @Override
   public float lengthSq()
   {
      return x * x + y * y + z * z;
   }

   @Override
   public float length()
   {
      return (float)Math.sqrt( x * x + y * y + z * z );
   }

   /**
    * Sets this to the vector with the given yaw and pitch in radians with the 
    * given magnitude, and returns this.
    */
   public Vec3 angle( float yaw, float pitch, float magnitude )
   {
       // euler angles to vector
       x = (float)(Math.cos(yaw) * Math.cos(pitch)) * magnitude;
       y = (float)(Math.sin(yaw) * Math.cos(pitch)) * magnitude;
       z = (float)(Math.sin(pitch)) * magnitude;

       return this;
   }

   /**
    * Returns the yaw in radians of this vector.
    */
   public float yaw()
   {
       float a = (float)StrictMath.atan2( x, z );

       if ( a < 0 )
       {
           a += ANGLE_FIX;
       }

       return a;
   }

   /**
    * Returns the yaw in radians of this vector.
    */
   public float pitch()
   {
       float a = (float)StrictMath.atan2( y, Math.sqrt( z * z + x * x) );

       if ( a < 0 )
       {
           a += ANGLE_FIX;
       }

       return a;
   }

   @Override
   public Vec3 rotate( Vec3 cossin, Vec3 out )
   {
      float ox = x, oy = y, oz = z, px, py, pz;
      float zcos = cossin.x;
      float zsin = cossin.y;
      
      px = ox * zcos - oy * zsin;
      py = ox * zsin + oy * zcos;
      pz = oz;
      
      float ycos = cossin.x;
      float ysin = cossin.z;
      
      ox = px * ycos + pz * ysin;
      oy = py;
      oz =-px * ysin + pz * ycos;

      float xcos = cossin.z;
      float xsin = cossin.y;
      
      px = ox;
      py = oy * xcos - oz * xsin;
      py = oy * xsin + oz * xcos;
      
      return out.set( px, py, pz );
   }

   @Override
   public Vec3 unrotate( Vec3 cossin, Vec3 out )
   {
      float ox = x, oy = y, oz = z, px, py, pz;
      float zcos = cossin.x;
      float zsin = cossin.y;
      
      px = ox * zcos - oy * zsin;
      py = ox * zsin + oy * zcos;
      pz = oz;
      
      float ycos = cossin.x;
      float ysin = cossin.z;
      
      ox = px * ycos + pz * ysin;
      oy = py;
      oz =-px * ysin + pz * ycos;

      float xcos = cossin.z;
      float xsin = cossin.y;
      
      px = ox;
      py = oy * xcos - oz * xsin;
      py = oy * xsin + oz * xcos;
      
      return out.set( px, py, pz );
   }

   @Override
   public Vec3 floor( Vec3 out )
   {
      out.x = (float)Math.floor( x );
      out.y = (float)Math.floor( y );
      out.z = (float)Math.floor( z );
      return out;
   }

   @Override
   public Vec3 ceil( Vec3 out )
   {
      out.x = (float)Math.ceil( x );
      out.y = (float)Math.ceil( y );
      out.z = (float)Math.ceil( z );
      return out;
   }

   @Override
   public Vec3 invert( Vec3 out )
   {
      out.x = x == 0.0f ? 0.0f : 1.0f / x;
      out.y = y == 0.0f ? 0.0f : 1.0f / y;
      out.z = z == 0.0f ? 0.0f : 1.0f / z;
      return out;
   }

   @Override
   public Vec3 mini( Vec3 a, Vec3 b )
   {
      return min( a, b, this );
   }

   @Override
   public Vec3 maxi( Vec3 a, Vec3 b )
   {
      return max( a, b, this );
   }

   @Override
   public float dot( Vec3 v )
   {
      return dot( this, v );
   }

   @Override
   public float distanceSq( Vec3 v )
   {
      return distanceSq( this, v );
   }

   @Override
   public float distance( Vec3 v )
   {
      return distance( this, v );
   }

   @Override
   public boolean isParallel( Vec3 v, float epsilon )
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public Vec3 clone()
   {
      return new Vec3( x, y, z );
   }

   @Override
   public Vec3 ZERO()
   {
       return ZERO;
   }
   
   @Override
   public boolean isZero( float epsilon )
   {
      return isEqual( 0, 0, 0, epsilon );
   }

   /**
    * Determines if this vector is equal to the vector {xx, yy, zz}.
    */
   public boolean isEqual( float xx, float yy, float zz )
   {
      return (x == xx && y == yy && z == zz);
   }

   @Override
   public boolean isEqual( Vec3 v, float epsilon )
   {
      return isEqual( v.x, v.y, v.z, epsilon );
   }

   /**
    * Determines if this vector is equal to the vector {xx, yy, zz} within epsilon.
    */
   public boolean isEqual( float xx, float yy, float zz, float epsilon )
   {
      return Math.abs( xx - x ) < epsilon && Math.abs( yy - y ) < epsilon && Math.abs( zz - z ) < epsilon;
   }

   @Override
   public Vec3 defaultUnit()
   {
       return set( 1, 0, 0 );
   }

   @Override
   public boolean isBetween( Vec3 min, Vec3 max, float buffer )
   {
       return !(x < min.x + buffer || x > max.x - buffer || y < min.y + buffer || y > max.y - buffer || z < min.z + buffer || z > max.z - buffer);
   }
   
   @Override
   public Vec3 create()
   {
       return new Vec3();
   }
   
   @Override
   public Vec3 clone( Vec3 value )
   {
       return new Vec3( value );
   }
   
   @Override
   public Vec3 copy( Vec3 from, Vec3 to )
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
      result = prime * result + Float.floatToIntBits( z );
      return result;
   }

   @Override
   public boolean equals( Object obj )
   {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Vec3 other = (Vec3)obj;
      if (Float.floatToIntBits( x ) != Float.floatToIntBits( other.x )) return false;
      if (Float.floatToIntBits( y ) != Float.floatToIntBits( other.y )) return false;
      if (Float.floatToIntBits( z ) != Float.floatToIntBits( other.z )) return false;
      return true;
   }

   @Override
   public String toString()
   {
      return "{" + x + "," + y + "," + z + "}";
   }

   /**
    * Returns and sets out to the minimum x and y coordinates from a and b.
    */
   public static Vec3 min( Vec3 a, Vec3 b, Vec3 out )
   {
      out.x = StrictMath.min( a.x, b.x );
      out.y = StrictMath.min( a.y, b.y );
      out.z = StrictMath.min( a.z, b.z );
      return out;
   }

   /**
    * Returns and sets out to the maximum x and y coordinates from a and b.
    */
   public static Vec3 max( Vec3 a, Vec3 b, Vec3 out )
   {
      out.x = StrictMath.max( a.x, b.x );
      out.y = StrictMath.max( a.y, b.y );
      out.z = StrictMath.max( a.z, b.z );
      return out;
   }

   /**
    * Return the dot product between the two vectors.
    */
   public static float dot( Vec3 a, Vec3 b )
   {
      return a.x * b.x + a.y * b.y + a.z * b.z;
   }

   /**
    * Return the distance (squared) between the two points.
    */
   public static float distanceSq( Vec3 a, Vec3 b )
   {
      float dx = a.x - b.x;
      float dy = a.y - b.y;
      float dz = a.z - b.z;

      return dx * dx + dy * dy + dz * dz;
   }

   /**
    * Return the distance between the two points.
    */
   public static float distance( Vec3 a, Vec3 b )
   {
      float dx = a.x - b.x;
      float dy = a.y - b.y;
      float dz = a.z - b.z;

      return (float)Math.sqrt( dx * dx + dy * dy + dz * dz );
   }

   /**
    * Returns a new array of instantiated Vectors of the given length.
    */
   public static Vec3[] arrayOf( int length )
   {
      Vec3[] array = new Vec3[length];

      while (--length >= 0)
      {
         array[length] = new Vec3();
      }

      return array;
   }
  
}
