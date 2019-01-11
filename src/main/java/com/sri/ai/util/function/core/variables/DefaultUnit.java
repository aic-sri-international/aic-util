package com.sri.ai.util.function.core.variables;

import com.sri.ai.util.function.api.variables.Unit;

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

	@Override
	public String toString() {
		return "DefaultUnit{" +
				"name='" + name + '\'' +
				", symbol='" + symbol + '\'' +
				'}';
	}
}
