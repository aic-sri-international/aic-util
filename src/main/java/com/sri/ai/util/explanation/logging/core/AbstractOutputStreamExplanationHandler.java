package com.sri.ai.util.explanation.logging.core;

import com.sri.ai.util.explanation.logging.api.ExplanationHandler;

import java.io.OutputStream;

public abstract class AbstractOutputStreamExplanationHandler implements ExplanationHandler {
	
	OutputStream explanationStream;

}
