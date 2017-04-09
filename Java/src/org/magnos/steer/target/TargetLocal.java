package org.magnos.steer.target;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Target;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;



public class TargetLocal<V extends Vec<V>> implements Target<V>
{

	public Target<V> target;
	public float minimum;
	public float maximum;
	
	public TargetLocal(Target<V> target, float maximum)
	{
		this( target, 0, maximum );
	}
	
	public TargetLocal(Target<V> target, float minimum, float maximum)
	{
		this.target = target;
		this.minimum = minimum;
		this.maximum = maximum;
	}
	
	@Override
	public SpatialEntity<V> getTarget( SteerSubject<V> subject )
	{
	    SpatialEntity<V> actual = target.getTarget( subject );
		
		if ( actual == null )
		{
			return null;
		}
		
		float distanceSq = actual.getPosition().distanceSq( subject.getPosition() );
		
		if (minimum != 0 && distanceSq < minimum * minimum)
		{
			return null;
		}
		
		if (maximum != Float.MAX_VALUE && distanceSq > maximum * maximum)
		{
			return null;
		}
		
		return actual;
	}

}
