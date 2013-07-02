package org.magnos.steer;

public class Segment
{

	public static final int FRONT = 1;
	public static final int TOP = 0;
	public static final int BACK = -1;
	public static final int OVERLAP = 2;
	
	public float x0, y0;
	public float x1, y1;
	public float dx, dy;
	public float a, b, c;
	public float length, invLength, lengthSq;
	
	public Segment()
	{
	}

	public Segment( Vector s, Vector e )
	{
		this( s.x, s.y, e.x, e.y );
	}
	
	public Segment(float x0, float y0, float x1, float y1)
	{
		set( x0, y0, x1, y1 );
	}
	
	public void set(float x0, float y0, float x1, float y1)
	{
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
		this.dx = (x1 - x0);
		this.dy = (y1 - y0);
		this.lengthSq = dx * dx + dy * dy;
		this.length = SteerMath.sqr( lengthSq );
		this.invLength = 1.0f / length;
		this.a = -dy * invLength;
		this.b = dx * invLength;
		this.c = -(a * x0 + b * y0);
	}
	
	public float distanceSq(Vector v) 
	{
		return distanceSq( v.x, v.y );
	}
	
	public float distanceSq(float vx, float vy) 
	{
		float d = (a * vx + b * vy + c); 
		return (d * d);
	}
	
	public float distance(Vector v)
	{
		return (a * v.x + b * v.y + c); 
	}
	
	public float distance(float x, float y)
	{
		return (a * x + b * y + c);
	}
	
	public boolean intersects( Vector circle, float radius, boolean withinPoints )
	{
		Vector v = closest( circle, withinPoints, new Vector() );
		
		return ( v.distanceSq( circle ) < radius * radius );
	}
	
	public boolean intersection(Segment p, boolean withinPoints, Vector out)
	{
		float div = (a * p.b - b * p.a);
		
		if ( div == 0 ) {
			return false;
		}
		
		div = 1 / div;
		out.x = (-c * p.b + b * p.c) * div;
		out.y = (-a * p.c + c * p.a) * div;
		
		return !withinPoints || (insideBox( out ) && p.insideBox( out ));
	}
	
	public float distanceSq(Segment p, Vector closest, Vector pclosest)
	{
		float denom = (dx * p.dy) - (dy * p.dx);
		
		if ( denom == 0.0f ) 
		{
			return -1.0f;
		}
		
		float cx = p.x0 - x0;
		float cy = p.y0 - y0;
		float t = SteerMath.clamp( (cx * p.dy - cy * p.dx) / denom, 0f, 1f );
		float u = SteerMath.clamp( (cx * dy - cy * dx) / denom, 0f, 1f );
		float tx = x0 + t * dx;
		float ty = y0 + t * dy;
		float ux = p.x0 + u * p.dx;
		float uy = p.y0 + u * p.dy;
		float tux = tx - ux;
		float tuy = ty - uy;
		
		if ( closest != null) {
			closest.set( tx, ty );
		}
		
		if ( pclosest != null ) { 
			pclosest.set( ux, uy );
		}
		
		return (tux * tux + tuy * tuy);
	}
	
	public boolean insideBox( Vector p )
	{
		return (p.x >= Math.min( x0, x1 ) && 
			     p.x <= Math.max( x0, x1 ) && 
			     p.y >= Math.min( y0, y1 ) && 
			     p.y <= Math.max( y0, y1 ));
	}
	
	public int sign(Vector v)
	{
		return sign( v.x, v.y );
	}
	
	public int sign(float vx, float vy)
	{
		float e = distance( vx, vy );
		
		if ( e > SteerMath.EPSILON ) 
		{
			return FRONT;
		}
		else if (e < -SteerMath.EPSILON) 
		{
			return BACK;
		}
		
		return TOP;
	}

	public boolean isParallel(Segment w)
	{
		return ((a * w.b) - (b * w.a)) == 0.0f;
	}
	
	public int eval(Vector start, Vector end)
	{
		int s = sign( start );
		int e = sign( end );
		
		if (s == e) 
		{
			return s;
		}
		else if (s == TOP) 
		{
			return e;
		}
		else if (e == TOP) 
		{
			return s;
		}
		
		return OVERLAP;
	}
	
	public float radius() 
	{
		return length * 0.5f;
	}
	
	public float delta(Vector v)
	{
		return delta( v.x, v.y );
	}
	
	public float delta(float x, float y) 
	{
		return ((x - x0) * dx + (y - y0) * dy) / lengthSq;
	}
	
	public Vector closest(Vector v, boolean withinPoints, Vector out)
	{
		float d = delta( v.x, v.y );
		
		if (withinPoints)
		{
			d = SteerMath.clamp( d, 0.0f, 1.0f );
		}
		
		out.x = (x0 + d * dx);
		out.y = (y0 + d * dy);
		
		return out;
	}
	
	public Vector normal(Vector v, boolean withinPoints, Vector out)
	{
		closest( v, withinPoints, out );
		out.subi( v );
		out.negi();
		out.normalize();
		return out;
	}
	
	public Vector getStart()
	{
		return getStart( new Vector() );
	}
	
	public Vector getStart(Vector out)
	{
		return out.set( x0, y0 );
	}
	
	public Vector getEnd()
	{
		return getEnd( new Vector() );
	}
	
	public Vector getEnd(Vector out)
	{
		return out.set( x1, y1 );
	}
	
}
