package com.sri.ai.util.planning.api;

/**
 * A contingent goal is a goal which we may learn to be satisfiable from state
 * (as opposed to static goals which we always know to be satisfied or not based on applied rules).
 *  
 * @author braz
 *
 */
public interface ContingentGoal extends Goal {

	boolean isSatisfied(State state);
	
}
