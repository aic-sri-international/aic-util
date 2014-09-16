/*
 * Copyright (c) 2013, SRI International
 * All rights reserved.
 * Licensed under the The BSD 3-Clause License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * 
 * http://opensource.org/licenses/BSD-3-Clause
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the aic-util nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.sri.ai.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.google.common.annotations.Beta;

/**
 * A basic utility for retrieving typed configuration information. Configuration
 * defaults are set programmatically, which can be overridden by setting system
 * properties corresponding to the property at runtime, e.g.:<br>
 * <br>
 * java &lt;main class&gt; -Dconfig.property.name=&lt;a value&gt; <br>
 * <br>
 * These can again be overridden with a configuration script expressed in
 * {@link Preferences#exportNode(java.io.OutputStream)} format. This utility
 * initializes its properties from configuration scripts in the following order:
 * <ol>
 * <li>The configuration utility tries to find a file called
 * aic-smf-config-local.xml in the classpath.</li>
 * <li>If no such file is found, it tries to find a file called
 * aic-smf-config-test.xml in the classpath.</li>
 * <li>If no such file is found, it tries to find a file called
 * aic-smf-config.xml in the classpath.</li>
 * <li>If a configuration script file is found, only the properties defined in
 * the script file will override the programmatically and system property
 * defined values.</li>
 * <li>If no configuration script file is found, only the programmatic defaults
 * and system property overrides are used to initialize properties.</li>
 * </ol>
 * 
 * <b>Configurations Per-Thread Support:</b><br>
 * To better enable concurrent execution, configuration information is
 * maintained on a Thread by Thread basis. On initialization a root set of
 * configuration information is set up as described previously and when calls
 * are made to the configuration API from a thread for the first time its
 * configuration settings are setup based on this root set. In addition, it is
 * possible to pass configuration information between threads in a parent child
 * relationship by calling
 * {@link Configuration#inheritConfiguration(Thread, Thread)}. This permits
 * configurations specific to a parent Thread to be inherited by a child Thread
 * and then adapted as needed in the child thread without changing the parent
 * threads configuration settings. <br>
 * <br>
 * <b>Note:</b> In the future we may switch to using: <a
 * href="http://commons.apache.org/configuration/">the Apache Commons
 * Configuration library</a> internally, in order to have more flexibility in
 * terms of where configuration information comes from.<br>
 * <br>
 * This class should be sub-classed in each project, where the extended class
 * lists the configuration properties specific to it, e.g.:
 * <pre>
 * public class MyProjectConfiguration extends Configuration {
 *     public static final String  KEY_PROPERTY_ONE      = "my.project.property.1";
 *     public static final Integer DEFAULT_PROPERTY_ONE  = new Integer(0);
 *     ...
 * 
 *     public static int getProperyOne() {
 *        int result = getInt(KEY_PROPERTY_ONE, DEFAULT_PROPERTY_ONE);
 *        return result;
 *     }
 *     
 *     ...
 * }
 * </pre>
 * @see AICUtilConfiguration
 * 
 * @author oreilly
 */
@Beta
public class Configuration {
	private static final ConcurrentHashMap<String, String>                            _userRootPreferences = initUserRootPreferences();
	private static final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> _threadPreferences   = new ConcurrentHashMap<String, ConcurrentHashMap<String, String>>();
	
	/**
	 * Use this to create an example of a configuration file.
	 * 
	 * @param args
	 *        not used.
	 */
	public static void main(String[] args) {
		Preferences preferences = Preferences.userRoot();
		try {
			preferences.put("key1", "value1");
			preferences.put("key2", "value2");
			preferences.put("key3", "value3");
			FileOutputStream fos = new FileOutputStream("aic-smf-config.xml");
			preferences.exportNode(fos);
			fos.flush();
			fos.close();
			
			FileInputStream fis = new FileInputStream("aic-smf-config.xml");
			Preferences.importPreferences(fis);
			fis.close();	
			System.out.println("Preferences loaded back in from file");
			for (String key : preferences.keys()) {
				System.out.println(key+" = "+preferences.get(key, ""));
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Create an instance of a named class using its default constructor. The
	 * intent is to use this after obtaining the class name from a configuration
	 * setting.
	 * 
	 * @param className
	 * 			the name of the Class to instantiate.
	 * @return a instance of the named class.
	 * @param <I> the type of the class to be instantiated.
	 */
	@SuppressWarnings("unchecked")
	public static <I> I newConfiguredInstance(String className) {
		I result = null;
		try {
			Class<?> clazz = Class.forName(className);
			result = (I) clazz.newInstance();
		} catch (Exception ex) {
			// Things are incorrectly configured if get an exception here,
			// best to exit.
			Util.fatalError(ex);
		}
		return result;
	}

	/**
	 * Get the String property value associated with the specified property key.
	 * 
	 * @param key
	 *            the key/name of the property.
	 * @param defaultValue
	 *            the default value to use if the property is not configured.
	 * @return the property value associated with the specified property key.
	 */
	public static String getString(String key, String defaultValue) {
		String value = getProperty(key, defaultValue);

		return value;
	}

	/**
	 * Get the Boolean property value associated with the specified property key.
	 * 
	 * @param key
	 *            the key/name of the property.
	 * @param defaultValue
	 *            the default value to use if the property is not configured.
	 * @return the property value associated with the specified property key.
	 */
	public static boolean getBoolean(String key, Boolean defaultValue) {
		String value = getProperty(key, defaultValue);
		boolean result = Boolean.parseBoolean(value);

		return result;
	}

	/**
	 * Get the Integer property value associated with the specified property key.
	 * 
	 * @param key
	 *            the key/name of the property.
	 * @param defaultValue
	 *            the default value to use if the property is not configured.
	 * @return the property value associated with the specified property key.
	 */
	public static int getInt(String key, Integer defaultValue) {
		String value = getProperty(key, defaultValue);
		int result = Integer.parseInt(value);

		return result;
	}

	/**
	 * Get the Long property value associated with the specified property key.
	 * 
	 * @param key
	 *            the key/name of the property.
	 * @param defaultValue
	 *            the default value to use if the property is not configured.
	 * @return the property value associated with the specified property key.
	 */
	public static long getLong(String key, Long defaultValue) {
		String value = getProperty(key, defaultValue);
		long result = Long.parseLong(value);

		return result;
	}

	/**
	 * Get the Float property value associated with the specified property key.
	 * 
	 * @param key
	 *            the key/name of the property.
	 * @param defaultValue
	 *            the default value to use if the property is not configured.
	 * @return the property value associated with the specified property key.
	 */
	public static float getFloat(String key, Float defaultValue) {
		String value = getProperty(key, defaultValue);
		float result = Float.parseFloat(value);

		return result;
	}

	/**
	 * Get the Double property value associated with the specified property key.
	 * 
	 * @param key
	 *            the key/name of the property.
	 * @param defaultValue
	 *            the default value to use if the property is not configured.
	 * @return the property value associated with the specified property key.
	 */
	public static double getDouble(String key, Double defaultValue) {
		String value = getProperty(key, defaultValue);
		double result = Double.parseDouble(value);

		return result;
	}
	
	/**
	 * Set the property value associated with a key/name (on the current
	 * thread).
	 * 
	 * @param key
	 *            the key/name of the property.
	 * @param value
	 *            a String representation of the value to be associated with the
	 *            key.
	 */
	public static void setProperty(String key, String value) {
		ConcurrentHashMap<String, String> preferences = getPreferences(Thread.currentThread());
		preferences.put(key, value);
	}
	
	/**
	 * Explicitly inherit the properties associated with a thread.
	 * 
	 * @param parentThread
	 *            the thread whose properties are to be inherited from.
	 * @param childThread
	 *            the thread inheriting the properties.
	 */
	public static void inheritConfiguration(Thread parentThread, Thread childThread) {
		// Ensure we actually need to inherit something.
		if (parentThread != childThread) {
			ConcurrentHashMap<String, String> parentPreferences = getPreferences(parentThread);
			ConcurrentHashMap<String, String> childPreferences  = getPreferences(childThread);

			copyPreferences(parentPreferences, childPreferences);
		}
	}
	
	/**
	 * Reset the configuration information associated with the current thread.
	 */
	public static void clear() {
		clear(Thread.currentThread());
	}

	//
	// PRIVATE
	//
	private static ConcurrentHashMap<String, String> initUserRootPreferences() {
		ConcurrentHashMap<String, String> result = new ConcurrentHashMap<String, String>();
		
		try {
			// Initially load the default and
			// ensure it is cleared of any prior
			// settings on startup.
			Preferences userRoot = Preferences.userRoot();
			// Ensure we start with clean preferences first.
			userRoot.clear();
			for (String cname : userRoot.childrenNames()) {
				userRoot.node(cname).removeNode();
			}
			
			// Now try to load configuration settings from
			// pre-specified configuration scripts
			String[] configuratScriptNames = new String[] {
					"aic-smf-config-local.xml",
					"aic-smf-config-test.xml",
					"aic-smf-config.xml"
			};
			for (String configScriptName : configuratScriptNames) {
				InputStream is = ClassLoader.getSystemResourceAsStream(configScriptName);
				if (is == null) {
					// Note: ClassLoader.getSystemResourceAsStream does not work in the context
					// of Java Web Start, see: 
					// http://docs.oracle.com/javase/1.5.0/docs/guide/javaws/developersguide/faq.html#211
					// This should work around the issue:
					is = Configuration.class.getClassLoader().getResourceAsStream(configScriptName);
				}
				
				if (is != null) {
					try {
						Preferences.importPreferences(is);
						is.close();
						// Assign over the values from the preferences object
						for (String key : userRoot.keys()) {
							result.put(key, userRoot.get(key, ""));
						}
						// Only load the first one found
						break;
					} catch (Exception ex) {
						// Bad configuration files, best to get out of here
						// and let the caller know the system not starting up
						// correctly.
						Util.fatalError(ex);
					} 
				}
			}
		} catch (BackingStoreException bse) {
			// Not good if this happens, lets get out of here.
			Util.fatalError(bse);
		}
		return result;
	}
	
	private static void clear(Thread currentThread) {
		_threadPreferences.remove(getPreferencesIdForThread(Thread.currentThread()));			
	}
	
	private static String getProperty(String key, Object defaultValue) {
		ConcurrentHashMap<String, String> preferences = getPreferences(Thread.currentThread());
		String value                                  = preferences.get(key);
		if (value == null) {
			value = System.getProperty(key, defaultValue.toString());
			preferences.put(key, value);
		}
		
		return value;
	}
	
	private static ConcurrentHashMap<String, String> getPreferences(Thread forThread) {
		ConcurrentHashMap<String, String> result = null;

		String preferencesForThreadId = getPreferencesIdForThread(forThread);
		result = _threadPreferences.get(preferencesForThreadId);
		if (result == null) {
			result = new ConcurrentHashMap<String, String>();
			result.putAll(_userRootPreferences);
			_threadPreferences.put(preferencesForThreadId, result);
		}
	
		return result;
	}
	
	private static String getPreferencesIdForThread(Thread thread) {
		return ""+thread.getId();
	}
	
	private static void copyPreferences(ConcurrentHashMap<String, String> fromPreferences, ConcurrentHashMap<String, String> toPreferences) { 
		toPreferences.clear();
		toPreferences.putAll(fromPreferences);
	}
}
