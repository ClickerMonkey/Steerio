
package org.magnos.steer.vec;

import org.magnos.steer.SteerSubject;

/**
 * A dimension absent partial implementation of {@link Vec}.
 * 
 * @author Philip Diffenderfer
 * 
 */
public abstract class AbstractVec<V extends AbstractVec<V>> implements Vec<V>
{

    @Override
    public V clone()
    {
        return null;
    }

    @Override
    public V negi()
    {
        return neg( (V)this );
    }

    @Override
    public V neg()
    {
        return neg( create() );
    }

    @Override
    public V absi()
    {
        return abs( (V)this );
    }

    @Override
    public V abs()
    {
        return abs( create() );
    }

    @Override
    public V muli( float s )
    {
        return mul( s, (V)this );
    }

    @Override
    public V mul( float s )
    {
        return mul( s, create() );
    }

    @Override
    public V divi( float s )
    {
        return div( s, (V)this );
    }

    @Override
    public V div( float s )
    {
        return div( s, create() );
    }

    @Override
    public V addi( float s )
    {
        return add( s, (V)this );
    }

    @Override
    public V add( float s )
    {
        return add( s, create() );
    }

    @Override
    public V muli( V v )
    {
        return mul( v, (V)this );
    }

    @Override
    public V mul( V v )
    {
        return mul( v, create() );
    }

    @Override
    public V divi( V v )
    {
        return div( v, (V)this );
    }

    @Override
    public V div( V v )
    {
        return div( v, create() );
    }

    @Override
    public V addi( V v )
    {
        return add( v, (V)this );
    }

    @Override
    public V add( V v )
    {
        return add( v, create() );
    }

    @Override
    public V addsi( V v, float s )
    {
        return adds( v, s, (V)this );
    }

    @Override
    public V adds( V v, float s )
    {
        return adds( v, s, create() );
    }

    @Override
    public V subi( V v )
    {
        return sub( v, (V)this );
    }

    @Override
    public V sub( V v )
    {
        return sub( v, create() );
    }
    
    @Override
    public V modi( float s )
    {
        return mod( s, (V)this );
    }

    @Override
    public V mod( float s )
    {
        return mod( s, create() );
    }

    @Override
    public V modi( V v )
    {
        return mod( v, (V)this );
    }

    @Override
    public V mod( V v )
    {
        return mod( v, create() );
    }

    @Override
    public V directi( V origin, V target )
    {
        return direct( origin, target, (V)this );
    }

    @Override
    public V direct( V origin, V target )
    {
        return direct( origin, target, create() );
    }

    @Override
    public V interpolatei( V start, V end, float delta )
    {
        return interpolate( start, end, delta, (V)this );
    }

    @Override
    public V interpolate( V start, V end, float delta )
    {
        return interpolate( start, end, delta, create() );
    }

    @Override
    public V interpolateToi( V end, float delta )
    {
        return interpolateTo( end, delta, (V)this );
    }

    @Override
    public V interpolateTo( V end, float delta )
    {
        return interpolateTo( end, delta, create() );
    }

    @Override
    public V clampi( V min, V max )
    {
        return clamp( min, max, (V)this );
    }

    @Override
    public V clamp( V min, V max )
    {
        return clamp( min, max, create() );
    }

    @Override
    public boolean isUnit()
    {
        return lengthSq() == 1.0f;
    }

    @Override
    public boolean isUnit( float epsilon )
    {
        return Math.abs( lengthSq() - 1.0f ) < epsilon;
    }

    @Override
    public float length( float length )
    {
        float sq = lengthSq();
        float actual = length;

        if ( sq != 0.0 && sq != length * length )
        {
            actual = (float)Math.sqrt( sq );
            muli( length / actual );
        }

        return actual;
    }

    @Override
    public V clamp( float min, float max )
    {
        float sq = lengthSq();

        if ( sq != 0 )
        {
            if ( sq < min * min )
            {
                muli( min / (float)Math.sqrt( sq ) );
            }
            else if ( sq > max * max )
            {
                muli( max / (float)Math.sqrt( sq ) );
            }
        }

        return (V)this;
    }

    @Override
    public V min( float min )
    {
        float sq = lengthSq();

        if ( sq != 0 && sq < min * min )
        {
            muli( min / (float)Math.sqrt( sq ) );
        }

        return (V)this;
    }

    @Override
    public V max( float max )
    {
        float sq = lengthSq();

        if ( sq != 0 && sq > max * max )
        {
            muli( max / (float)Math.sqrt( sq ) );
        }
        return (V)this;
    }

    @Override
    public V rotatei( V cossin )
    {
        return rotate( cossin, (V)this );
    }

    @Override
    public V rotate( V cossin )
    {
        return rotate( cossin, create() );
    }

    @Override
    public V unrotatei( V cossin )
    {
        return unrotate( cossin, (V)this );
    }

    @Override
    public V unrotate( V cossin )
    {
        return unrotate( cossin, create() );
    }
    
    @Override
    public V floor()
    {
        return floor( create() );
    }

    @Override
    public V floori()
    {
        return floor( (V)this );
    }

    @Override
    public V ceil()
    {
        return ceil( create() );
    }

    @Override
    public V ceili()
    {
        return ceil( (V)this );
    }

    @Override
    public V inverti()
    {
        return invert( (V)this );
    }

    @Override
    public V invert()
    {
        return invert( create() );
    }

    @Override
    public V reflecti( V normal )
    {
        return reflect( normal, (V)this );
    }

    @Override
    public V reflect( V normal, V out )
    {
       final float scale = 2 * dot( normal );
       out.set( (V)this );
       out.addsi( normal, -scale );
       return (V)out;
    }

    @Override
    public V reflect( V normal )
    {
        return reflect( normal, create() );
    }

    @Override
    public V refracti( V normal )
    {
        return refract( normal, (V)this );
    }

    @Override
    public V refract( V normal, V out )
    {
       final float scale = 2 * dot( normal );
       out.set( (V)this );
       out.negi();
       out.addsi( normal, scale );
       return (V)out;
    }

    @Override
    public V refract( V normal )
    {
        return refract( normal, create() );
    }

    @Override
    public float normalize()
    {
        float m = lengthSq();

        if ( m != 0.0f )
        {
            divi( m = (float)Math.sqrt( m ) );
        }

        return m;
    }

    @Override
    public V normali()
    {
        return normal( (V)this );
    }

    @Override
    public V normal( V out )
    {
        float m = lengthSq();

        out.set( (V)this );

        if ( m != 0.0 )
        {
            out.muli( 1.0f / (float)Math.sqrt( m ) );
        }

        return out;
    }

    @Override
    public V normal()
    {
        return normal( create() );
    }

    @Override
    public boolean isParallel( V v )
    {
        return isParallel( v, 0 );
    }

    @Override
    public boolean isZero()
    {
        return isZero( 0 );
    }

    @Override
    public boolean isEqual( V v )
    {
        return isEqual( v, 0 );
    }
    
    @Override
    public V getTarget( SteerSubject<V> subject )
    {
        return (V)this;
    }

}
