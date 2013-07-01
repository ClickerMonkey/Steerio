package org.magnos.steer.spatial;

import org.magnos.steer.Vector;
import org.magnos.steer.Segment;


public class SpatialEntityWall extends Segment implements SpatialEntity
{

	public final Vector center = new Vector();
	public boolean inert;
	public long groups;
	public long collisionGroups;
	
	public SpatialEntityWall()
	{
	}
	
	public SpatialEntityWall(float x0, float y0, float x1, float y1, long groups, long collisionGroups)
	{
		this.set( x0, y0, x1, y1 );
		this.groups = groups;
		this.collisionGroups = collisionGroups; 
		this.inert = false;
	}
	
	@Override
	public void set(float x0, float y0, float x1, float y1)
	{
		super.set( x0, y0, x1, y1 );
		
		this.center.x = (x0 + x1) * 0.5f;
		this.center.y = (y0 + y1) * 0.5f;
	}
	
	@Override
	public Vector getPosition()
	{
		return center;
	}

	@Override
	public float getRadius()
	{
		return radius();
	}

	@Override
	public long getSpatialGroups()
	{
		return groups;
	}
	
	@Override
	public long getSpatialCollisionGroups()
	{
		return collisionGroups;
	}

	@Override
	public boolean isStatic()
	{
		return true;
	}

	@Override
	public boolean isInert()
	{
		return inert;
	}

}
