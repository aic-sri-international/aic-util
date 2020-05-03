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
		Method enclosingMethod = getClass().getEnclosingMethod();
		checkProperUsage(enclosingMethod);
		return enclosingMethod.getName();
	}
	
	/**
	 * Returns the enclosing class.
	 * @return
	 */
	public Class clazz() {
		Class<?> enclosingClass = getClass().getEnclosingClass();
		checkProperUsage(enclosingClass);
		return enclosingClass;
	}

	/**
	 * Returns the enclosing method.
	 * @return
	 */
	public Method method() {
		Method enclosingMethod = getClass().getEnclosingMethod();
		checkProperUsage(enclosingMethod);
		return enclosingMethod;
	}
	
	/**
	 * Returns the simple name of the enclosing class.
	 * @return
	 */
	public String className() {
		Class<?> enclosingClass = getClass().getEnclosingClass();
		checkProperUsage(enclosingClass);
		return enclosingClass.getSimpleName();
	}

	private void checkProperUsage(Object enclosingElement) {
		if (enclosingElement == null) {
			throw new Error("Incorrect usage of " + getClass() + ". Correct usage is (new Enclosing(){}).<...> (that is, creating an anonymous extension of Encoding), but it seems like new Enclosing() has been used instead. Please check Encoding's Javadoc documentation.");
		}
	}
}