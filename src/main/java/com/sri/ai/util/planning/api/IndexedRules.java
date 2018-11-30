package com.sri.ai.util.planning.api;

import java.util.Collection;
import java.util.List;

public interface IndexedRules<R extends Rule, G extends Goal> {

	List<R> getRulesFor(G goal);

	Collection<? extends G> getGoals();
	
}
