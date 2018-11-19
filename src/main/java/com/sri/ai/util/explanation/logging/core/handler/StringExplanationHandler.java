package com.sri.ai.util.explanation.logging.core.handler;

import java.io.StringWriter;

public class StringExplanationHandler extends AbstractWriterExplanationHandler {

	public StringExplanationHandler() {
		super(new StringWriter());
	}

	@Override
	public String toString() {
		String result = getWriter().toString();
		return result;
	}
}
