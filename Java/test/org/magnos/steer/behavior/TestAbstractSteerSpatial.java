package org.magnos.steer.behavior;

import static org.junit.Assert.*;

import org.junit.Test;
import org.magnos.steer.AssertSpatialEntity;
import org.magnos.steer.AssertSteerSpatial;
import org.magnos.steer.BaseSteerSubject;
import org.magnos.steer.BaseSteerTest;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.array.SpatialArray;
import org.magnos.steer.vec.Vec2;

public class TestAbstractSteerSpatial extends BaseSteerTest
{
    
    @Test
    public void testNoIntersection()
    {
        SpatialDatabase<Vec2> db = new SpatialArray<Vec2>( 16 );
        AssertSpatialEntity e0 = newSpatialEntity( 100, 0, 10 );
        
        db.add( e0 );
        
        BaseSteerSubject<Vec2> ss = newSteerSubject( 100, 20, 5, 200 );
        
        AssertSteerSpatial spatial = new AssertSteerSpatial( 0, 100, db, 10, 5, -1, 10, null, true );
        
        Vec2 outVector = new Vec2();
        float outForce = spatial.getForce( 0.01f, ss, outVector );
        
        assertEquals( 0.0f, outForce, 0.0001 );
    }

    @Test
    public void testDelta()
    {
        SpatialDatabase<Vec2> db = new SpatialArray<Vec2>( 16 );
        AssertSpatialEntity e0 = newSpatialEntity( 100, 0, 10 );
        
        db.add( e0 );
        
        BaseSteerSubject<Vec2> ss = newSteerSubject( 100, 20, 15, 200 );

        AssertSteerSpatial spatial = new AssertSteerSpatial( 0, 100, db, 15, 5, -1, 10, null, true );
        spatial.assertOverlap = new float[] { 5.0f };
        spatial.assertDelta = new float[] { 0.5f };
        spatial.assertTotal = 1;
        
        Vec2 outVector0 = new Vec2();
        float outForce0 = spatial.getForce( 0.01f, ss, outVector0 );

        assertEquals( 0.0f, outVector0.x, 0.0001 );
        assertEquals( 1.0f, outVector0.y, 0.0001 );
        assertEquals( 50.0f, outForce0, 0.0001 );
        
        ss.position.y = 4;
        spatial.assertOverlap = new float[] { 21.0f };
        spatial.assertDelta = new float[] { 1.0f };
        
        Vec2 outVector1 = new Vec2();
        float outForce1 = spatial.getForce( 0.01f, ss, outVector1 );

        assertEquals( 0.0f, outVector1.x, 0.0001 );
        assertEquals( 1.0f, outVector1.y, 0.0001 );
        assertEquals( 100.0f, outForce1, 0.0001 );
    }
    
}
