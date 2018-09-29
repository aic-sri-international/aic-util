package com.sri.ai.util.graph2d.core;

import com.sri.ai.util.graph2d.api.functions.Functions;
import com.sri.ai.util.graph2d.api.graph.GraphSetMaker;

public class DefaultGraphSetMaker implements GraphSetMaker {

	private Functions functions;
	
	@Override
	public Functions getFunctions() {
		return functions;
	}

	@Override
	public void setFunctions(Functions functions) {
		this.functions = functions;
	}

}
