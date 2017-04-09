package org.magnos.steer.behavior;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.vec.Vec;


public class SteerMany<V extends Vec<V>> extends AbstractSteerSpatial<V, SteerMany<V>>
{

    public V force;
    public V towards;

    @Override
    public float getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        force = out;
        
        int total = search( subject );
        
        if ( total > 0 )
        {
            return forceFromVector( this, force );
        }
        
        return 0;
    }

    @Override
    protected boolean onFoundInView( SpatialEntity<V> entity, float overlap, int index, V queryOffset, float queryRadius, int queryMax, long queryGroups, float delta )
    {
        towards.directi( entity.getPosition(), queryOffset );
        towards.length( calculateMagnitude( delta ) );
        
        // accumulator.accumulate
        
        
        return true;
    }

}
