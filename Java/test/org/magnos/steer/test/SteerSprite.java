
package org.magnos.steer.test;

import java.awt.Color;
import java.awt.Graphics2D;

import org.magnos.steer.BaseSteerSubject;
import org.magnos.steer.Steer;
import org.magnos.steer.behavior.SteerBasicExample;
import org.magnos.steer.vec.Vec2;

import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.core.Entity;


public class SteerSprite extends BaseSteerSubject<Vec2> implements Entity
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

	public SteerSprite( Color color, float radius, float velocityMax, float accelerationMax, Steer<Vec2> steer )
	{
		super( Vec2.ZERO, radius, velocityMax, accelerationMax, steer );

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
		SteerBasicExample.drawVector( gr, color, position, direction, radius * 1.2f, true );
		SteerBasicExample.drawCircle( gr, color, position, radius, true );
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
