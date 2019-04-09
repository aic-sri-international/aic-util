package com.sri.ai.util.planning.api;

/**
 * A contingent goal is a goal which we may learn to be satisfiable from state
 * (as opposed to static goals which we always know to be satisfied or not based on applied rules).
 *  
 * @author braz
 *
 */
public interface ContingentGoal extends Goal {

	/**
	 * Indicates whether this contingent goal is satisfied by the current state.
	 * Note that this method is not at the {@link Goal} level, even though it is typical to talk about goals being satisfied,
	 * because {@link Goal} is meant as static goals, for which there should not be a need to
	 * decide satisfiability given a state (if there were, they would be <i>contingent</i> goals).
	 * @param state
	 * @return
	 */
	boolean isSatisfied(State state);
	
}
