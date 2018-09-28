package com.sri.ai.util.graph2d.api.functions;

import java.util.List;

/**
 * An extension of {@link Functions} containing {@link SingleInputFunction}s only.
 *
 * @author braz
 *
 */
public interface SingleInputFunctions extends Functions {

	@Override
	List<? extends SingleInputFunction> getFunctions();

	public static SingleInputFunctions singleInputFunctions() {
		// TODO implement default implementation class and create instance here
		return null;
	}
	
	void add(SingleInputFunction singleInputFunction);

}
