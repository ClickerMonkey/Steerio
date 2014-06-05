
package org.magnos.steer.vec;

import java.util.Arrays;

import org.magnos.steer.SteerMath;

/**
 * An n-dimensional {@link Vec} implementation.
 * 
 * @author Philip Diffenderfer
 *
 */
public class VecN extends AbstractVec<VecN>
{

    public static VecN[] ZERO = {
        new VecN( 0 ),
        new VecN( 1 ),
        new VecN( 2 ),
        new VecN( 3 ),
        new VecN( 4 ),
        new VecN( 5 ),
        new VecN( 6 ),
        new VecN( 7 ),
        new VecN( 8 )
    };
                                 
    
    /**
     * The coordinates of the Vector.
     */
    public float[] x;

    /**
     * Instantiates a new Vector at the origin.
     */
    public VecN( int n )
    {
        this.x = new float[ n ];
    }

    /**
     * Instantiates a new Vector at the specified coordinates.
     * 
     * @param x
     *        The initial coordinates of the vector.
     */
    public VecN( float ... x )
    {
        this.x = x;
    }

    /**
     * Instantiates a new Vector based on another Vector.
     * 
     * @param v
     *        The vector to copy x and y coordinates from.
     */
    public VecN( VecN v )
    {
        set( v );
    }

    /**
     * Sets the coordinates of this vector and returns this.
     */
    public VecN set( float ... x )
    {
        this.x = x;
        return this;
    }

    @Override
    public int size()
    {
        return x.length;
    }
    
    @Override
    public float getComponent(int component)
    {
        return x[component];
    }
    
    @Override
    public void setComponent(int component, float value)
    {
        x[component] = value;
    }
    
    @Override
    public VecN set( VecN v )
    {
        if (x == null || x.length != v.size()) 
        {
            x = new float[ v.size() ];
        }
        
        System.arraycopy( v.x, 0, x, 0, x.length );
        return this;
    }

    @Override
    public void clear()
    {
        Arrays.fill( x, 0.0f );
    }

    @Override
    public VecN clear( float value )
    {
        Arrays.fill( x, value );
        
        return this;
    }

    @Override
    public VecN neg( VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = -x[i];
        }
        return out;
    }

    @Override
    public VecN abs( VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = Math.abs( x[i] );
        }
        return out;
    }

    @Override
    public VecN mul( float s, VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = s * x[i];
        }
        return out;
    }

    @Override
    public VecN div( float s, VecN out )
    {
        if ( s != 0.0f )
        {
            s = 1.0f / s;

            for (int i = x.length - 1; i >= 0; i--)
            {
                out.x[i] = s * x[i];
            }
        }
        return out;
    }

    @Override
    public VecN add( float s, VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = s + x[i];
        }
        return out;
    }

    @Override
    public VecN mul( VecN v, VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = x[i] * v.x[i];
        }
        return out;
    }

    @Override
    public VecN div( VecN v, VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            if (v.x[i] != 0)
            {
                out.x[i] = x[i] / v.x[i];    
            }
        }
        return out;
    }

    @Override
    public VecN add( VecN v, VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = x[i] + v.x[i];
        }
        return out;
    }

    @Override
    public VecN adds( VecN v, float s, VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = x[i] + v.x[i] * s;
        }
        return out;
    }

    @Override
    public VecN sub( VecN v, VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = x[i] - v.x[i];
        }
        return out;
    }
    
    @Override
    public VecN mod( float s, VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = x[i] % s;
        }
        return out;
    }

    @Override
    public VecN mod( VecN v, VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = x[i] % v.x[i];
        }
        return out;
    }

    @Override
    public VecN direct( VecN origin, VecN target, VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = target.x[i] - origin.x[i];
        }
        return out;
    }

    @Override
    public VecN interpolate( VecN start, VecN end, float delta, VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = (end.x[i] - start.x[i]) * delta + start.x[i];
        }
        return out;
    }

    @Override
    public VecN interpolateTo( VecN end, float delta, VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = (end.x[i] - x[i]) * delta + x[i];
        }
        return out;
    }
    
    @Override
    public VecN clamp( VecN min, VecN max, VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = SteerMath.clamp( x[i], min.x[i], max.x[i] );
        }
        return out;
    }

    @Override
    public float lengthSq()
    {
        float lengthSq = 0;
        
        for (int i = x.length - 1; i >= 0; i--)
        {
            lengthSq += x[i] * x[i];
        }
        
        return lengthSq;
    }

    @Override
    public float length()
    {
        return (float)Math.sqrt( lengthSq() );
    }

    @Override
    public VecN rotate( VecN cossin, VecN out )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public VecN unrotate( VecN cossin, VecN out )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public VecN floor( VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = (float)Math.floor( x[i] );
        }
        
        return out;
    }

    @Override
    public VecN ceil( VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = (float)Math.ceil( x[i] );
        }
        
        return out;
    }

    @Override
    public VecN invert( VecN out )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            out.x[i] = x[i] == 0.0f ? 0.0f : 1.0f / x[i];
        }
        return out;
    }
    
    @Override
    public VecN mini( VecN a, VecN b )
    {
        return min( a, b, this );
    }

    @Override
    public VecN maxi( VecN a, VecN b )
    {
        return max( a, b, this );
    }

    @Override
    public float dot( VecN v )
    {
        return dot( this, v );
    }

    @Override
    public float distanceSq( VecN v )
    {
        return distanceSq( this, v );
    }

    @Override
    public float distance( VecN v )
    {
        return distance( this, v );
    }

    @Override
    public boolean isParallel( VecN v, float epsilon )
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Clones this vector.
     */
    public VecN clone()
    {
        return new VecN( Arrays.copyOf( x, x.length ) );
    }

    @Override
    public VecN ZERO()
    {
        return ZERO[ x.length ];
    }
    
    @Override
    public boolean isZero( float epsilon )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            if (Math.abs(x[i]) >= epsilon)
            {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public boolean isEqual( VecN v, float epsilon )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            if (Math.abs(x[i] - v.x[i]) >= epsilon)
            {
                return false;
            }
        }
        
        return true;
    }


    @Override
    public VecN defaultUnit()
    {
        x[0] = 1.0f;
        
        for (int i = x.length - 1; i >= 1; i--)
        {
            x[i] = 0.0f;
        }
        
        return this;
    }

    @Override
    public boolean isBetween( VecN min, VecN max, float buffer )
    {
        for (int i = x.length - 1; i >= 0; i--)
        {
            if (x[i] < min.x[i] + buffer || x[i] > max.x[i] - buffer)
            {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public VecN create()
    {
        return new VecN( x.length );
    }

    @Override
    public VecN clone( VecN value )
    {
        return new VecN( value );
    }

    @Override
    public VecN copy( VecN from, VecN to )
    {
        to.set( from );
        return to;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        for (int i = 0; i < x.length; i++)
        {
            result = prime * result + Float.floatToIntBits( x[i] );    
        }
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        VecN other = (VecN)obj;
        for (int i = 0; i < x.length; i++)
        {
            if ( Float.floatToIntBits( x[i] ) != Float.floatToIntBits( other.x[i] ) ) return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder( x.length * 3 + 3 );
        sb.append( "{" );
        
        for (int i = 0; i < x.length; i++)
        {
            if (i > 0) sb.append( "," );
            
            sb.append( x[i] );
        }
        
        sb.append( "}" );
        return sb.toString();
    }

    /**
     * Returns and sets out to the minimum x and y coordinates from a and b.
     */
    public static VecN min( VecN a, VecN b, VecN out )
    {
        for (int i = out.x.length - 1; i >= 0; i--)
        {
            out.x[i] = StrictMath.min( a.x[i], b.x[i] );
        }
        return out;
    }

    /**
     * Returns and sets out to the maximum x and y coordinates from a and b.
     */
    public static VecN max( VecN a, VecN b, VecN out )
    {
        for (int i = out.x.length - 1; i >= 0; i--)
        {
            out.x[i] = StrictMath.max( a.x[i], b.x[i] );
        }
        return out;
    }

    /**
     * Return the dot product between the two vectors.
     */
    public static float dot( VecN a, VecN b )
    {
        float dot = 0;
        
        for (int i = a.x.length - 1; i >= 0; i--)
        {
            dot += a.x[i] * b.x[i];
        }
        
        return dot;
    }

    /**
     * Return the distance (squared) between the two points.
     */
    public static float distanceSq( VecN a, VecN b )
    {
        float distanceSq = 0;
        
        for (int i = a.x.length - 1; i >= 0; i--)
        {
            float dx = a.x[i] - b.x[i]; 
            
            distanceSq += dx * dx;
        }
        
        return distanceSq;
    }

    /**
     * Return the distance between the two points.
     */
    public static float distance( VecN a, VecN b )
    {
        return (float)Math.sqrt( distanceSq( a, b ) );
    }

    /**
     * Returns a new array of instantiated Vectors of the given length.
     */
    public static VecN[] arrayOf( int length, int n )
    {
        VecN[] array = new VecN[length];

        while ( --length >= 0 )
        {
            array[length] = new VecN( n );
        }

        return array;

    }

}
