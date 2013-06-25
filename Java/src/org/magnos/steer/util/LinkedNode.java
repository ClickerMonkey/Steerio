package org.magnos.steer.util;
public class LinkedNode<T> 
{
	public LinkedNode<T> next;
	public LinkedNode<T> prev;
	public final T value;

	public LinkedNode(final T value) {
		this.value = value;
	}

	// Removes the node from its list.
	public void remove() {
		if (prev != null && next != null) {
			prev.next = next;
			next.prev = prev;
			next = prev = null;	
		}
	}
}