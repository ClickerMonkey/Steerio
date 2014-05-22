package org.magnos.steer.test;

import org.junit.Test;
import org.magnos.steer.BaseSteerSubject;
import org.magnos.steer.vec.Vec2;


public class TestGetDistanceAndNormal
{
    
    @Test
    public void test()
    {
        BaseSteerSubject<Vec2> a = new BaseSteerSubject<Vec2>( Vec2.FACTORY, 2, 1000 );
        a.position.set( 5, 5 );
        a.velocity.set( 100, 0 );
        
        BaseSteerSubject<Vec2> b = new BaseSteerSubject<Vec2>( Vec2.FACTORY, 2, 1000 );
        b.position.set( 10, 0 );
        b.velocity.set( 0, 5 );
        
        Vec2 normal = new Vec2();
        float distance = a.getDistanceAndNormal( b.position, b.position.add( b.velocity ), normal );
        
        System.out.println( distance );
        System.out.println( normal );
    }

}
