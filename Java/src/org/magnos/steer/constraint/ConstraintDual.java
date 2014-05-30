
package org.magnos.steer.constraint;

import org.magnos.steer.Constraint;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec;


public class ConstraintDual<V extends Vec<V>> implements Constraint<V>
{

    public Constraint<V> first;
    public Constraint<V> second;

    public ConstraintDual( Constraint<V> first, Constraint<V> second )
    {
        this.first = first;
        this.second = second;
    }

    @Override
    public void constrain( float elapsed, SteerSubject<V> subject )
    {
        first.constrain( elapsed, subject );
        second.constrain( elapsed, subject );
    }

}
