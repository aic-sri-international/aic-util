package com.sri.ai.util.explanation.logging.core.handler;

import static com.sri.ai.util.Util.println;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class ConsoleExplanationHandler extends AbstractWriterExplanationHandler {

	public ConsoleExplanationHandler(boolean isActive) {
		super(new BufferedWriter(new OutputStreamWriter(System.out)));
		if (isActive) {
			println("Explanations to be sent to console.");
		}
	}

}
