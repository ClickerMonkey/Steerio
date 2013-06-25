package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.Vector;



public class TargetInterpose implements Target
{

	public static final float DEFAULT_DELTA = 0.5f;
	
	public Target target0;
	public Target target1;
	public float delta;
	
	public final Vector interpose = new Vector();
	
	public TargetInterpose(Target target0, Target target1)
	{
		this( target0, target1, DEFAULT_DELTA );
	}
	
	public TargetInterpose(Target target0, Target target1, float delta)
	{
		this.target0 = target0;
		this.target1 = target1;
		this.delta = delta;
	}
	
	@Override
	public Vector getTarget( SteerSubject subject )
	{
		Vector t0 = target0.getTarget( subject );
		
		if ( t0 == null )
		{
			return null;
		}
		
		Vector t1 = target1.getTarget( subject );
		
		if ( t1 == null )
		{
			return null;
		}
		
		interpose.interpolate( t0, t1, delta );
		
		return interpose;
	}

}
