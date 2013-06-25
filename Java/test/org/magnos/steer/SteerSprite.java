
package org.magnos.steer;

import java.awt.Color;
import java.awt.Graphics2D;

import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.core.Entity;


public class SteerSprite extends BaseSteerSubject implements Entity
{

	public Color color;

	public SteerSprite( Color color, float radius, float max )
	{
		this( color, radius, max, max, null );
	}

	public SteerSprite( Color color, float radius, float velocityMax, float accelerationMax )
	{
		this( color, radius, velocityMax, accelerationMax, null );
	}

	public SteerSprite( Color color, float radius, float velocityMax, float accelerationMax, Steer steer )
	{
		super( radius, velocityMax, accelerationMax, steer );

		this.color = color;
	}

	@Override
	public void update( GameState state, Scene scene )
	{
		update( state.seconds );
	}

	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		SteerBasicExample.drawVector( gr, color, position, direction, radius * 1.2f, false );
		SteerBasicExample.drawCircle( gr, color, position, radius, false );
	}

	@Override
	public boolean isExpired()
	{
		return isInert();
	}

	@Override
	public void expire()
	{
		inert = true;
	}

	@Override
	public void onExpire()
	{

	}

}
