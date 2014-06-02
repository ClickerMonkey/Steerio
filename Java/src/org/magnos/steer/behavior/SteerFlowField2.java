package org.magnos.steer.behavior;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec2;


public class SteerFlowField2 extends AbstractSteer<Vec2>
{

	public Vec2[][] field;
	public Vec2 fieldMin;
	public Vec2 fieldMax;
	public float lookahead;
	protected int cellsWide;
	protected int cellsHigh;
	protected float cellWidth;
	protected float cellHeight;
	protected final Vec2 lookaheadPoint = new Vec2();
	private final Vec2 temp0 = new Vec2();
	private final Vec2 temp1 = new Vec2();
	
	public SteerFlowField2(float lookahead, Vec2 fieldMin, Vec2 fieldMax, Vec2[][] field)
	{
		setField( lookahead, fieldMin, fieldMax, field );
	}
	
	public void setField(float lookahead, Vec2 fieldMin, Vec2 fieldMax, Vec2[][] field)
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
	public void getForce( float elapsed, SteerSubject<Vec2> subject, Vec2 out )
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

			Vec2 TL = field[y][x];
			Vec2 TR = field[y][x+1];
			Vec2 BL = field[y+1][x];
			Vec2 BR = field[y+1][x+1];
			
			temp0.interpolatei( TL, TR, dx );
			temp1.interpolatei( BL, BR, dx );
			
			out.interpolatei( temp0, temp1, dy );
			
			maximize( subject, out );
		}
	}

	@Override
	public boolean isShared()
	{
		return true;
	}

}
