package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.SteerMath;
import org.magnos.steer.vec.Vec;

/**
 * http://www.reddit.com/r/gamedev/comments/1ei88i/very_fast_2d_interpolation/
 * 
 * @author Philip Diffenderfer
 *
 * @param <T>
 */
public class KramerPath<V extends Vec<V>> implements Path<V>
{
	
	public static final float DEFAULT_LOOSENESS = 0.0575f;
	
	protected V[] points;
	protected final V temp0;
	protected final V temp1;
	protected int depth;
	protected float looseness;
	protected boolean loops;
	protected float roughness;
	
	public KramerPath( int depth, boolean loops, V ... points )
	{
		this( depth, loops, DEFAULT_LOOSENESS, 0.0f, points );
	}
	
	public KramerPath( int depth, boolean loops, float looseness, V ... points ) 
	{
		this( depth, loops, looseness, 0.0f, points );
	}
	
	public KramerPath( float roughness, boolean loops, V ... points )
	{
		this( 0, loops, DEFAULT_LOOSENESS, roughness, points );
	}
	
	public KramerPath( float roughness, boolean loops, float looseness, V ... points ) 
	{
		this( 0, loops, looseness, roughness, points );
	}
	
	protected KramerPath( int depth, boolean loops, float looseness, float roughness, V ... points ) 
	{
		this.depth = depth;
		this.loops = loops;
		this.looseness = looseness;
		this.roughness = roughness;
		this.points = points;
        this.temp0 = points[0].create();
        this.temp1 = points[0].create();
	}
	
	@Override
	public V set(V subject, float delta) 
	{
		final int n = points.length;
		final float a = delta * n;
		final int i = SteerMath.clamp( (int)a, 0, n - 1 );
		float d = a - i;
		
		if (depth != 0)
		{
			getPointWithExactDepth( i, d, subject );
		}
		else
		{
			getPointWithRoughness( i, d, subject );
		}
		
		return subject;
	}
	
	public void getPointWithExactDepth( int i, float d, V subject )
	{
		// v0 and v5 are used to calculate the next v1 or v4, at the next level.
		V v0 = points[ getActualIndex( i - 2 ) ];
		V v1 = points[ getActualIndex( i - 1 ) ];
		V v2 = points[ getActualIndex( i ) ];
		V v3 = points[ getActualIndex( i + 1 ) ];
		V v4 = points[ getActualIndex( i + 2 ) ];
		V v5 = points[ getActualIndex( i + 3 ) ];
		
		int k = depth;

		while (--k >= 0)
		{
			// Get mid point
			V mid = getPoint( v1, v2, v3, v4 );
			
			// If the desired point is closer to v2...
			if (d < 0.5f)
			{
				// shift all surrounding points one-level closer to v2
				if (k == 0)
				{
					v3 = mid;
				}
				else
				{
					V newEnd = v1;
					v5 = v4;
					v4 = v3;
					v3 = mid;
					v1 = getPoint( v0, v1, v2, v3 );
					v0 = newEnd;
				}
				// adjust d so it's between 0.0 an 1.0
				d = d * 2.0f;
			}
			// else, the desired point is closer to v3...
			else
			{
				// shift all surrounding points one-level closer to v3
				if (k == 0)
				{
					v2 = mid;
				}
				else
				{
					V newEnd = v4;
					v0 = v1;
					v1 = v2;
					v2 = mid;
					v4 = getPoint( v2, v3, v4, v5 );
					v5 = newEnd;
				}
				// adjust d so it's between 0.0 an 1.0
				d = (d - 0.5f) * 2.0f;
			}
		}
		
		// subject = (v3 - v2) * d + v2
		subject.interpolatei( v2, v3, d );
	}
	
	public void getPointWithRoughness( int i, float d, V subject )
	{
		// v0 and v5 are used to calculate the next v1 or v4, at the next level.
		V v0 = points[ getActualIndex( i - 2 ) ];
		V v1 = points[ getActualIndex( i - 1 ) ];
		V v2 = points[ getActualIndex( i ) ];
		V v3 = points[ getActualIndex( i + 1 ) ];
		V v4 = points[ getActualIndex( i + 2 ) ];
		V v5 = points[ getActualIndex( i + 3 ) ];

		for (;;)
		{
			// Get mid point
			V mid = getPoint( v1, v2, v3, v4 );
			
			// if distance from mid to (v2->v3) is <= roughness, break
			// calculate the distance between all three points to form a triangle,
			// with the distances determine the perimeter then area. Use the
			//	area = 0.5 * b * h formula to calculate height.
			float height = SteerMath.getDistanceFromLine( mid, v2, v3, temp0 );
			
			if (height <= roughness)
			{
				break;
			}
			
			// If the desired point is closer to v2...
			if (d < 0.5f)
			{
				// shift all surrounding points one-level closer to v2
				V newEnd = v1;
				v5 = v4;
				v4 = v3;
				v3 = mid;
				v1 = getPoint( v0, v1, v2, v3 );
				v0 = newEnd;
				// adjust d so it's between 0.0 an 1.0
				d = d * 2.0f;
			}
			// else, the desired point is closer to v3...
			else
			{
				// shift all surrounding points one-level closer to v3
				V newEnd = v4;
				v0 = v1;
				v1 = v2;
				v2 = mid;
				v4 = getPoint( v2, v3, v4, v5 );
				v5 = newEnd;
				// adjust d so it's between 0.0 an 1.0
				d = (d - 0.5f) * 2.0f;
			}
		}
		
		// subject = (v3 - v2) * d + v2
		subject.interpolatei( v2, v3, d );
	}
	
	public float getDistance( V a, V b )
	{
		temp0.set( a );
	
		return temp0.distance( b );
	}
	
	public V getPoint( V v1, V v2, V v3, V v4 )
	{
		// p = (0.5f + looseness) * (v2 + v3) - looseness * (v1 + v4)
		
		temp0.set( v2 );
		temp0.addi( v3 );
		temp0.muli( 0.5f + looseness );
		
		temp1.set( v1 );
		temp1.addi( v4 );
		temp0.addsi( temp1, -looseness );
		
		return temp0.clone();
	}
	
	public int getActualIndex( int index )
	{
		final int n = points.length;
		
		return ( loops ? (index + n) % n : SteerMath.clamp( index, 0, n - 1 ) );
	}

}
