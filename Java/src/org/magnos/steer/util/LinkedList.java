package org.magnos.steer.util;

public class LinkedList<T> 
{
	public final LinkedNode<T> head;

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
	
}