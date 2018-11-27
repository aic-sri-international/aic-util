package com.sri.ai.util.planning.api;

import java.util.Collection;

public interface Rule extends Plan {
	
	Collection<? extends Goal> getAntecendents();

	Collection<? extends Goal> getConsequents();

}
