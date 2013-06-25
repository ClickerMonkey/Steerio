package org.magnos.steer.path;

import org.magnos.steer.Path;
import org.magnos.steer.Vector;


public class HermitePath implements Path
{

	public Vector start;
	public Vector startTangent;
	public Vector end;
	public Vector endTangent;
	
	public HermitePath()
	{
	}
	
	public HermitePath(Vector start, Vector startTangent, Vector end, Vector endTangent)
	{
		this.start = start;
		this.startTangent = startTangent;
		this.end = end;
		this.endTangent = endTangent;
	}
	
	@Override
	public Vector set( Vector subject, float d )
	{
		float d2 = d * d;
		float d3 = d2 * d;
		
		subject.clear();
		subject.addsi( start, 2 * d3 - 3 * d2 + 1 );
		subject.addsi( end, -2 * d3 + 3 * d2 );
		subject.addsi( startTangent, d3 - 2 * d2 + d );
		subject.addsi( endTangent, d3 - d2 );
		
		return subject;
	}

}
