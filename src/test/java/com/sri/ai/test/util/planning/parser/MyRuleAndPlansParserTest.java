package com.sri.ai.test.util.planning.parser;

import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.println;
import static com.sri.ai.util.antlr.AntlrBundle.antlrBundle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.function.Function;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import com.sri.ai.util.antlr.AntlrBundle;
import com.sri.ai.util.planning.test.MyRuleAndPlansLexer;
import com.sri.ai.util.planning.test.MyRuleAndPlansParser;

public class MyRuleAndPlansParserTest {

	@Test
	public void test() {
		Function<MyRuleAndPlansParser, ParseTree> ruleInvocation;
		String string;

		ruleInvocation = p -> p.goal();
		string = "contingent a";
        runTest(string, ruleInvocation);

		ruleInvocation = p -> p.goal();
		string = "b";
        runTest(string, ruleInvocation);

		ruleInvocation = p -> p.myRule();
		string = "b <= contingent a";
        runTest(string, ruleInvocation);

		ruleInvocation = p -> p.myRule();
		string = "b <= ";
        runTest(string, ruleInvocation);

		ruleInvocation = p -> p.myRule();
		string = "contingent a <= ";
        runTest(string, ruleInvocation);

		ruleInvocation = p -> p.plan();
		string = "if (contingent a) then b <= contingent a else b <= a";
        runTest(string, ruleInvocation);

		ruleInvocation = p -> p.plan();
		string = "if (contingent a) then b <= else b <=";
        runTest(string, ruleInvocation);

		ruleInvocation = p -> p.plan();
		string = "or(if (contingent a) then b <= else b <=, a <= b)";
        runTest(string, ruleInvocation);

		ruleInvocation = p -> p.plan();
		string = "or()";
        runTest(string, ruleInvocation);

		ruleInvocation = p -> p.plan();
		string = "and(if (contingent a) then b <= else b <=, a <= b)";
        runTest(string, ruleInvocation);

		ruleInvocation = p -> p.plan();
		string = "and()";
        runTest(string, ruleInvocation);

		ruleInvocation = p -> p.plan();
		string = "and(or(a <= contingent b, a <= a, b, c), if (contingent a) then b <= else b <=, a <= b)";
        runTest(string, ruleInvocation);

		ruleInvocation = p -> p.myRuleList();
		string = "a <= contingent b; a <= a";
        runTest(string, ruleInvocation);

		ruleInvocation = p -> p.goalList();
		string = "a, contingent b";
        runTest(string, ruleInvocation);
	}

	@Test
	public void errorsTest() {
		Function<MyRuleAndPlansParser, ParseTree> ruleInvocation;
		String string;
		String errorMessage;

		ruleInvocation = p -> p.goal();
		string = "contingent";
		errorMessage = "line 1:10 missing Identifier at '<EOF>'";
		runErrorTest(string, ruleInvocation, errorMessage);
		
		ruleInvocation = p -> p.goal();
		string = "b x";
		errorMessage = "Parser has not parsed the entire input";
		runErrorTest(string, ruleInvocation, errorMessage);

		ruleInvocation = p -> p.myRule();
		string = "<= contingent a";
		errorMessage = "line 1:0 extraneous input '<=' expecting {'contingent', Identifier}";
		runErrorTest(string, ruleInvocation, errorMessage);

		ruleInvocation = p -> p.myRule();
		string = "b <= 10";
		errorMessage = "line 1:5 token recognition error at: '1'";
		runErrorTest(string, ruleInvocation, errorMessage);

		ruleInvocation = p -> p.myRule();
		string = "contingent <= ";
		errorMessage = "line 1:11 missing Identifier at '<='";
		runErrorTest(string, ruleInvocation, errorMessage);

		ruleInvocation = p -> p.plan();
		string = "if (contingent a) then b <= contingent a /* else */ b <= a";
		errorMessage = "line 1:52 missing 'else' at 'b'";
		runErrorTest(string, ruleInvocation, errorMessage);

		ruleInvocation = p -> p.plan();
		string = "if contingent a) then b <= else b <=";
		errorMessage = "line 1:3 missing '(' at 'contingent'";
		runErrorTest(string, ruleInvocation, errorMessage);

		ruleInvocation = p -> p.plan();
		string = "or(if (contingent a) then b <= else b <=, a <= b, )";
		errorMessage = "line 1:50 mismatched input ')' expecting {'and', 'or', 'if', 'contingent', Identifier}";
		runErrorTest(string, ruleInvocation, errorMessage);

		ruleInvocation = p -> p.plan();
		string = "or(";
		errorMessage = "line 1:3 mismatched input '<EOF>' expecting {'and', ')', 'or', 'if', 'contingent', Identifier}";
		runErrorTest(string, ruleInvocation, errorMessage);

		ruleInvocation = p -> p.plan();
		string = "and(if (contingent a),  then b <= else b <=, a <= b)";
		errorMessage = "line 1:21 extraneous input ',' expecting 'then'";
		runErrorTest(string, ruleInvocation, errorMessage);

		ruleInvocation = p -> p.plan();
		string = "and)";
		errorMessage = "line 1:3 missing '(' at ')'";
		runErrorTest(string, ruleInvocation, errorMessage);

		ruleInvocation = p -> p.plan();
		string = "(and)";
		errorMessage = "line 1:0 extraneous input '(' expecting {'and', 'or', 'if', 'contingent', Identifier}";
		runErrorTest(string, ruleInvocation, errorMessage);

		ruleInvocation = p -> p.myRuleList();
		string = "(and)";
		errorMessage = "line 1:0 mismatched input '(' expecting {'contingent', Identifier}";
		runErrorTest(string, ruleInvocation, errorMessage);

		ruleInvocation = p -> p.myRuleList();
		string = "a <= contingent b, a <= a";
		errorMessage = "Parser has not parsed the entire input";
        runErrorTest(string, ruleInvocation, errorMessage);

		ruleInvocation = p -> p.goalList();
		string = "a; contingent b";
		errorMessage = "Parser has not parsed the entire input";
        runErrorTest(string, ruleInvocation, errorMessage);
	}

	private void runErrorTest(
			String string, 
			Function<MyRuleAndPlansParser, ParseTree> ruleInvocation,
			String errorMessage) {
		
		println("-----------");
		println(string);
		println("-----------");
		boolean error = false;
		try {
			runTest(string, ruleInvocation);
		}
        catch (Error e) {
        	println(e.getMessage());
        	assertEquals(errorMessage, e.getMessage());
        	error = true;
        }
		if (!error) {
			println("Expected error with message: " + errorMessage);
			fail("Expected error with message: " + errorMessage);
		}
	}

	private void runTest(String string, Function<MyRuleAndPlansParser, ParseTree> ruleInvocation) {
		Object object = parse(string, ruleInvocation);
        println("Goal: " + object);
        assertEquals(string.trim(), object.toString().trim());
	}

	private Object parse(
			String string, 
			Function<MyRuleAndPlansParser, ParseTree> ruleInvocation) {

		Collection<? extends ANTLRErrorListener> errorListeners = list(new TestErrorListener());
		boolean requireEntireStringToBeParsed = true;
		
		ParseTree tree = 
				parse(string, ruleInvocation, errorListeners, requireEntireStringToBeParsed);
        
        Object result = visit(tree);
        
		return result;
	}

	private ParseTree parse(String string, Function<MyRuleAndPlansParser, ParseTree> ruleInvocation,
			Collection<? extends ANTLRErrorListener> errorListeners, boolean requireEntireStringToBeParsed) {

		println("Parsing:");
		println(string);
		
		Reader reader = new StringReader(string);

		AntlrBundle<MyRuleAndPlansLexer, MyRuleAndPlansParser, MyRuleAndPlansVisitor> 
		bundle = 
		antlrBundle(reader, errorListeners, MyRuleAndPlansLexer.class, MyRuleAndPlansParser.class, MyRuleAndPlansVisitor.class);
		
		ParseTree tree = bundle.parse(ruleInvocation);
		
		if (requireEntireStringToBeParsed) {
			int position = bundle.getParser().getCurrentToken().getCharPositionInLine();
			if (position != string.length()) {
				throw new Error("Parser has not parsed the entire input");
			}
		}
		
		println(tree.toStringTree(bundle.getParser())); // print LISP-style tree	}
        return tree;
	}

	private Object visit(ParseTree tree) {
		MyRuleAndPlansVisitor visitor = new MyRuleAndPlansVisitor();
        Object value = visitor.visit(tree);
		return value;
	}
}
