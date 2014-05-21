
package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.SteerMath;
import org.magnos.steer.vec.Vec;


//TODO fix BasisSplinePath where 
//	P(t) = Ti * Mbs * Gbs
//		Ti = [(t - ti)^3, (t - ti)^2, (t - ti), 1]
//		Gbs = [P(i-3), P(i-2), P(i-1), P(i)]
//		Mbs = BasisSplinePath.MATRIX * BasisSplinePath.WEIGHT
public class BasisSplinePath<V extends Vec<V>> implements Path<V>
{

    public static final float WEIGHT = 1.0f / 6.0f;
    public static final float[][] MATRIX = {
        { -1, 3, -3, 1 },
        { 3, -6, 3, 0 },
        { -3, 0, 3, 0 },
        { 1, 4, 1, 0 }
    };

    public V[] points;

    public BasisSplinePath()
    {
    }

    public BasisSplinePath( V... points )
    {
        this.points = points;
    }

    @Override
    public V set( V subject, float delta )
    {
        return SteerMath.parametricCubicCurve( delta, points, MATRIX, WEIGHT, subject );
    }

}
