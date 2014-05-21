package org.magnos.steer.spatial;

import org.magnos.steer.vec.Vec;


public interface CollisionCallback<V extends Vec<V>>
{
	public void onCollisionStart();
	public void onCollision(SpatialEntity<V> entity, SpatialEntity<V> collidedWith, float overlap, int index, boolean second);
	public void onCollisionEnd();
}
