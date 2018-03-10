package com.sri.ai.util.computation.anytime.api;

import java.util.ArrayList;
import java.util.List;


 public class ConvexBoundApproximation<T> implements Approximation<T> {
	
	ArrayList<T> vertices;

	public ConvexBoundApproximation(ArrayList<T> vertices){
		this.vertices = vertices;
	}
	
	public List<T> getVertices(){
		return vertices;
	}
}
