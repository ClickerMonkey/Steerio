package org.magnos.steer.test;


public class Timer
{

	public long time;
	public long count;
	public long duration;
	public double elapsed;
	public double total;
	public double average;
	public double min;
	public double max;
	
	public Timer()
	{
		reset();
	}
	
	public void reset()
	{
		time = 0;
		count = 0;
		duration = 0;
		elapsed = 0.0;
		total = 0.0;
		average = 0.0;
		min = Double.MAX_VALUE;
		max = -Double.MAX_VALUE;
	}
	
	public void start()
	{
		time = System.nanoTime();
	}
	
	public void stop()
	{
		duration = System.nanoTime() - time;
		elapsed = duration * 0.000000001; 
		total += elapsed;
		count++;
		average = total / count;
		min = Math.min( min, elapsed );
		max = Math.max( max, elapsed );
	}
	
	public void print()
	{
		System.out.format( "\tTotal Iterations: %d\n", count );
		System.out.format( "\tAverage: %.9f\n", average );
		System.out.format( "\tMin: %.9f\n", min );
		System.out.format( "\tMax: %.9f\n", max );
	}
	
}
