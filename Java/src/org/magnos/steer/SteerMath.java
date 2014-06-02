
package org.magnos.steer;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.magnos.steer.util.FieldOfView;
import org.magnos.steer.vec.Vec;


public class SteerMath
{

    public static final Random random = new Random();
    public static final float PI = (float)Math.PI;
    public static final float PI2 = (float)(Math.PI * 2.0);
    public static final float EPSILON = 0.00001f;

    public static boolean equals( float a, float b )
    {
        return equals( a, b, EPSILON );
    }

    public static boolean equals( float a, float b, float epsilon )
    {
        return Math.abs( a - b ) < epsilon;
    }

    public static float sqrt( float x )
    {
        return (float)Math.sqrt( x );
    }

    public static float cos( float x )
    {
        return (float)Math.cos( x );
    }

    public static float sin( float x )
    {
        return (float)Math.sin( x );
    }

    public static float clamp( float d, float min, float max )
    {
        return (d < min ? min : (d > max ? max : d));
    }

    public static int clamp( int d, int min, int max )
    {
        return (d < min ? min : (d > max ? max : d));
    }
    
    public static <V extends Vec<V>> V slerp( V s, V e, float t, V out )
    {
        float slength = s.length();
        float elength = e.length();
        float angle = (float)Math.acos( s.dot( e ) / (slength * elength) );
        
        return slerp( s, e, angle, t, out );
    }
    
    public static <V extends Vec<V>> V slerpNormal( V s, V e, float t, V out )
    {
        float angle = (float)Math.acos( s.dot( e ) );
        
        return slerp( s, e, angle, t, out );
    }
    
    public static <V extends Vec<V>> V slerp( V s, V e, float angle, float t, V out )
    {
        float denom = 1.0f / sin( angle );
        float d0 = sin( (1 - t) * angle ) * denom;
        float d1 = sin( t * angle ) * denom;
        
        return out.set( e ).muli( d1 ).addsi( s, d0 );
    }

    public static <V extends Vec<V>> V closest( V s, V e, V v, V out )
    {
        V p0 = e.sub( s );
        V p1 = v.sub( s );
        float delta = p0.dot( p1 ) / p0.lengthSq();

        delta = clamp( delta, 0, 1 );

        return out.interpolatei( s, e, delta );
    }

    public static <V extends Vec<V>> float interceptTime( V shooter, float shooterSpeed, V targetPosition, V targetVelocity )
    {
        V tvec = targetPosition.sub( shooter );

        float a = targetVelocity.lengthSq() - (shooterSpeed * shooterSpeed);
        float b = 2 * targetVelocity.dot( tvec );
        float c = tvec.lengthSq();

        float t0 = Float.MIN_VALUE;
        float t1 = Float.MIN_VALUE;

        if ( Math.abs( a ) < EPSILON )
        {
            if ( Math.abs( b ) < EPSILON )
            {
                if ( Math.abs( c ) < EPSILON )
                {
                    t0 = 0.0f;
                    t1 = 0.0f;
                }
            }
            else
            {
                t0 = -c / b;
                t1 = -c / b;
            }
        }
        else
        {
            float disc = b * b - 4 * a * c;

            if ( disc >= 0 )
            {
                disc = sqrt( disc );
                a = 2 * a;
                t0 = (-b - disc) / a;
                t1 = (-b + disc) / a;
            }
        }

        if ( t0 != Float.MIN_VALUE )
        {
            float t = Math.min( t0, t1 );

            if ( t < 0 )
            {
                t = Math.max( t0, t1 );
            }

            if ( t > 0 )
            {
                return t;
            }
        }

        return -1;
    }

    public static <V extends Vec<V>> float getDistanceFromLine( V point, V start, V end )
    {
        float lineLength = start.distance( end );
        float startToPoint = point.distance( start );
        float endToPoint = point.distance( end );

        return getTriangleHeight( lineLength, startToPoint, endToPoint );
    }

    public static <V extends Vec<V>> float getDistanceFromLine( V point, V start, V end, V temp )
    {
        temp.set( start );
        float lineLength = temp.distance( end );
        float startToPoint = temp.distance( point );
        temp.set( end );
        float endToPoint = temp.distance( point );

        return getTriangleHeight( lineLength, startToPoint, endToPoint );
    }

    public static float getTriangleHeight( float base, float side1, float side2 )
    {
        float p = (base + side1 + side2) * 0.5f;
        float area = sqrt( p * (p - base) * (p - side1) * (p - side2) );
        float height = area * 2.0f / base;

        return height;
    }
    
    public static <V extends Vec<V>> boolean isPointInView( V origin, V direction, float fovCos, V point )
    {
        return point.sub( origin ).dot( direction ) > fovCos;
    }
    
    /**
     * Returns whether a circle is within view of an object at a position facing some direction.
     * 
     * @param viewOrigin
     *        The origin of the view.
     * @param viewDirection
     *        The direction of the view, must be a normalized vector.
     * @param fovTan
     *        The tangent of half of the total field of view.
     * @param fovCos
     *        The cosine of half of the total field of view.
     * @param circle
     *        The center of the circle.
     * @param circleRadius
     *        The radius of the circle.
     * @param entirely
     *        True if this method should return whether the circle is completely
     *        within view, or false if this method should return whether the circle
     *        is partially within view.
     * @return True if the circle is in view, otherwise false.
     */
    public static <V extends Vec<V>> boolean isCircleInView( V viewOrigin, V viewDirection, double fovTan, double fovCos, V circle, float circleRadius, boolean entirely )
    {
        // http://www.cbloom.com/3d/techdocs/culling.txt
        final V circleToOrigin = circle.sub( viewOrigin );
        double distanceAlongDirection = circleToOrigin.dot( viewDirection );    
        double coneRadius = distanceAlongDirection * fovTan;                       
        double distanceFromAxis = Math.sqrt( circleToOrigin.lengthSq() - distanceAlongDirection * distanceAlongDirection ); 
        double distanceFromCenterToCone = distanceFromAxis - coneRadius;                              
        double shortestDistance = distanceFromCenterToCone * fovCos;                          
        
        if (entirely) {
            shortestDistance += circleRadius;
        } else {
            shortestDistance -= circleRadius;
        }
        
        return shortestDistance <= 0;
    }

    public static <V extends Vec<V>> boolean isCircleInView( V viewOrigin, V viewDirection, double fovTan, double fovCos, V circle, float circleRadius, FieldOfView fovType )
    {
        if ( fovType == FieldOfView.IGNORE )
        {
            return true;
        }

        if ( fovType == FieldOfView.HALF )
        {
            circleRadius = 0f;
        }

        return isCircleInView( viewOrigin, viewDirection, fovTan, fovCos, circle, circleRadius, fovType == FieldOfView.FULL );
    }

    public static int factorial( int x )
    {
        int n = x;
        while ( --x >= 1 )
        {
            n *= x;
        }
        return n;
    }

    // greatest common divisor, 32-bit integer
    public static int gcd( int a, int b )
    {
        int shift = 0;

        if ( a == 0 || b == 0 )
        {
            return (a | b);
        }

        for ( shift = 0; ((a | b) & 1) == 0; ++shift )
        {
            a >>= 1;
            b >>= 1;
        }

        while ( (a & 1) == 0 )
        {
            a >>= 1;
        }

        do
        {
            while ( (b & 1) == 0 )
            {
                b >>= 1;
            }
            if ( a < b )
            {
                b -= a;
            }
            else
            {
                int d = a - b;
                a = b;
                b = d;
            }
            b >>= 1;
        }
        while ( b != 0 );

        return (a << shift);
    }

    // greatest common divisor, 64-bit integer
    public static long gcd( long a, long b )
    {
        int shift = 0;

        if ( a == 0 || b == 0 )
        {
            return (a | b);
        }

        for ( shift = 0; ((a | b) & 1) == 0; ++shift )
        {
            a >>= 1;
            b >>= 1;
        }

        while ( (a & 1) == 0 )
        {
            a >>= 1;
        }

        do
        {
            while ( (b & 1) == 0 )
            {
                b >>= 1;
            }
            if ( a < b )
            {
                b -= a;
            }
            else
            {
                long d = a - b;
                a = b;
                b = d;
            }
            b >>= 1;
        }
        while ( b != 0 );

        return (a << shift);
    }

    // Calculates the combination of the given integer n and m. Un-ordered
    // collection of distinct elements.
    // C(n,m) = n! / m!(n - m)!
    public static long choose( long n, long m )
    {
        long num = 1, den = 1, gcd;

        if ( m > (n >> 1) )
        {
            m = n - m;
        }

        while ( m >= 1 )
        {
            num *= n--;
            den *= m--;
            gcd = gcd( num, den );
            num /= gcd;
            den /= gcd;
        }

        return num;
    }

    // Calculates the combination of the given integer n and m. Un-ordered
    // collection of distinct elements.
    // C(n,m) = n! / m!(n - m)!
    public static int choose( int n, int m )
    {
        int num = 1, den = 1, gcd;

        if ( m > (n >> 1) )
        {
            m = n - m;
        }

        while ( m >= 1 )
        {
            num *= n--;
            den *= m--;
            gcd = gcd( num, den );
            num /= gcd;
            den /= gcd;
        }

        return num;
    }

    public static <T> T[] add( T e, T[] elements )
    {
        int size = elements.length;
        elements = Arrays.copyOf( elements, size + 1 );
        elements[size] = e;
        return elements;
    }

    public static float[] add( float e, float[] elements )
    {
        int size = elements.length;
        elements = Arrays.copyOf( elements, size + 1 );
        elements[size] = e;
        return elements;
    }

    public static int[] add( int e, int[] elements )
    {
        int size = elements.length;
        elements = Arrays.copyOf( elements, size + 1 );
        elements[size] = e;
        return elements;
    }

    public static boolean[] add( boolean e, boolean[] elements )
    {
        int size = elements.length;
        elements = Arrays.copyOf( elements, size + 1 );
        elements[size] = e;
        return elements;
    }

    public static void setRandomSeed( long randomSeed )
    {
        random.setSeed( randomSeed );
    }

    public static float randomFloat()
    {
        return random.nextFloat();
    }

    public static float randomFloat( float x )
    {
        return random.nextFloat() * x;
    }

    public static float randomFloat( float min, float max )
    {
        return random.nextFloat() * (max - min) + min;
    }

    public static int randomSign()
    {
        return (random.nextInt( 2 ) << 1) - 1;
    }

    public static int randomInt()
    {
        return random.nextInt();
    }

    public static int randomInt( int x )
    {
        return random.nextInt( x );
    }

    public static int randomInt( int min, int max )
    {
        return random.nextInt( max - min + 1 ) + min;
    }

    public static long randomLong()
    {
        return random.nextLong();
    }

    public static long randomLong( long min, long max )
    {
        return (long)((max - min + 1) * random.nextDouble() + min);
    }

    public static boolean randomBoolean()
    {
        return random.nextBoolean();
    }

    public static <E extends Enum<E>> E random( Class<E> enumClass )
    {
        return random( enumClass.getEnumConstants() );
    }

    public static <T> T random( T[] elements )
    {
        return elements[random.nextInt( elements.length )];
    }

    public static <T> T random( T[] elements, T returnOnNull )
    {
        return (elements == null || elements.length == 0 ? returnOnNull : elements[random.nextInt( elements.length )]);
    }

    public static <T> T random( T[] elements, int min, int max, T returnOnNull )
    {
        return (elements == null || elements.length == 0 ? returnOnNull : elements[random.nextInt( max - min + 1 ) + min]);
    }

    public static <T> T random( List<T> elements )
    {
        return elements.get( random.nextInt( elements.size() ) );
    }

    public static <T> T random( List<T> elements, int min, int max, T returnOnNull )
    {
        return (elements == null || elements.size() == 0 ? returnOnNull : elements.get( random.nextInt( max - min + 1 ) + min ));
    }

    public static final int MATRIX_DIMENSIONS = 4;

    public static <V extends Vec<V>> V parametricCubicCurve( float delta, V[] points, float[][] matrix, float weight, V out )
    {
        final int n = points.length - 1;
        final float a = delta * n;
        final int i = clamp( (int)a, 0, n - 1 );
        final float d = a - i;

        final V p0 = (i == 0 ? points[0] : points[i - 1]);
        final V p1 = points[i];
        final V p2 = points[i + 1];
        final V p3 = (i == n - 1 ? points[n] : points[i + 2]);

        SteerMath.cubicCurve( d, p0, p1, p2, p3, matrix, out );

        out.muli( weight );

        return out;
    }

    public static <V extends Vec<V>> void cubicCurve( float t, V p0, V p1, V p2, V p3, float[][] matrix, V out )
    {
        final V temp = out.clone();
        final float[] ts = { 1.0f, t, t * t, t * t * t };

        out.muli( 0 );

        for ( int i = 0; i < MATRIX_DIMENSIONS; i++ )
        {
            temp.clear();
            temp.addsi( p0, matrix[i][0] );
            temp.addsi( p1, matrix[i][1] );
            temp.addsi( p2, matrix[i][2] );
            temp.addsi( p3, matrix[i][3] );

            out.addsi( temp, ts[i] );
        }
    }

    public static <V extends Vec<V>> void cubicCurveCached( V p0, V p1, V p2, V p3, float[][] matrix, V[] out )
    {
        for ( int i = 0; i < MATRIX_DIMENSIONS; i++ )
        {
            V a = out[i];

            a.set( p0 );
            a.muli( matrix[i][0] );
            a.addsi( p1, matrix[i][1] );
            a.addsi( p2, matrix[i][2] );
            a.addsi( p3, matrix[i][3] );
        }
    }

}
