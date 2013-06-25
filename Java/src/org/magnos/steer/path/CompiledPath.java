package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.Vector;

public class CompiledPath extends JumpPath
{
		
	public CompiledPath()
	{
	}
	
	public CompiledPath( Path path, Vector[] allocated )
	{
		super( compile( path, allocated ) );
	}
	
	public static <T> Vector[] compile( Path path, Vector[] allocated)
	{
		int n = allocated.length - 1;
		
		for ( int i = 0; i <= n; i++ )
		{
			path.set( allocated[i], (float)i / n );
		}
		
		return allocated;
	}
	
}
