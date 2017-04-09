package org.magnos.steer.util;

import java.util.Iterator;

public class LinkedList<T> implements Iterable<T>
{
	public final LinkedNode<T> head;
	
	private LinkedListIterator iterator = new LinkedListIterator();

	public LinkedList() 
	{
		head = new LinkedNode<T>( null );
		head.next = head.prev = head;
	}
	
	// Adds the given node to this list. The node MUST not exist in a list already.
	// If it does (or you suspect it does), call node.Remove();
	public void add(LinkedNode<T> node) 
	{
		final LinkedNode<T> prev = head.prev;
		node.prev = prev;
		node.next = head;
		prev.next = node;
		head.prev = node;
	}

	// Counts the number of elements in this linked list
	public int size() 
	{
		int size = 0;
		LinkedNode<T> current = head.next;
		while (current != head) {
			size++;
			current = current.next;
		}
		return size;
	}
	
	public boolean isEmpty()
	{
	    return head.next == head;
	}
	
	@Override
	public Iterator<T> iterator()
	{
	    return iterator.hasNext() ? new LinkedListIterator() : iterator.reset();
	}
	
	private class LinkedListIterator implements Iterator<T>
	{
	    
	    private LinkedNode<T> current;
	    private boolean removed;
	    
	    public LinkedListIterator()
	    {
	        reset();
	    }

	    public LinkedListIterator reset()
	    {
	        current = head;
	        removed = false;
	        
	        return this;
	    }
	    
        @Override
        public boolean hasNext()
        {
            return current.next != head;
        }

        @Override
        public T next()
        {
            removed = false;
            
            return (current = current.next).value;
        }

        @Override
        public void remove()
        {
            if ( !removed )
            {
                current.remove();
                removed = true;
            }
        }
	    
	}
	
}