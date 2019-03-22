package com.sri.ai.util.antlr;

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
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

public class AntlrBundle<L extends Lexer, P extends Parser, V extends ParseTreeVisitor> {

	private L lexer;
	private P parser;
	private V visitor;
	
	public static
	<L1 extends Lexer, P1 extends Parser, V1 extends ParseTreeVisitor> 
	AntlrBundle<L1, P1, V1> 
	antlrBundle(
			Reader reader, 
			Class<L1> lexerClass, 
			Class<P1> parserClass,
			Class<V1> visitorClass) {
		
		return new AntlrBundle<L1, P1, V1>(reader, lexerClass, parserClass, visitorClass);
	}
	
	public static 
	<L1 extends Lexer, P1 extends Parser, V1 extends ParseTreeVisitor> 
	AntlrBundle<L1, P1, V1> 
	antlrBundle(
			Reader reader, 
			Collection<? extends ANTLRErrorListener> errorListeners, 
			Class<L1> lexerClass,
			Class<P1> parserClass, 
			Class<V1> visitorClass) {
		
		AntlrBundle<L1, P1, V1> antlrBundle = new AntlrBundle<L1, P1, V1>(reader, lexerClass, parserClass, visitorClass);
		antlrBundle.setErrorListeners(errorListeners);
		return antlrBundle;
	}
	
	@SuppressWarnings("unchecked")
	public AntlrBundle(Reader reader, Class<L> lexerClass, Class<P> parserClass, Class<V> visitorClass) {
		try {
			
			CharStream input = CharStreams.fromReader(reader);

			// create a lexer that feeds off of input CharStream
			lexer = (L) lexerClass.getConstructors()[0].newInstance(input);

			// create a buffer of tokens pulled from the lexer
			CommonTokenStream tokens = new CommonTokenStream(lexer);

			// create a parser that feeds off the tokens buffer
			parser = (P) parserClass.getConstructors()[0].newInstance(tokens);
			
			// create a visitor
			visitor = (V) visitorClass.getConstructors()[0].newInstance();
			
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

	public Object visit(Function<P, ParseTree> ruleInvocation) {
		ParseTree tree = parse(ruleInvocation);
		Object result = visitor.visit(tree);
		return result;
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