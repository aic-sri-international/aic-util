package com.sri.ai.test.util.compilation;

import static com.sri.ai.util.Util.println;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.junit.Test;

import com.sri.ai.util.compilation.Compiler;

public class CompilationTest {

	public interface ITest {
	    int answerToEverything();
	}

	@Test
	public void test() throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, URISyntaxException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    
    	String packageName = "com.sri.ai.test.util.compilation";
    	String className = "Test";
		String fullClassName = packageName + "." + className;
        String source = 
        		"package " + packageName + ";" +
        				"public class " + className + " implements " + packageName + ".CompilationTest.ITest{" +
        				"public int answerToEverything() { return 42; }}";

		Compiler.Compilation<ITest> compilation = Compiler.compile(fullClassName, source);

        if (!compilation.succeeded) {
            compilation.diagnostics.getDiagnostics().forEach(System.out::println);
            fail("Dynamic compilation failed");
        }
        else {
        	ITest iTest = compilation.compiledClass.getDeclaredConstructor().newInstance();
        	var answerToEverything = iTest.answerToEverything();
			println(answerToEverything);
        	assertEquals(42, answerToEverything);
        }
    }
}
