package com.sri.ai.util.explanation.logging.core.handler;

import java.io.IOException;

/**
 * A handler producing files in the format required by Squirrel (http://www.ai.sri.com/~braz/detaillogging.html)
 * @author braz
 *
 */
public class SquirrelFileExplanationHandler extends FileExplanationHandler {

	public SquirrelFileExplanationHandler(String filename) throws IOException {
		super(filename);
		setNestingStringBlock("*");
		setNestingPostfix(" ");
	}

	@Override
	public String toString() {
		return "Squirrel file explanation handler for " + getFilename();
	}
}
