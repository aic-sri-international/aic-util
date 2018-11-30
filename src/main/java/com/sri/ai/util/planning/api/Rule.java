package com.sri.ai.util.planning.api;

import java.util.Collection;

public interface Rule<G extends Goal> extends Plan {
	
	Collection<? extends G> getAntecendents();

	Collection<? extends G> getConsequents();

}
