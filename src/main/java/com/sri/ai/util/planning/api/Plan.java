package com.sri.ai.util.planning.api;

import com.google.common.base.Strings;

public interface Plan {

	double getEstimatedSuccessWeight();

	void execute(State state);
	
	void reward(double reward);

	default String nestedString() {
		return nestedString(0);
	}

	default String nestedString(int level) {
		return Strings.padStart("", level*4, ' ') + toString();
	}
}
