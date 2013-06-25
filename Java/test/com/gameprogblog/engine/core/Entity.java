package com.gameprogblog.engine.core;

import java.awt.Graphics2D;

import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public interface Entity
{
	public void update( GameState state, Scene scene );
	public void draw( GameState state, Graphics2D gr, Scene scene );
	public boolean isExpired();
	public void expire();
	public void onExpire();
}
