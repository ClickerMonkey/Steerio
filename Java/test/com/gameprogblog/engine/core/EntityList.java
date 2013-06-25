package com.gameprogblog.engine.core;

import java.awt.Graphics2D;
import java.util.Arrays;

import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;


public class EntityList<E extends Entity> implements Entity
{
	public static int DEFAULT_CAPACITY = 16;
	
	private E[] entities;
	private int size;
	private boolean expired;
	
	public EntityList()
	{
		this( DEFAULT_CAPACITY );
	}
	
	@SuppressWarnings("unchecked")
	public EntityList( int initialCapacity )
	{
		this( (E[]) new Entity[ initialCapacity ] );
	}
	
	public EntityList( E[] entities )
	{
		this.entities = entities;
	}
	
	protected void onUpdated( E entity, GameState state, Scene scene )
	{
		
	}
	
	protected void onExpired( E entity, GameState state, Scene scene )
	{
		
	}
	
	protected void onAdd( E entity )
	{
		
	}
	
	@Override
	public void draw( GameState state, Graphics2D gr, Scene scene )
	{
		for ( int i = 0; i < size; i++ )
		{
			entities[i].draw( state, gr, scene );
		}
	}
	
	@Override
	public void update( GameState state, Scene scene )
	{
		int alive = 0;
		
		for ( int i = 0; i < size; i++ )
		{
			E entity = entities[i];
			
			if ( !entity.isExpired() )
			{
				entity.update( state, scene );	
			}
			
			if ( entity.isExpired() )
			{
				entity.onExpire();
				
				onExpired( entity, state, scene );
			}
			else
			{
				entities[alive++] = entity;
				
				onUpdated( entity, state, scene );
			}
		}
		
		while ( size > alive )
		{
			entities[--size] = null;
		}
	}
	
	@Override
	public boolean isExpired()
	{
		return expired;
	}
	
	@Override
	public void expire()
	{
		expired = true;
	}
	
	@Override
	public void onExpire()
	{
		for ( int i = 0; i < size; i++ )
		{
			entities[i].onExpire();
		}
	}
	
	public int size()
	{
		return size;
	}
	
	public int capacity()
	{
		return entities.length;
	}
	
	public int available()
	{
		return (entities.length - size);
	}
	
	public void pad( int count )
	{
		final int capacity = entities.length;
		
		if ( size + count >= capacity )
		{
			int nextSize = capacity + (capacity >> 1);
			int minimumSize = size + count;
			
			entities = Arrays.copyOf( entities, Math.max( nextSize, minimumSize ) );
		}
	}
	
	public <T extends E> void add(T[] entityArray, int offset, int length)
	{
		pad( length );
		
		System.arraycopy( entityArray, offset, entities, size, length );
		
		for ( int i = 0; i < length; i++ )
		{
			onAdd( entityArray[offset + i] );
		}
		
		size += length;
	}
	
	public <T extends E> void add(T[] entityArray)
	{
		add( entityArray, 0, entityArray.length );
	}
	
	public <T extends E> void add(EntityList<T> entityList)
	{
		add( entityList.entities, 0, entityList.size );
	}

	public void add(E entity)
	{
		pad( 1 );

		entities[size++] = entity;
		
		onAdd( entity );
	}
	
	public E get(int i)
	{
		return entities[i];
	}
	
}
