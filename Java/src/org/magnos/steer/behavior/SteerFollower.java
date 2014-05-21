
package org.magnos.steer.behavior;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec;


public class SteerFollower<V extends Vec<V>> extends AbstractSteer<V>
{

    public SteerSubject<V> leader;
    public float distance;
    public boolean shared;

    private V future;
    private V closest;

    public SteerFollower( SteerSubject<V> leader, float distance, V template )
    {
        this( leader, distance, true, template );
    }

    public SteerFollower( SteerSubject<V> leader, float distance, boolean shared, V template )
    {
        this.leader = leader;
        this.distance = distance;
        this.shared = shared;
        this.future = template.create();
        this.closest = template.create();
    }

    @Override
    public void getForce( float elapsed, SteerSubject<V> subject, V out )
    {
        future.set( leader.getPosition() );
        future.add( leader.getVelocity() );

        SteerMath.closest( leader.getPosition(), future, subject.getPosition(), closest );

        float distanceSq = closest.dot( subject.getPosition() );

        if ( distanceSq <= distance * distance )
        {
            away( subject, closest, out, this );
        }
    }

    @Override
    public boolean isShared()
    {
        return shared;
    }

}
