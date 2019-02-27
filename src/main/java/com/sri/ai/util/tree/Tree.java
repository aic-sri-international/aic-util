package com.sri.ai.util.tree;

import java.util.Iterator;

public interface Tree<T> {
	
	T getInformation();
	
	Iterator<? extends Tree<? extends T>> getChildren();

}
