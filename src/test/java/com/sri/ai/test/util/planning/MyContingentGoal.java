package com.sri.ai.test.util.planning;

import com.sri.ai.util.planning.api.ContingentGoal;
import com.sri.ai.util.planning.api.State;

public class MyContingentGoal extends MyGoal implements ContingentGoal {

	public MyContingentGoal(String name) {
		super(name);
	}

	@Override
	public boolean isSatisfied(State state) {
		return false;
	}
	
	@Override
	public String toString() {
		return "contingent " + name;
	}

}