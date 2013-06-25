package org.magnos.steer.spatial;

import org.magnos.steer.Vector;
import org.magnos.steer.Wall;


public class SpatialEntityWall extends Wall implements SpatialEntity
{

	public final Vector center = new Vector();
	public boolean inert;
	public long groups;
	
	public SpatialEntityWall()
	{
	}
	
	public SpatialEntityWall(float x0, float y0, float x1, float y1, long groups)
	{
		this.set( x0, y0, x1, y1 );
		this.groups = groups;
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
