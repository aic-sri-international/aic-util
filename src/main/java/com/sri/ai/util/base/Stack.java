package com.sri.ai.util.base;

import java.util.ArrayList;

public class Stack <T> {
	
	ArrayList<T> stack;
	
	
	//TODO:  add exception throwing / handling
	//TODO:  implement shrinking if wanted
	
	
	// CONSTRUCTORS //////////////////////////////////////////////////
	
	public Stack() {
		stack = new ArrayList<T>();
	}
	
	public Stack(int initialCapacity) {
		stack = new ArrayList<T>(initialCapacity);
	}
	
	
	
	// WORKER METHODS ////////////////////////////////////////////////
	
	public void push(T element) {
		stack.add(element);
	}
	
	public T pop() {
		return isEmpty() ? stack.remove(stack.size()-1) : null;
	}
	
	public T top() {
		return isEmpty() ? stack.get(stack.size()-1) : null;
	}

	public boolean isEmpty() {
		return stack.size() == 0 ? true : false;
	}

}
