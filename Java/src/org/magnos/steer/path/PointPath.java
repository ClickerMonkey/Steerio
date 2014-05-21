package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.vec.Vec;


public class PointPath<V extends Vec<V>> implements Path<V>
{
	
	public V value;
	
	public PointPath(V value)
	{
		this.value = value;
	}

	@Override
	public V set( V subject, float delta )
	{
		subject.set( value );
		
		return subject;
	}

}
