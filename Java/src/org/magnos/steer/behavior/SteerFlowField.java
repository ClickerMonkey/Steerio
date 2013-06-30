package org.magnos.steer.behavior;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.Vector;


public class SteerFlowField extends AbstractSteer
{

	public Vector[][] field;
	public Vector fieldMin;
	public Vector fieldMax;
	public float lookahead;
	protected int cellsWide;
	protected int cellsHigh;
	protected float cellWidth;
	protected float cellHeight;
	protected final Vector lookaheadPoint = new Vector();
	private final Vector temp0 = new Vector();
	private final Vector temp1 = new Vector();
	
	public SteerFlowField(float lookahead, Vector fieldMin, Vector fieldMax, Vector[][] field)
	{
		setField( lookahead, fieldMin, fieldMax, field );
	}
	
	public void setField(float lookahead, Vector fieldMin, Vector fieldMax, Vector[][] field)
	{
		this.lookahead = lookahead;
		this.fieldMin = fieldMin;
		this.fieldMax = fieldMax;
		this.field = field;
		this.cellsHigh = field.length;
		this.cellsWide = field[0].length;
		this.cellWidth = (fieldMax.x - fieldMin.x) / (cellsWide - 1);
		this.cellHeight = (fieldMax.y - fieldMin.y) / (cellsHigh - 1);
	}
	
	@Override
	public Vector getForce( float elapsed, SteerSubject subject )
	{
		lookaheadPoint.set( subject.getPosition() );
		lookaheadPoint.addsi( subject.getDirection(), lookahead );
		
		if ( lookaheadPoint.x >= fieldMin.x && 
			  lookaheadPoint.x < fieldMax.x && 
			  lookaheadPoint.y >= fieldMin.y && 
			  lookaheadPoint.y < fieldMax.y)
		{
			int x = (int)Math.floor( lookaheadPoint.x / cellWidth );
			int y = (int)Math.floor( lookaheadPoint.y / cellHeight );
			float dx = (lookaheadPoint.x - (x * cellWidth)) / cellWidth;
			float dy = (lookaheadPoint.y - (y * cellHeight)) / cellHeight;

			Vector TL = field[y][x];
			Vector TR = field[y][x+1];
			Vector BL = field[y+1][x];
			Vector BR = field[y+1][x+1];
			
			temp0.interpolate( TL, TR, dx );
			temp1.interpolate( BL, BR, dx );
			
			force.interpolate( temp0, temp1, dy );
			
			maximize( subject, force );
		}
		else
		{
			force.clear();
		}
		
		return force;
	}

	@Override
	public boolean isShared()
	{
		return true;
	}

}
