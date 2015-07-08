package org.magnos.steer;

import org.magnos.steer.vec.Vec2;



public class BaseSteerTest
{
    
    public AssertSpatialEntity newSpatialEntity(float x, float y, float radius)
    {
        AssertSpatialEntity e = new AssertSpatialEntity();
        
        e.position.x = x;
        e.position.y = y;
        e.radius = radius;
        
        return e;
    }
    
    public BaseSteerSubject<Vec2> newSteerSubject(float x, float y, float radius, float velocityMax)
    {
        BaseSteerSubject<Vec2> s = new BaseSteerSubject<Vec2>( Vec2.FACTORY, radius, velocityMax );
        
        s.position.set( x, y );
        
        return s;
    }
    
}
