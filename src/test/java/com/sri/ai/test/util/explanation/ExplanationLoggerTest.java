package com.sri.ai.test.util.explanation;

import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.println;
import static com.sri.ai.util.explanation.logging.api.ExplanationLogger.lazy;
import static com.sri.ai.util.explanation.logging.core.ExplanationBlock.CODE;
import static com.sri.ai.util.explanation.logging.core.ExplanationBlock.RESULT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.sri.ai.util.explanation.logging.core.DefaultExplanationLogger;
import com.sri.ai.util.explanation.logging.core.handler.StringExplanationHandler;

public class ExplanationLoggerTest {
	
	@Test
	public void basicTests() {
		
		DefaultExplanationLogger logger;
		StringExplanationHandler stringHandler;
		String expected;
		
		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		logger.addHandler(stringHandler);
		logger.explain("Hello, ", "World", "!!!");
		expected = "* Hello, World!!!\n";
		println("expected: " + expected);
		println("actual  : " + stringHandler);
		assertEquals(expected, stringHandler.toString());
		println();
		
		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		logger.addHandler(stringHandler);
		logger.explain("Hello, ", lazy(() -> "World"), lazy(() -> "!!!"));
		expected = "* Hello, World!!!\n";
		println("expected: " + expected);
		println("actual  : " + stringHandler);
		assertEquals(expected, stringHandler.toString());
		println();
		
		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		logger.addHandler(stringHandler);
		logger.explain();
		expected = "* \n";
		println("expected: " + expected);
		println("actual  : " + stringHandler);
		assertEquals(expected, stringHandler.toString());
		println();
		
		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		logger.addHandler(stringHandler);
		logger.explain("Can you do numbers? ", lazy(() -> "Sure! High "), lazy(() -> 5), lazy(() -> "!!!"));
		expected = "* Can you do numbers? Sure! High 5!!!\n";
		println("expected: " + expected);
		println("actual  : " + stringHandler);
		assertEquals(expected, stringHandler.toString());
		println();
		
		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		logger.addHandler(stringHandler);
		logger.explain("What about objects? ", lazy(() -> "You bet! "), lazy(() -> list(1, 2, 3, 4, 5)), lazy(() -> "!!!"));
		expected = "* What about objects? You bet! [1, 2, 3, 4, 5]!!!\n";
		println("expected: " + expected);
		println("actual  : " + stringHandler);
		assertEquals(expected, stringHandler.toString());
		println();
	}

	@Test
	public void blockTests() {
		
		DefaultExplanationLogger logger;
		StringExplanationHandler stringHandler;
		String expected;
		
		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		logger.addHandler(stringHandler);
		logger.start("Starting block ", 1);
		logger.explain("I'm explanation 1.1");
		logger.explain("I'm explanation 1.2");
		logger.end("End of block ", 1);
		expected =
				"* Starting block 1\n" + 
				"** I'm explanation 1.1\n" + 
				"** I'm explanation 1.2\n" + 
				"* End of block 1\n";
		println("expected:\n" + expected);
		println("actual  :\n" + stringHandler);
		assertEquals(expected, stringHandler.toString());
		println();
		
		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		logger.addHandler(stringHandler);
		logger.start("Starting block ", 1);
		logger.explain("I'm explanation 1.1");
		logger.start("Starting block 1.1");
		logger.explain("I'm explanation 1.1.1");
		logger.explain("I'm explanation 1.1.2");
		logger.explain("I'm explanation 1.1.3");
		logger.end("End of block 1.1");
		logger.explain("I'm explanation 1.2");
		logger.end("End of block ", 1);
		expected =
				"* Starting block 1\n" + 
				"** I'm explanation 1.1\n" + 
				"** Starting block 1.1\n" + 
				"*** I'm explanation 1.1.1\n" + 
				"*** I'm explanation 1.1.2\n" + 
				"*** I'm explanation 1.1.3\n" + 
				"** End of block 1.1\n" + 
				"** I'm explanation 1.2\n" + 
				"* End of block 1\n";
		println("expected:\n" + expected);
		println("actual  :\n" + stringHandler);
		assertEquals(expected, stringHandler.toString());
		println();
	}

	@Test
	public void blockErrorsTests() {
		
		DefaultExplanationLogger logger;
		StringExplanationHandler stringHandler;
		
		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		logger.addHandler(stringHandler);
		logger.start("Starting block ", 1);
		logger.explain("I'm explanation 1.1");
		logger.explain("I'm explanation 1.2");
		logger.end("End of block ", 1);
		try {
			logger.end("End of block ", 0);
			fail("Should have thrown an error by closing inexisting block.");
		}
		catch(Throwable t) {
			
		}

		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		logger.addHandler(stringHandler);
		logger.explain("I'm an explanation");
		try {
			logger.end("End of block ", 0);
			fail("Should have thrown an error by closing inexisting block.");
		}
		catch(Throwable t) {
			
		}

		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		try {
			logger.end("End of block ", 0);
			fail("Should have thrown an error by closing inexisting block.");
		}
		catch(Throwable t) {
			
		}
	}

	@Test
	public void blockImportanceTests() {
		
		DefaultExplanationLogger logger;
		StringExplanationHandler stringHandler;
		String expected;
		
		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		logger.addHandler(stringHandler);
		logger.setImportanceThreshold(1000);
		logger.start("Starting block ", 1);
		logger.explain("I'm explanation 1.1");
		logger.explain("I'm explanation 1.2");
		logger.end("End of block ", 1);
		expected = "";
		println("expected:\n" + expected);
		println("actual  :\n" + stringHandler);
		assertEquals(expected, stringHandler.toString());
		println();
		
		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		logger.addHandler(stringHandler);
		logger.setImportanceThreshold(0.25);
		logger.start(0.5, "Starting block ", 1);
		logger.explain(0.4, "I'm explanation 1.1");
		logger.start(0.1, "Starting block 1.1");
		logger.explain(1000, "I'm explanation 1.1.1");
		logger.start(50, "Starting block 1.1.1");
		logger.explain(1000, "I'm explanation 1.1.1.1");
		logger.explain(1000, "I'm explanation 1.1.1.2");
		logger.explain(1000, "I'm explanation 1.1.1.3");
		logger.end("End of block 1.1.1");
		logger.explain(1000, "I'm explanation 1.1.2");
		logger.explain(1000, "I'm explanation 1.1.3");
		logger.end("End of block 1.1");
		logger.start(0.9, "Starting block 1.2");
		logger.explain(1000, "I'm explanation 1.2.1");
		logger.explain(1000, "I'm explanation 1.2.2");
		logger.explain(1000, "I'm explanation 1.2.3");
		logger.end("End of block 1.2");
		logger.explain("I'm explanation 1.2");
		logger.end("End of block ", 1);
		expected =
				"* Starting block 1\n" + 
				"** Starting block 1.2\n" + 
				"*** I'm explanation 1.2.1\n" + 
				"*** I'm explanation 1.2.2\n" + 
				"*** I'm explanation 1.2.3\n" + 
				"** End of block 1.2\n" + 
				"** I'm explanation 1.2\n" + 
				"* End of block 1\n";
		println("expected:\n" + expected);
		println("actual  :\n" + stringHandler);
		assertEquals(expected, stringHandler.toString());
		println();
	}

	@Test
	public void filterTests() {
		
		DefaultExplanationLogger logger;
		StringExplanationHandler stringHandler;
		String expected;
		
		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		logger.addHandler(stringHandler);
		logger.setFilter(record -> record.getNestingDepth() != 2);
		// This should select nesting depths 0, 1 and 3.
		// Depth 3 passes the test but is inside the filtered out blocks, so they should not appear.
		logger.start("Starting block ", 1);
		logger.explain("I'm explanation 1.1");
		logger.start("Starting block 1.1");
		logger.explain("I'm explanation 1.1.1");
		logger.start("Starting block 1.1.1");
		logger.explain("I'm explanation 1.1.1.1");
		logger.explain("I'm explanation 1.1.1.2");
		logger.start("Starting block 1.1.1.1");
		logger.explain("I'm explanation 1.1.1.1.1");
		logger.explain("I'm explanation 1.1.1.1.2");
		logger.explain("I'm explanation 1.1.1.1.3");
		logger.end("End of block 1.1.1.1");
		logger.explain("I'm explanation 1.1.1.3");
		logger.end("End of block 1.1.1");
		logger.explain("I'm explanation 1.1.2");
		logger.explain("I'm explanation 1.1.3");
		logger.end("End of block 1.1");
		logger.start("Starting block 1.2");
		logger.explain("I'm explanation 1.2.1");
		logger.explain("I'm explanation 1.2.2");
		logger.explain("I'm explanation 1.2.3");
		logger.end("End of block 1.2");
		logger.explain("I'm explanation 1.2");
		logger.end("End of block ", 1);
		expected =
				"* Starting block 1\n" + 
				"** I'm explanation 1.1\n" + 
				"** Starting block 1.1\n" + 
				"** End of block 1.1\n" + 
				"** Starting block 1.2\n" + 
				"** End of block 1.2\n" + 
				"** I'm explanation 1.2\n" + 
				"* End of block 1\n";
		println("expected:\n" + expected);
		println("actual  :\n" + stringHandler);
		assertEquals(expected, stringHandler.toString());
		println();
	}

	
	@Test
	public void explanationBlockTest() {
		
		DefaultExplanationLogger logger;
		StringExplanationHandler stringHandler;
		String expected;
		int result;
		
		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		logger.addHandler(stringHandler);

		result = logger.explanationBlock("Going to solve the universe", CODE(() -> {
			logger.explain("Thanks for all the fish");
			return 42;
		}),	"The answer is ", RESULT);
		assertEquals(42, result);
		expected =
				"* Going to solve the universe\n" + 
				"** Thanks for all the fish\n" + 
				"* The answer is 42\n";
		println("expected:\n" + expected);
		println("actual  :\n" + stringHandler);
		assertEquals(expected, stringHandler.toString());
		println();
	}
	
	@Test
	public void explanationBlockWithExceptionTest() {
		
		DefaultExplanationLogger logger;
		StringExplanationHandler stringHandler;
		String expected;
		
		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		logger.addHandler(stringHandler);

		try {
			logger.explanationBlock("Going to solve the universe", CODE(() -> {
				logger.explain("Thanks for all the fish");
				throw new Error("Oops");
			}),	"The answer is ", RESULT);
		}
		catch (Error error) {
			assertEquals("Oops", error.getMessage());
			expected =
					"* Going to solve the universe\n" + 
					"** Thanks for all the fish\n" + 
					"* Throwable thrown: java.lang.Error: Oops\n";
			println("expected:\n" + expected);
			println("actual  :\n" + stringHandler);
			assertEquals(expected, stringHandler.toString());
			println();
		}
	}

	@Test
	public void explanationBlocksWithTwoNestedExceptionTest() {
		
		DefaultExplanationLogger logger;
		StringExplanationHandler stringHandler;
		String expected;
		
		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		logger.addHandler(stringHandler);

		try {
			logger.explanationBlock("Going to solve the universe", CODE(() -> {
				try {
					logger.explanationBlock("Going to solve the planet first", CODE(() -> {
						logger.explain("Thanks for all the fish");
						throw new Error("Oops");
					}),	"The answer is ", RESULT);
				}
				catch (Error error) {
					assertEquals("Oops", error.getMessage());
					throw new Error("Double oops!");
				}
				return 0;
			}),	"The answer is ", RESULT);
		}
		catch (Error error) {
			assertEquals("Double oops!", error.getMessage());
			expected =
					"* Going to solve the universe\n" +
					"** Going to solve the planet first\n" +
					"*** Thanks for all the fish\n" + 
					"** Throwable thrown: java.lang.Error: Oops\n" +
					"* Throwable thrown: java.lang.Error: Double oops!\n";
			println("expected:\n" + expected);
			println("actual  :\n" + stringHandler);
			assertEquals(expected, stringHandler.toString());
			println();
		}
	}
}
