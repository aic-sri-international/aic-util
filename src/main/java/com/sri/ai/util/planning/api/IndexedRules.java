package com.sri.ai.util.planning.api;

import java.util.Collection;
import java.util.List;

public interface IndexedRules {

	List<Rule> getRulesFor(Goal goal);

	Collection<? extends Goal> getGoals();
	
}
