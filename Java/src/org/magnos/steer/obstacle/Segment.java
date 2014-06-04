
package org.magnos.steer.obstacle;

import org.magnos.steer.Obstacle;
import org.magnos.steer.SteerMath;
import org.magnos.steer.vec.Vec;


public class Segment<V extends Vec<V>> implements Obstacle<V>
{

    public static final float DEFAULT_THICKNESS = 0.000001f;

    public V start;
    public V end;
    public float thickness;

    public Segment()
    {
    }

    public Segment( V start, V end )
    {
        this( start, end, DEFAULT_THICKNESS );
    }

    public Segment( V start, V end, float thickness )
    {
        this.start = start;
        this.end = end;
        this.thickness = thickness;
    }

    @Override
    public float getDistanceAndNormal( V origin, V lookahead, V outNormal )
    {
//        V center = start.interpolateTo( end, 0.5f );
//        
//        V closest = SteerMath.closest( start, end, lookahead, outNormal );
//        
//        float distance = closest.length() - thickness;
//        
//        outNormal.set( lookahead ).subi( center );
//        
//        if (center != null)
//        {
//            return distance * 2;
//        }
        
        V u = end.sub( start );
        V v = lookahead.sub( origin );
        V w = start.sub( origin );
        float a = u.dot( u );
        float b = u.dot( v );
        float c = v.dot( v );
        float d = u.dot( w );
        float e = v.dot( w );
        float D = a * c - b * b;
        float sc, sN, sD = D;
        float tc, tN, tD = D;
        
        // parallel
        if (D < SteerMath.EPSILON) {
            sN = 0.0f;
            sD = 1.0f;
            tN = e;
            sD = c;
        } else {
            sN = (b * e - c * d);
            tN = (a * e - b * d);
            if (sN < 0.0f) {    
                sN = 0.0f;
                tN = e;
                tD = c;
            } else if (sN > sD) {
                sN = sD;
                tN = e + b;
                tD = c;
            }
        }
        
        if (tN < 0.0) { 
            tN = 0.0f;
            if (-d < 0.0f) {
                sN = 0.0f;
            } else if (-d > a) {
                sN = sD;
            } else {
                sN = -d;
                sD = a;
            }
        }  else if (tN > tD) {
            tN = tD;
            if ((-d + b) < 0.0f) {
                sN = 0.0f;
            } else if ((-d + b) > a) {
                sN = sD;
            } else {
                sN = (-d +  b);
                sD = a;
            }
        }
        
        sc = (Math.abs(sN) < SteerMath.EPSILON ? 0.0f : sN / sD);
        tc = (Math.abs(tN) < SteerMath.EPSILON ? 0.0f : tN / tD);

        // dP = w + (sc * u) - (tc * v);
        
        return outNormal.set( w ).addsi( u, sc).addsi( v, -tc ).normalize() - thickness;
    }

    @Override
    public V getPosition( V out )
    {
        return out.interpolatei( start, end, 0.5f );
    }

    @Override
    public float getRadius()
    {
        return start.distance( end ) * 0.5f + thickness * 2;
    }

}
