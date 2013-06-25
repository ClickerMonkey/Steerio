package org.magnos.steer.spatial;


public class CollisionPair
{
	
	public SpatialEntity a;
	public SpatialEntity b;
	public float overlap;
	public boolean mutual;
	
	public void set(SpatialEntity a, SpatialEntity b, float overlap, boolean mutual)
	{
		this.a = a;
		this.b = b;
		this.overlap = overlap; 
		this.mutual = mutual;
	}
	
}
