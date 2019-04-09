package com.sri.ai.util.planning.api;

import java.util.Random;

/**
 * A class implementing a failed {@link State}.
 * 
 * @author braz
 *
 */
public class FailedState implements State {
	
	/**
	 * A global instance of {@link FailedState}, used by {@link State#FAILED}.
	 */
	public final static FailedState FAILED = new FailedState();
	
	@Override
	public Random getRandom() {
		throw new Error("Failed state cannot provide a Random object.");
	}
	
}
