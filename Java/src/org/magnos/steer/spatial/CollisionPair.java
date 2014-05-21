package org.magnos.steer.spatial;

import org.magnos.steer.vec.Vec;


public class CollisionPair<V extends Vec<V>>
{
	
	public SpatialEntity<V> a;
	public SpatialEntity<V> b;
	public float overlap;
	public boolean mutual;
	
	public CollisionPair()
	{
	}
	
	public CollisionPair(SpatialEntity<V> a, SpatialEntity<V> b, float overlap, boolean mutual)
	{
		set( a, b, overlap, mutual );
	}
	
	public void set(SpatialEntity<V> a, SpatialEntity<V> b, float overlap, boolean mutual)
	{
		this.a = a;
		this.b = b;
		this.overlap = overlap; 
		this.mutual = mutual;
	}

	@Override
	public int hashCode()
	{
		int x = ((a == null) ? 0 : a.hashCode());
		int y = ((b == null) ? 0 : b.hashCode());
		
		return (x ^ y);
	}

	@Override
	public boolean equals( Object obj )
	{
		CollisionPair<V> pair = (CollisionPair<V>)obj;
		
		return (a == pair.a && b == pair.b) ||
			    (a == pair.b && b == pair.a);
	}
	
}
