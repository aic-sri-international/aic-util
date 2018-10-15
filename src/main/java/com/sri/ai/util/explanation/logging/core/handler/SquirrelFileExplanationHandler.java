package com.sri.ai.util.explanation.logging.core.handler;

import java.io.IOException;

public class SquirrelFileExplanationHandler extends FileExplanationHandler {

	public SquirrelFileExplanationHandler(String filename) throws IOException {
		super(filename);
		setNestingStringBlock("*");
		setNestingStringPostfix(" ");
	}

	@Override
	public String toString() {
		return "Squirrel file explanation handler for " + getFilename();
	}
}
