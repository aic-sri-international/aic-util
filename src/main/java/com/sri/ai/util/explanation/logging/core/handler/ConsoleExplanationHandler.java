package com.sri.ai.util.explanation.logging.core.handler;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class ConsoleExplanationHandler extends AbstractWriterExplanationHandler {

	public ConsoleExplanationHandler(Writer explanationStream) {
		super(new BufferedWriter(new OutputStreamWriter(System.out)));
	}

}
