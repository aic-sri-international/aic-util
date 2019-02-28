package com.sri.ai.test.util.antlr;

import java.io.Reader;
import java.util.Collection;
import java.util.function.Function;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;

public class AntlrBundle<L extends Lexer, P extends Parser> {

	private L lexer;
	private P parser;
	
	public static <L1 extends Lexer, P1 extends Parser> 
	AntlrBundle<L1, P1> antlrBundle(
			Reader reader, 
			Class<L1> lexerClass, 
			Class<P1> parserClass) {
		
		return new AntlrBundle<L1, P1>(reader, lexerClass, parserClass);
	}
	
	public static <L1 extends Lexer, P1 extends Parser> 
	AntlrBundle<L1, P1> antlrBundle(
			Reader reader, 
			Class<L1> lexerClass, 
			Class<P1> parserClass, 
			Collection<? extends ANTLRErrorListener> errorListeners) {
		
		AntlrBundle<L1, P1> antlrBundle = new AntlrBundle<L1, P1>(reader, lexerClass, parserClass);
		antlrBundle.setErrorListeners(errorListeners);
		return antlrBundle;
	}
	
	@SuppressWarnings("unchecked")
	public AntlrBundle(Reader reader, Class<L> lexerClass, Class<P> parserClass) {
		try {
			
			CharStream input = CharStreams.fromReader(reader);

			// create a lexer that feeds off of input CharStream
			lexer = (L) lexerClass.getConstructors()[0].newInstance(input);

			// create a buffer of tokens pulled from the lexer
			CommonTokenStream tokens = new CommonTokenStream(lexer);

			// create a parser that feeds off the tokens buffer
			parser = (P) parserClass.getConstructors()[0].newInstance(tokens);
			
		} catch (Throwable e) {
			throw new Error(e);
		}
	}
	
	public void setErrorListeners(Collection<? extends ANTLRErrorListener> errorListeners) {
        if (errorListeners != null) {
        	lexer.removeErrorListeners();
        	errorListeners.forEach(lexer::addErrorListener);

        	parser.removeErrorListeners();
        	errorListeners.forEach(parser::addErrorListener);
        }
	}

	public ParseTree parse(Function<P, ParseTree> ruleInvocation) {
		return ruleInvocation.apply(parser);
	}
	
	public Lexer getLexer() {
		return lexer;
	}

	public Parser getParser() {
		return parser;
	}
}