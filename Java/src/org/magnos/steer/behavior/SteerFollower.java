
package org.magnos.steer.behavior;

import org.magnos.steer.Steer;
import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec;


public class SteerFollower<V extends Vec<V>> extends AbstractSteer<V, SteerFollower<V>>
{

    public SteerSubject<V> leader;
    public float distance;
    public boolean shared;

    private V future;
    private V closest;

    public SteerFollower( float minimum, float maximum, SteerSubject<V> leader, float distance, V template )
    {
        this( minimum, maximum, leader, distance, DEFAULT_SHARED, template );
    }

    public SteerFollower( float magnitude, SteerSubject<V> leader, float distance, V template )
    {
        this( magnitude, magnitude, leader, distance, DEFAULT_SHARED, template );
    }

    public SteerFollower( float magnitude, SteerSubject<V> leader, float distance, boolean shared, V template )
    {
        this( magnitude, magnitude, leader, distance, shared, template );
    }

    public SteerFollower( float minimum, float maximum, SteerSubject<V> leader, float distance, boolean shared, V template )
    {
        super( minimum, maximum );
        
        this.leader = leader;
        this.distance = distance;
        this.shared = shared;
        this.future = template.create();
        this.closest = template.create();
    }

    @Override
    public float getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        future.set( leader.getPosition() );
        future.addi( leader.getVelocity() );

        SteerMath.closest( leader.getPosition(), future, subject.getPosition(), closest );

        float distanceSq = closest.dot( subject.getPosition() );

        if ( distanceSq <= distance * distance )
        {
            return away( subject, closest, out, this );
        }
        
        return Steer.NONE;
    }

    @Override
    public boolean isShared()
    {
        return shared;
    }

}
