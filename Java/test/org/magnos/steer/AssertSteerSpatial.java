
package org.magnos.steer;

import static org.junit.Assert.assertEquals;

import org.magnos.steer.behavior.AbstractSteerSpatial;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec2;

public class AssertSteerSpatial extends AbstractSteerSpatial<Vec2, AssertSteerSpatial>
{

    public float[] assertOverlap = {};
    public float[] assertDelta = {};
    public int assertTotal = 0;
    
    public Vec2 force = new Vec2();

    public AssertSteerSpatial( float minimum, float maximum, SpatialDatabase<Vec2> space, float minimumRadius, float maximumRadius, long groups, int max, Filter<Vec2> filter, boolean shared )
    {
        super( minimum, maximum, space, minimumRadius, maximumRadius, groups, max, filter, shared );
    }
    
    @Override
    public float getForce( float elapsed, SteerSubject<Vec2> ss, Vec2 out )
    {
        subject = ss;
        force.clear();
        
        int total = search( subject );

        assertEquals( assertTotal, total );

        return out.set( force ).normalize();
    }

    @Override
    protected boolean onFoundInView( SpatialEntity<Vec2> entity, float overlap, int index, Vec2 queryOffset, float queryRadius, int queryMax, long queryGroups, float delta )
    {
        assertEquals( assertOverlap[index], overlap, 0.0001 );
        assertEquals( assertDelta[index], delta, 0.0001 );

        Vec2 sub = queryOffset.sub( entity.getPosition() );
        sub.length( delta * ( maximum - minimum ) + minimum );
        
        force.addi( sub );
        
        return true;
    }
}
