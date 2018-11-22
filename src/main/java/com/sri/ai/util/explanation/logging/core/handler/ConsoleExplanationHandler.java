package com.sri.ai.util.explanation.logging.core.handler;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class ConsoleExplanationHandler extends AbstractWriterExplanationHandler {

	public ConsoleExplanationHandler() {
		super(new BufferedWriter(new OutputStreamWriter(System.out)));
	}

}
