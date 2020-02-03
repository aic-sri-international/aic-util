package com.sri.ai.util.explanation.logging.core.handler;

/**
 * A class producing nesting strings given a nesting level;
 * nesting strings are formed by concatenating a nesting string block as many times as the nesting level, followed by a postfix.
 * @author braz
 *
 */
public class Nesting {
	
	private String nestingStringBlock;
	private String nestingStringPostfix;
	
	public Nesting(String nestingStringBlock, String nestingStringPostfix) {
		this.nestingStringBlock = nestingStringBlock;
		this.nestingStringPostfix = nestingStringPostfix;
	}

	public String getNestingString(int nestingLevel) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i != nestingLevel; i++) {
			builder.append(nestingStringBlock);
		}
		builder.append(nestingStringPostfix);
		return builder.toString();
	}
	
	public String getNestingBlock() {
		return nestingStringBlock;
	}

	public String getNestingPostfix() {
		return nestingStringPostfix;
	}

	public Nesting setNestingBlock(String newNestingString) {
		return new Nesting(newNestingString, nestingStringPostfix);
	}
	
	public Nesting setNestingPostfix(String newNestingStringPostfix) {
		return new Nesting(nestingStringBlock, newNestingStringPostfix);
	}
}