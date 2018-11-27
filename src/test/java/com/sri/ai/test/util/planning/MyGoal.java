package com.sri.ai.test.util.planning;

import com.sri.ai.util.planning.api.Goal;

class MyGoal implements Goal {
	public String name;

	public MyGoal(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}