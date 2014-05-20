package org.magnos.steer;


/**
 * A steering behavior which can periodically update a another steering 
 * behavior, apply a weight to the resulting force, and clamp the resulting 
 * force's magnitude to a maximum value.
 */
public class SteerModifier implements Steer
{
	public Steer steer;
	public float maximum;
	public float weight;
	public float update;
	public boolean enabled;
	
	protected boolean maximized = true;
	protected Vector force = new Vector();
	protected float time;
	
	public SteerModifier(Steer steer, float weight)
	{
		this( steer, INFINITE, weight, 0 );
	}
	
	public SteerModifier(Steer steer, float maximum, float weight)
	{
		this( steer, maximum, weight, 0 );
	}
	
	public SteerModifier(Steer steer, float maximum, float weight, float update)
	{
		this.steer = steer;
		this.maximum = maximum;
		this.weight = weight;
		this.update = update;
		this.enabled = true;
	}
	
	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		force.clear();
		
		time += elapsed;
		
		if (time >= update && enabled)
		{
			force.set( steer.getForce( time, subject ) );
			force.muli( weight );
			
			if (maximum != INFINITE)
			{
				force.max( maximum );
			}
			
			time = Math.max(0, time - update);
		}
		
		return force;
	}
	
	@Override
	public boolean isShared()
	{
		return false;
	}
	
    @Override
    public boolean isMaximized()
    {
        return maximized;
    }
    
    @Override
    public void setMaximized(boolean maximize)
    {
        this.maximized = maximize;
    }

	@Override
	public Steer clone()
	{
		return new SteerModifier( steer.isShared() ? steer : steer.clone(), maximum, weight, update );
	}
	
}
