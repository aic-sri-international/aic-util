package com.sri.ai.util.compilation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class Compiler {
	
	public static class Compilation<T> {
		public boolean succeeded;
		public Class<T> compiledClass;
		public DiagnosticCollector<JavaFileObject> diagnostics;
		public Compilation(boolean succeeded, Class<T> compiledClass, DiagnosticCollector<JavaFileObject> diagnostics) {
			this.succeeded = succeeded;
			this.compiledClass = compiledClass;
			this.diagnostics = diagnostics;
		}
	}

	public 
	static
	<T>
	Compilation<T>
	compile(String className, String source) throws URISyntaxException, IOException, ClassNotFoundException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

    	JavaByteObject byteObject = new JavaByteObject(className);

        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(diagnostics, null, null);

        JavaFileManager fileManager = createFileManager(standardFileManager, byteObject);
        
		JavaCompiler.CompilationTask task = 
        		compiler.getTask(null, fileManager, diagnostics, null, null, getCompilationUnits(className, source));

        if (!task.call()) {
            return new Compilation<>(false, null, diagnostics);
        }
        
        fileManager.close();

        ClassLoader inMemoryClassLoader = createClassLoader(byteObject);
        @SuppressWarnings("unchecked")
        Class<T> test = (Class<T>) inMemoryClassLoader.loadClass(className);
		return new Compilation<>(true, test, null);
	}

    private static JavaFileManager createFileManager(StandardJavaFileManager fileManager, JavaByteObject byteObject) {
    	return new ForwardingJavaFileManager<StandardJavaFileManager>(fileManager) {
    		@Override
    		public JavaFileObject getJavaFileForOutput(Location location,
    				String className, JavaFileObject.Kind kind,
    				FileObject sibling) throws IOException {
    			return byteObject;
    		}
    	};
    }

    private static ClassLoader createClassLoader(final JavaByteObject byteObject) {
    	return new ClassLoader() {
    		@Override
    		public Class<?> findClass(String name) throws ClassNotFoundException {
    			//no need to search class path, we already have byte code.
    			byte[] bytes = byteObject.getBytes();
    			return defineClass(name, bytes, 0, bytes.length);
    		}
    	};
    }

    public static Iterable<? extends JavaFileObject> getCompilationUnits(String className, String source) {
        JavaStringObject stringObject = new JavaStringObject(className, source);
        return Arrays.asList(stringObject);
    }
}