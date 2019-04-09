package com.sri.ai.util.planning.api;

import java.util.Random;

public interface State {
	
	public final static State FAILED = FailedState.FAILED;
	
	Random getRandom();
	
}
