package com.sri.ai.test.util.explanation;

import static com.sri.ai.util.Util.list;
import static com.sri.ai.util.Util.println;
import static com.sri.ai.util.Util.readContentsOfFile;
import static com.sri.ai.util.explanation.logging.api.ExplanationLogger.lazy;
import static com.sri.ai.util.explanation.logging.core.ExplanationBlock.RESULT;
import static com.sri.ai.util.explanation.logging.core.ExplanationBlock.code;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sri.ai.util.explanation.logging.api.ExplanationConfiguration;
import com.sri.ai.util.explanation.logging.api.ThreadExplanationLogger;
import com.sri.ai.util.explanation.logging.core.DefaultExplanationLogger;
import com.sri.ai.util.explanation.logging.core.handler.Nesting;
import com.sri.ai.util.explanation.logging.core.handler.StringExplanationHandler;

public class ExplanationLoggerTest {
	
	private boolean oldActive;
	
	@Before
	public void setUp() {
		oldActive = ExplanationConfiguration.setWhetherExplanationLoggersAreActiveByDefaultAndReturnOldValue(true);
	}
	
	@After
	public void shutDown() {
		ExplanationConfiguration.setWhetherExplanationLoggersAreActiveByDefaultAndReturnOldValue(oldActive);
	}
	
	@Test
	public void basicTests() {

		Nesting[] nestings = { new Nesting("*", " "), new Nesting("\t", "") };

		for (Nesting nesting: nestings) {
			DefaultExplanationLogger logger;
			StringExplanationHandler stringHandler;
			String expected;

			logger = new DefaultExplanationLogger();
			stringHandler = new StringExplanationHandler();
			stringHandler.setNesting(nesting);
			logger.addHandler(stringHandler);
			logger.explain("Hello, ", "World", "!!!");
			expected = nesting.getNestingString(0) + "Hello, World!!!\n";
			println("expected: " + expected);
			println("actual  : " + stringHandler);
			assertEquals(expected, stringHandler.toString());
			println();

			logger = new DefaultExplanationLogger();
			stringHandler = new StringExplanationHandler();
			stringHandler.setNesting(nesting);
			logger.addHandler(stringHandler);
			logger.explain("Hello, ", lazy(() -> "World"), lazy(() -> "!!!"));
			expected = nesting.getNestingString(0) + "Hello, World!!!\n";
			println("expected: " + expected);
			println("actual  : " + stringHandler);
			assertEquals(expected, stringHandler.toString());
			println();

			logger = new DefaultExplanationLogger();
			stringHandler = new StringExplanationHandler();
			stringHandler.setNesting(nesting);
			logger.addHandler(stringHandler);
			logger.explain();
			expected = nesting.getNestingString(0) + "\n";
			println("expected: " + expected);
			println("actual  : " + stringHandler);
			assertEquals(expected, stringHandler.toString());
			println();

			logger = new DefaultExplanationLogger();
			stringHandler = new StringExplanationHandler();
			stringHandler.setNesting(nesting);
			logger.addHandler(stringHandler);
			logger.explain("Can you do numbers? ", lazy(() -> "Sure! High "), lazy(() -> 5), lazy(() -> "!!!"));
			expected = nesting.getNestingString(0) + "Can you do numbers? Sure! High 5!!!\n";
			println("expected: " + expected);
			println("actual  : " + stringHandler);
			assertEquals(expected, stringHandler.toString());
			println();

			logger = new DefaultExplanationLogger();
			stringHandler = new StringExplanationHandler();
			stringHandler.setNesting(nesting);
			logger.addHandler(stringHandler);
			logger.explain("What about objects? ", lazy(() -> "You bet! "), lazy(() -> list(1, 2, 3, 4, 5)), lazy(() -> "!!!"));
			expected = nesting.getNestingString(0) + "What about objects? You bet! [1, 2, 3, 4, 5]!!!\n";
			println("expected: " + expected);
			println("actual  : " + stringHandler);
			assertEquals(expected, stringHandler.toString());
			println();
		}
	}

	@Test
	public void blockTests() {
		
		Nesting[] nestings = { new Nesting("*", " "), new Nesting("\t", "") };

		for (Nesting nesting: nestings) {
			DefaultExplanationLogger logger;
			StringExplanationHandler stringHandler;
			String expected;
			
			logger = new DefaultExplanationLogger();
			stringHandler = new StringExplanationHandler();
			stringHandler.setNesting(nesting);
			stringHandler.setIncludeBlockTime(false);
			logger.addHandler(stringHandler);
			logger.start("Starting block ", 1);
			logger.explain("I'm explanation 1.1");
			logger.explain("I'm explanation 1.2");
			logger.end("End of block ", 1);
			expected =
					nesting.getNestingString(0) + "Starting block 1\n" + 
							nesting.getNestingString(1) + "I'm explanation 1.1\n" + 
							nesting.getNestingString(1) + "I'm explanation 1.2\n" + 
							nesting.getNestingString(0) + "End of block 1\n";
			println("expected:\n" + expected);
			println("actual  :\n" + stringHandler);
			assertEquals(expected, stringHandler.toString());
			println();

			logger = new DefaultExplanationLogger();
			stringHandler = new StringExplanationHandler();
			stringHandler.setNesting(nesting);
			stringHandler.setIncludeBlockTime(false);
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
					nesting.getNestingString(0) + "Starting block 1\n" + 
							nesting.getNestingString(1) + "I'm explanation 1.1\n" + 
							nesting.getNestingString(1) + "Starting block 1.1\n" + 
							nesting.getNestingString(2) + "I'm explanation 1.1.1\n" + 
							nesting.getNestingString(2) + "I'm explanation 1.1.2\n" + 
							nesting.getNestingString(2) + "I'm explanation 1.1.3\n" + 
							nesting.getNestingString(1) + "End of block 1.1\n" + 
							nesting.getNestingString(1) + "I'm explanation 1.2\n" + 
							nesting.getNestingString(0) + "End of block 1\n";
			println("expected:\n" + expected);
			println("actual  :\n" + stringHandler);
			assertEquals(expected, stringHandler.toString());
			println();
		}
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
		
		Nesting[] nestings = { new Nesting("*", " "), new Nesting("\t", "") };

		for (Nesting nesting: nestings) {
			DefaultExplanationLogger logger;
			StringExplanationHandler stringHandler;
			String expected;

			logger = new DefaultExplanationLogger();
			stringHandler = new StringExplanationHandler();
			stringHandler.setNesting(nesting);
			stringHandler.setIncludeBlockTime(false);
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
			stringHandler.setNesting(nesting);
			stringHandler.setIncludeBlockTime(false);
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
					nesting.getNestingString(0) + "Starting block 1\n" + 
							nesting.getNestingString(1) + "Starting block 1.2\n" + 
							nesting.getNestingString(2) + "I'm explanation 1.2.1\n" + 
							nesting.getNestingString(2) + "I'm explanation 1.2.2\n" + 
							nesting.getNestingString(2) + "I'm explanation 1.2.3\n" + 
							nesting.getNestingString(1) + "End of block 1.2\n" + 
							nesting.getNestingString(1) + "I'm explanation 1.2\n" + 
							nesting.getNestingString(0) + "End of block 1\n";
			println("expected:\n" + expected);
			println("actual  :\n" + stringHandler);
			assertEquals(expected, stringHandler.toString());
			println();
		}
	}

	@Test
	public void filterTests() {
		
		Nesting[] nestings = { new Nesting("*", " "), new Nesting("\t", "") };

		for (Nesting nesting: nestings) {
			DefaultExplanationLogger logger;
			StringExplanationHandler stringHandler;
			String expected;

			logger = new DefaultExplanationLogger();
			stringHandler = new StringExplanationHandler();
			stringHandler.setNesting(nesting);
			stringHandler.setIncludeBlockTime(false);
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
					nesting.getNestingString(0) + "Starting block 1\n" + 
							nesting.getNestingString(1) + "I'm explanation 1.1\n" + 
							nesting.getNestingString(1) + "Starting block 1.1\n" + 
							nesting.getNestingString(1) + "End of block 1.1\n" + 
							nesting.getNestingString(1) + "Starting block 1.2\n" + 
							nesting.getNestingString(1) + "End of block 1.2\n" + 
							nesting.getNestingString(1) + "I'm explanation 1.2\n" + 
							nesting.getNestingString(0) + "End of block 1\n";
			println("expected:\n" + expected);
			println("actual  :\n" + stringHandler);
			assertEquals(expected, stringHandler.toString());
			println();

		}
	}

	
	@Test
	public void explanationBlockTest() {
		
		Nesting[] nestings = { new Nesting("*", " "), new Nesting("\t", "") };

		for (Nesting nesting: nestings) {
			DefaultExplanationLogger logger;
			StringExplanationHandler stringHandler;
			String expected;
			int result;
			
			logger = new DefaultExplanationLogger();
			stringHandler = new StringExplanationHandler();
			stringHandler.setNesting(nesting);
			stringHandler.setIncludeBlockTime(false);
			logger.addHandler(stringHandler);

			result = logger.explanationBlock("Going to solve the universe", code(() -> {
				logger.explain("Thanks for all the fish");
				return 42;
			}),	"The answer is ", RESULT);
			assertEquals(42, result);
			expected =
					nesting.getNestingString(0) + "Going to solve the universe\n" + 
							nesting.getNestingString(1) + "Thanks for all the fish\n" + 
							nesting.getNestingString(0) + "The answer is 42\n";
			println("expected:\n" + expected);
			println("actual  :\n" + stringHandler);
			assertEquals(expected, stringHandler.toString());
			println();
		}
	}
	
	@Test
	public void explanationBlockWithExceptionTest() {
		
		Nesting[] nestings = { new Nesting("*", " "), new Nesting("\t", "") };

		for (Nesting nesting: nestings) {
			DefaultExplanationLogger logger;
			StringExplanationHandler stringHandler;
			String expected;
			boolean topLevelExceptionWasCaught;
			
			logger = new DefaultExplanationLogger();
			stringHandler = new StringExplanationHandler();
			stringHandler.setNesting(nesting);
			stringHandler.setIncludeBlockTime(false);
			logger.addHandler(stringHandler);

			topLevelExceptionWasCaught = false;
			try {
				logger.explanationBlock("Going to solve the universe", code(() -> {
					logger.explain("Thanks for all the fish");
					throw new Error("Oops");
				}),	"The answer is ", RESULT);
			}
			catch (Error error) {
				topLevelExceptionWasCaught = true;
				assertEquals("Oops", error.getMessage());
				expected =
						nesting.getNestingString(0) + "Going to solve the universe\n" + 
								nesting.getNestingString(1) + "Thanks for all the fish\n" + 
								nesting.getNestingString(0) + "Throwable thrown: java.lang.Error: Oops\n";
				println("expected:\n" + expected);
				println("actual  :\n" + stringHandler);
				assertEquals(expected, stringHandler.toString());
				println();
			}

			assertTrue(topLevelExceptionWasCaught);
		}
	}

	@Test
	public void explanationBlocksWithTwoNestedExceptionsTest() {
		
		Nesting[] nestings = { new Nesting("*", " "), new Nesting("\t", "") };

		for (Nesting nesting: nestings) {
			DefaultExplanationLogger logger;
			StringExplanationHandler stringHandler;
			String expected;
			boolean topLevelExceptionWasCaught;
			
			logger = new DefaultExplanationLogger();
			stringHandler = new StringExplanationHandler();
			stringHandler.setNesting(nesting);
			stringHandler.setIncludeBlockTime(false);
			logger.addHandler(stringHandler);

			topLevelExceptionWasCaught = false;
			try {
				logger.explanationBlock("Going to solve the universe", code(() -> {
					try {
						logger.explanationBlock("Going to solve the planet first", code(() -> {
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
				topLevelExceptionWasCaught = true;
				assertEquals("Double oops!", error.getMessage());
				expected =
						nesting.getNestingString(0) + "Going to solve the universe\n" +
								nesting.getNestingString(1) + "Going to solve the planet first\n" +
								nesting.getNestingString(2) + "Thanks for all the fish\n" + 
								nesting.getNestingString(1) + "Throwable thrown: java.lang.Error: Oops\n" +
								nesting.getNestingString(0) + "Throwable thrown: java.lang.Error: Double oops!\n";
				println("expected:\n" + expected);
				println("actual  :\n" + stringHandler);
				assertEquals(expected, stringHandler.toString());
				println();
			}

			assertTrue(topLevelExceptionWasCaught);
		}
	}

	@Test
	public void threadExplanationLoggerTest() {
		
		Nesting[] nestings = { new Nesting("*", " "), new Nesting("\t", "") };

		for (Nesting nesting: nestings) {
			DefaultExplanationLogger logger;
			StringExplanationHandler stringHandler;
			String expected;
			boolean topLevelExceptionWasCaught;

			logger = new DefaultExplanationLogger();
			stringHandler = new StringExplanationHandler();
			stringHandler.setNesting(nesting);
			stringHandler.setIncludeBlockTime(false);
			logger.addHandler(stringHandler);

			ThreadExplanationLogger.setThreadExplanationLogger(logger);

			topLevelExceptionWasCaught = false;
			try {
				ThreadExplanationLogger.explanationBlock("Going to solve the universe", code(() -> {
					try {
						ThreadExplanationLogger.explanationBlock("Going to solve the planet first", code(() -> {
							ThreadExplanationLogger.explain("Thanks for all the fish");
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
				topLevelExceptionWasCaught = true;
				assertEquals("Double oops!", error.getMessage());
				expected =
						nesting.getNestingString(0) + "Going to solve the universe\n" +
								nesting.getNestingString(1) + "Going to solve the planet first\n" +
								nesting.getNestingString(2) + "Thanks for all the fish\n" + 
								nesting.getNestingString(1) + "Throwable thrown: java.lang.Error: Oops\n" +
								nesting.getNestingString(0) + "Throwable thrown: java.lang.Error: Double oops!\n";
				println("expected:\n" + expected);
				println("actual  :\n" + stringHandler);
				assertEquals(expected, stringHandler.toString());
				println();
			}

			assertTrue(topLevelExceptionWasCaught);
		}
	}


	//@Test
	public void threadExplanationLoggerToFileTest() {
		

		Nesting[] nestings = { new Nesting("*", " "), new Nesting("\t", "") };

		for (Nesting nesting: nestings) {
			String fileName = "explanation.txt";

			DefaultExplanationLogger logger;
			StringExplanationHandler stringHandler;
			String expected;
			boolean topLevelExceptionWasCaught;

			logger = new DefaultExplanationLogger();
			stringHandler = new StringExplanationHandler();
			stringHandler.setNesting(nesting);
			logger.addHandler(stringHandler);

			ThreadExplanationLogger.setThreadExplanationLogger(logger);

			topLevelExceptionWasCaught = false;
			try {

				ThreadExplanationLogger.explanationBlockToFile(fileName, "Going to solve the universe", code(() -> {
					try {
						ThreadExplanationLogger.explanationBlock("Going to solve the planet first", code(() -> {
							ThreadExplanationLogger.explain("Thanks for all the fish");
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
				topLevelExceptionWasCaught = true;
				assertEquals("Double oops!", error.getMessage());
				expected =
						nesting.getNestingString(0) + "Going to solve the universe\n" +
								nesting.getNestingString(1) + "Going to solve the planet first\n" +
								nesting.getNestingString(2) + "Thanks for all the fish\n" + 
								nesting.getNestingString(1) + "Throwable thrown: java.lang.Error: Oops\n" +
								nesting.getNestingString(0) + "Throwable thrown: java.lang.Error: Double oops!\n";
				println("expected:\n" + expected);
				String actual = readContentsOfFile(fileName);
				println("actual  :\n" + actual);
				assertEquals(expected, actual);
				println();
			}

			assertTrue(topLevelExceptionWasCaught);
		}
	}
	
	@Test
	public void blockTimePrintoutsTests() {
		
		DefaultExplanationLogger logger;
		StringExplanationHandler stringHandler;
		
		logger = new DefaultExplanationLogger();
		stringHandler = new StringExplanationHandler();
		stringHandler.setIncludeBlockTime(true);
		stringHandler.setIncludeTimestamp(true);
		logger.addHandler(stringHandler);
		
		logger.start("Starting block ", 1);
		wasteTime();
		logger.explain("I'm explanation 1.1");
		wasteTime();
		logger.start("Starting block 1.1");
		wasteTime();
		logger.explain("I'm explanation 1.1.1");
		wasteTime();
		logger.explain("I'm explanation 1.1.2");
		wasteTime();
		logger.explain("I'm explanation 1.1.3");
		wasteTime();
		logger.end("End of block 1.1");
		wasteTime();
		logger.explain("I'm explanation 1.2");
		wasteTime();
		logger.end("End of block ", 1);
		println("blocks and times  :\n" + stringHandler);
		println();
		
		
		int result;
		
		logger.removeHandler(stringHandler);
		stringHandler = new StringExplanationHandler();
		logger.addHandler(stringHandler);
		stringHandler.setIncludeBlockTime(true);
		stringHandler.setIncludeTimestamp(true);
		
		result = logger.explanationBlock("Going to solve the multiverse", code(() -> {
			logger.explain("Thanks for all the portals");
			wasteTime();
			return 57;
		}),	"The answer is ", RESULT);
		assertEquals(57, result);
		println("actual  :\n" + stringHandler);
		println();
	}
	
	
	private void wasteTime() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
