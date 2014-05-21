
package org.magnos.steer.spatial;

import org.magnos.steer.Obstacle;
import org.magnos.steer.vec.Vec;


public interface SpatialEntity<V extends Vec<V>> extends Obstacle<V>
{

	public V getPosition();

	public float getRadius();

	public long getSpatialGroups();
	
	public long getSpatialCollisionGroups();

	public boolean isStatic();

	public boolean isInert();

}
