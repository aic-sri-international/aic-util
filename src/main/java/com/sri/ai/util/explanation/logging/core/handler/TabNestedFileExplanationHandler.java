package com.sri.ai.util.explanation.logging.core.handler;

import java.io.IOException;

/**
 * A handler that outputs explanations nested by tabs, which can be visualized in many editors, including Notepad++ in Python or YAML mode.
 *
 */
public class TabNestedFileExplanationHandler extends FileExplanationHandler {

	public TabNestedFileExplanationHandler(String filename) throws IOException {
		super(filename);
		this.setNestingStringBlock("\t");
		this.setNestingStringPostfix("");
	}

	@Override
	public String toString() {
		return "Tab-nested file explanation handler for " + getFilename();
	}
}
