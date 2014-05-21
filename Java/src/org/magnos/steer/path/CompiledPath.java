
package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.vec.Vec;


public class CompiledPath<V extends Vec<V>> extends JumpPath<V>
{

    public CompiledPath()
    {
    }

    public CompiledPath( Path<V> path, V[] allocated )
    {
        super( compile( path, allocated ) );
    }

    public static <V extends Vec<V>> V[] compile( Path<V> path, V[] allocated )
    {
        int n = allocated.length - 1;

        for ( int i = 0; i <= n; i++ )
        {
            path.set( allocated[i], (float)i / n );
        }

        return allocated;
    }

}
