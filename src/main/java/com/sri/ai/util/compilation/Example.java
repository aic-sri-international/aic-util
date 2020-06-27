package com.sri.ai.util.compilation;

import static com.sri.ai.util.Util.println;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

public class Example {

	public interface ITest {
	    int answerToEverything();
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, URISyntaxException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    
    	String packageName = "com.sri.ai.util.compilation";
    	String className = "Test";
		String fullClassName = packageName + "." + className;
        String source = 
        		"package " + packageName + ";" +
        				"public class " + className + " implements com.sri.ai.util.compilation.Example.ITest{" +
        				"public int answerToEverything() { return 42; }}";

		Compiler.Compilation<ITest> compilation = Compiler.compile(fullClassName, source);

        if (!compilation.succeeded) {
            compilation.diagnostics.getDiagnostics().forEach(System.out::println);
        }
        else {
        	ITest iTest = compilation.compiledClass.getDeclaredConstructor().newInstance();
        	println(iTest.answerToEverything());
        }
    }
}