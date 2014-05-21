package org.magnos.steer.util;

import java.util.Arrays;

import org.magnos.steer.vec.Vec;


public class VectorDimensionalArray<V extends Vec<V>, T>
{

    public final V size;
    public T[] data;
    
    public VectorDimensionalArray(V size, T ... initial)   
    {
        int capacity = 0;
        
        for (int i = 0; i < size.size(); i++)
        {
            capacity += size.getComponent( i );
        }

        this.size = size;
        this.data = Arrays.copyOf( initial, capacity );
    }
    
    public V fromIndex(int index, V out)
    {
        
    }
    
    public T get(V index)
    {
        return null;
    }
    
}
