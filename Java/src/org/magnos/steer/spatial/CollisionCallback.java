package org.magnos.steer.spatial;


public interface CollisionCallback
{
	public void onCollisionStart();
	public void onCollision(SpatialEntity entity, SpatialEntity collidedWith, float overlap, int index, boolean second);
	public void onCollisionEnd();
}
