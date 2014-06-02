package org.magnos.steer.behavior;

import org.magnos.steer.SteerSubject;
import org.magnos.steer.vec.Vec3;


public class SteerFlowField3 extends AbstractSteer<Vec3>
{

	public Vec3[][][] field;
	public Vec3 fieldMin;
	public Vec3 fieldMax;
	public float lookahead;
	protected int cellsWide;
	protected int cellsHigh;
	protected int cellsDeep;
	protected float cellWidth;
	protected float cellHeight;
	protected float cellDepth;
	protected final Vec3 lookaheadPoint = new Vec3();
    private final Vec3 temp0 = new Vec3();
    private final Vec3 temp1 = new Vec3();
    private final Vec3 temp2 = new Vec3();
    private final Vec3 temp3 = new Vec3();
    private final Vec3 temp4 = new Vec3();
    private final Vec3 temp5 = new Vec3();
	
	public SteerFlowField3(float lookahead, Vec3 fieldMin, Vec3 fieldMax, Vec3[][][] field)
	{
		setField( lookahead, fieldMin, fieldMax, field );
	}
	
	public void setField(float lookahead, Vec3 fieldMin, Vec3 fieldMax, Vec3[][][] field)
	{
		this.lookahead = lookahead;
		this.fieldMin = fieldMin;
		this.fieldMax = fieldMax;
		this.field = field;
		this.cellsHigh = field.length;
		this.cellsWide = field[0].length;
		this.cellsDeep = field[0][0].length;
		this.cellWidth = (fieldMax.x - fieldMin.x) / (cellsWide - 1);
        this.cellHeight = (fieldMax.y - fieldMin.y) / (cellsHigh - 1);
        this.cellDepth = (fieldMax.z - fieldMin.z) / (cellsDeep - 1);
	}
	
	@Override
	public void getForce( float elapsed, SteerSubject<Vec3> subject, Vec3 out )
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
            int z = (int)Math.floor( lookaheadPoint.z / cellHeight );
			float dx = (lookaheadPoint.x - (x * cellWidth)) / cellWidth;
            float dy = (lookaheadPoint.y - (y * cellHeight)) / cellHeight;
            float dz = (lookaheadPoint.z - (z * cellDepth)) / cellDepth;

            Vec3 NTL = field[y][x][z];
            Vec3 NTR = field[y][x+1][z];
            Vec3 NBL = field[y+1][x][z];
            Vec3 NBR = field[y+1][x+1][z];
            Vec3 FTL = field[y][x][z+1];
            Vec3 FTR = field[y][x+1][z+1];
            Vec3 FBL = field[y+1][x][z+1];
            Vec3 FBR = field[y+1][x+1][z+1];

            temp0.interpolatei( NTL, NTR, dx );
            temp1.interpolatei( NBL, NBR, dx );
            temp2.interpolatei( FTL, FTR, dx );
            temp3.interpolatei( FBL, FBR, dx );
			
			temp4.interpolatei( temp0, temp1, dy );
			temp5.interpolatei( temp2, temp3, dy );
			
			out.interpolatei( temp4, temp5, dz );
			
			maximize( subject, out );
		}
	}

	@Override
	public boolean isShared()
	{
		return true;
	}

}
