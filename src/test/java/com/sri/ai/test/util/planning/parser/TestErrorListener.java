package com.sri.ai.test.util.planning.parser;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class TestErrorListener extends BaseErrorListener implements ANTLRErrorListener {

	@Override
	public void syntaxError(
			Recognizer<?, ?> recognizer, 
			Object offendingSymbol, 
			int line, 
			int charPositionInLine,
			String msg, 
			RecognitionException e) {
		
		throw new Error("line " + line + ":" + charPositionInLine + " " + msg);
		
	}

}
