package org.magnos.steer;

/**
 * A steering behavior which encapsulates an array of steering behaviors 
 * prioritized by their order in the array. A maximum value can be given, if
 * the accumulated force from the steerings is greater than the maximum value
 * then the accumulation ceases (this creates prioritization). {@link SteerSet}
 * can be shared if all steering behaviors can also be shared, otherwise a
 * clone must be made to share this set with another subject.
 */
public class SteerSet implements Steer
{
	public float maximum;
	public Steer[] steerings;
	
	protected Vector force = new Vector();
	
	public SteerSet()
	{
		this(0, INFINITE);
	}
	
	public SteerSet(float maximum)
	{
		this(0, maximum);
	}
	
	public SteerSet(int steerCount, float maximum)
	{
		this.steerings = new Steer[steerCount];
		this.maximum = maximum;
	}
	
	public SteerSet(Steer ... steerings)
	{
		this(INFINITE, steerings);
	}
	
	public SteerSet(float maximum, Steer ... steerings )
	{
		this.maximum = maximum;
		this.steerings = steerings;
	}
	
	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		force.clear();

		float maximumSq = maximum * maximum;
		
		final int steerCount = steerings.length;
		for (int i = 0; i < steerCount; i++)
		{
			Steer steer = steerings[i];
			
			if (steer != null)
			{
				force.addi( steer.getForce( elapsed, subject ) );
				
				if (maximum != INFINITE && force.lengthSq() > maximumSq)
				{
					force.clamp( 0, maximum );
					
					break;
				}	
			}
		}
		
		return force;
	}

	@Override
	public boolean isShared()
	{
		final int steerCount = steerings.length;
		for (int i = 0; i < steerCount; i++)
		{
			Steer steer = steerings[i];
			if (steer != null && !steer.isShared())
			{
				return false;
			}
		}
		
		return true;
	}

	@Override
	public Steer clone()
	{
		int steerCount = steerings.length;
		
		SteerSet cloned = new SteerSet();
		cloned.maximum = maximum;
		cloned.steerings = new Steer[steerCount];
		
		for (int i = 0; i < steerCount; i++)
		{
			Steer s = steerings[i]; 
			
			cloned.steerings[i] = (s == null || s.isShared()) ? s : s.clone();
		}
		
		return cloned;
	}

	public void add(Steer steer)
	{
		steerings = SteerMath.add( steer, steerings );
	}
	
	public int size()
	{
		return steerings.length;
	}

}