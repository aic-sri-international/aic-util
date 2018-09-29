package com.sri.ai.util.graph2d.core;

import com.sri.ai.util.graph2d.api.variables.Unit;

public class DefaultUnit implements Unit {
	
	private String name;
	private String symbol;

	public DefaultUnit(String name, String symbol) {
		this.name = name;
		this.symbol = symbol;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSymbol() {
		return symbol;
	}

}
