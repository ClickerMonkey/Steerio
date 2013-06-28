package org.magnos.steer.spatial.base;

import org.magnos.steer.SteerMath;
import org.magnos.steer.Vector;
import org.magnos.steer.util.LinkedList;


public class LinkedListRectangle<N> extends LinkedList<N>
{

	public final float l, t, r, b;
	
	public LinkedListRectangle(float l, float t, float r, float b)
	{
		this.l = l;
		this.t = t;
		this.r = r;
		this.b = b;
	}
	
	public boolean isOverCenter(Vector center, float radius)
	{
		return isOverCenter( center.x - radius, center.y - radius, center.x + radius, center.y + radius );
	}
	
	public boolean isOverCenter(float left, float top, float right, float bottom)
	{
		final float cx = (l + r) * 0.5f;
		final float cy = (t + b) * 0.5f;
		
		final float closestX = SteerMath.clamp( cx, left, right );
		final float closestY = SteerMath.clamp( cy, top, bottom );
		
		return (closestX == cx && closestY == cy);
	}
	
	public boolean isContained(Vector center, float radius)
	{
		return isContained( center.x - radius, center.y - radius, center.x + radius, center.y + radius );
	}
	
	public boolean isContained(float left, float top, float right, float bottom)
	{
		return !(left < l || right >= r || top < t || bottom >= b);
	}
	
	public boolean isIntersecting(Vector center, float radius)
	{
		return isIntersecting( center.x - radius, center.y - radius, center.x + radius, center.y + radius );
	}
	
	public boolean isIntersecting(float left, float top, float right, float bottom)
	{
		return !(left >= r || right < l || top >= b || bottom < t);
	}
	
}
