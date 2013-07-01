package org.magnos.steer.spatial;


public class CollisionPair
{
	
	public SpatialEntity a;
	public SpatialEntity b;
	public float overlap;
	public boolean mutual;
	
	public CollisionPair()
	{
	}
	
	public CollisionPair(SpatialEntity a, SpatialEntity b, float overlap, boolean mutual)
	{
		set( a, b, overlap, mutual );
	}
	
	public void set(SpatialEntity a, SpatialEntity b, float overlap, boolean mutual)
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
		CollisionPair pair = (CollisionPair)obj;
		
		return (a == pair.a && b == pair.b) ||
			    (a == pair.b && b == pair.a);
	}
	
}
