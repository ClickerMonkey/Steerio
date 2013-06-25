package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.Vector;

public class UniformPath extends TimedPath
{

	public UniformPath( Path path, int attributeCount )
	{
		setPoints( CompiledPath.compile( path, new Vector[ attributeCount ] ) );
		setTimes( LinearPath.getTimes( points ) );
	}

}
