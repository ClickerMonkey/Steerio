
package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.vec.Vec;


public class UniformPath<V extends Vec<V>> extends TimedPath<V>
{

    public UniformPath( Path<V> path, V[] allocated )
    {
        setPoints( CompiledPath.compile( path, allocated ) );
        setTimes( LinearPath.getTimes( points ) );
    }

}
