package com.sri.ai.util;

import java.lang.reflect.Method;

/**
 * A class for helping to get enclosing method and class information.
 * To obtain the name of the enclosing method, use it in the following way: <code>(new Enclosing(){}).methodName()</code>
 * (and analogously for the other methods)>
 * 
 * @author braz
 *
 */
public class Enclosing {
	
	/**
	 * Returns the name of the enclosing method.
	 * @return
	 */
	public String methodName() {
		return getClass().getEnclosingMethod().getName();
	}
	
	/**
	 * Returns the enclosing class.
	 * @return
	 */
	public Class clazz() {
		return getClass().getEnclosingClass();
	}

	/**
	 * Returns the enclosing method.
	 * @return
	 */
	public Method method() {
		return getClass().getEnclosingMethod();
	}
	
	/**
	 * Returns the simple name of the enclosing class.
	 * @return
	 */
	public String className() {
		return getClass().getEnclosingClass().getSimpleName();
	}
}