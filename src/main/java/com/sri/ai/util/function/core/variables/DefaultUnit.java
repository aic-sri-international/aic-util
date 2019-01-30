package com.sri.ai.util.function.core.variables;

import com.sri.ai.util.function.api.variables.Unit;

public class DefaultUnit implements Unit {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultUnit other = (DefaultUnit) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

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
