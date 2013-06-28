package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.Vector;



public class TargetOffset implements Target
{

	public Vector direction;
	public Vector position;
	public Vector offset;
	public boolean relative;
	
	public final Vector actual = new Vector(); 
	
	public TargetOffset(SteerSubject target, Vector offset, boolean relative)
	{
		this( target.getPosition(), target.getDirection(), offset, relative );
	}
	
	public TargetOffset(Vector position, Vector direction, Vector offset, boolean relative)
	{
		this.position = position;
		this.direction = direction;
		this.offset = offset;
		this.relative = relative;
	}

	@Override
	public Vector getTarget(SteerSubject subject)
	{
		actual.set( offset );
		
		if (relative)
		{
			actual.rotatei( direction.angle() );
		}
		
		actual.addi( position );
		
		return actual;
	}
	
}
