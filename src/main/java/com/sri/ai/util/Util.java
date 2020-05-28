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

import static com.sri.ai.util.base.PairOf.makePairOf;
import static com.sri.ai.util.collect.FunctionIterator.functionIterator;
import static com.sri.ai.util.collect.PredicateIterator.predicateIterator;
import static java.lang.Math.pow;
import static java.lang.reflect.Proxy.newProxyInstance;
import static org.apache.commons.lang3.ArrayUtils.toPrimitive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.sri.ai.util.base.BinaryFunction;
import com.sri.ai.util.base.BinaryPredicate;
import com.sri.ai.util.base.BinaryProcedure;
import com.sri.ai.util.base.Equals;
import com.sri.ai.util.base.IndexingFunction;
import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.base.NullaryPredicate;
import com.sri.ai.util.base.NullaryProcedure;
import com.sri.ai.util.base.Pair;
import com.sri.ai.util.base.PairOf;
import com.sri.ai.util.base.Procedure;
import com.sri.ai.util.base.TernaryFunction;
import com.sri.ai.util.collect.EZIterator;
import com.sri.ai.util.collect.IntegerIterator;
import com.sri.ai.util.collect.NestedIterator;
import com.sri.ai.util.collect.ReverseIterator;
import com.sri.ai.util.collect.ReverseListIterator;
import com.sri.ai.util.math.Rational;

/**
 * A suite of general purpose utility routines related to handling errors,
 * string manipulation, I/O, collections, and math that have proven useful over
 * time.
 * 
 * @author braz
 * 
 */
@Beta
public class Util {

	/**
	 * Logs the error message and stack trace for the given exception, and exits
	 * the program, returning code 1.
	 * 
	 * @param e
	 *            the throwable causing the fatal error.
	 */
	public static void fatalError(Throwable e) {
		fatalError(e, true);
	}

	/**
	 * Logs a top level message, the error message and stack trace for the given
	 * exception, and exits the program, returning code 1.
	 * 
	 * @param topLevelMessage
	 *            the top level message describing the fatal error.
	 * @param e
	 *            the throwable causing the fatal error.
	 */
	public static void fatalError(String topLevelMessage, Throwable e) {
		fatalError(topLevelMessage, e, true);
	}

	/**
	 * Logs the error message for the given exception, and optionally logs a
	 * stack trace. Then exits the program with return code 1.
	 * 
	 * @param e
	 *            the throwable causing the fatal error.
	 * @param trace
	 *            indicates whether or not to log the stack trace.
	 */
	public static void fatalError(Throwable e, boolean trace) {
		fatalError("Fatal error: ", e, trace);
	}

	/**
	 * Logs error message and exits.
	 * 
	 * @param msg
	 *            the error message
	 * 
	 */
	public static void fatalError(String msg) {
		fatalError(msg, true);
	}

	/**
	 * Logs error message without loging stack trace, and exits.
	 * 
	 * @param msg
	 *            the error message
	 */
	public static void fatalErrorWithoutStack(String msg) {
		fatalError(msg, false);
	}

	/**
	 * Logs a top level message, the error message for the given exception, and
	 * optionally logs a stack trace. Then exits the program with return code 1.
	 * 
	 * @param topLevelMessage
	 *            the top level message describing the fatal error.
	 * @param e
	 *            the throwable causing the fatal error.
	 * @param trace
	 *            indicates whether or not to log the stack trace.
	 */
	public static void fatalError(String topLevelMessage, Throwable e,
			boolean trace) {
		if (trace) {
			if (e.getCause() != null) {
				System.err.println(topLevelMessage + "\n" + e.getMessage()
						+ "\n" + join("\n", e.getStackTrace()) + "\n"
						+ e.getCause().getMessage() + "\n"
						+ join("\n", e.getCause().getStackTrace()));
			} else {
				System.err.println(topLevelMessage + "\n" + e.getMessage());
			}
		} else {
			System.err.println(topLevelMessage + "\n" + e.getMessage());
		}
		if (e != null) {
			e.printStackTrace();
		}
		System.exit(1);
	}

	/**
	 * Logs error message, optionally logs stack trace, and exits.
	 * 
	 * @param msg
	 *            the error message
	 * 
	 * @param trace
	 *            if true, log a stack trace
	 */
	public static void fatalError(String msg, boolean trace) {
		if (trace) {
			System.err.println(msg + "\n"
					+ join("\n", Thread.currentThread().getStackTrace()));
		} else {
			System.err.println(msg);
		}
		System.exit(1);
	}

	/**
	 * Returns a string with the entire context of an input stream.
	 * 
	 * @param inputStream
	 *            the input stream from which to read all from.
	 * @return a String representation of the entire contents of the given input
	 *         stream.
	 */
	public static String readAll(InputStream inputStream) {
		StringBuilder result = new StringBuilder();
		String line;
		boolean first = true;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			while ((line = reader.readLine()) != null) {
				if (!first) {
					result.append("\n");
				}
				result.append(line);
				first = false;
			}
			return result.toString();
		} catch (Exception e) {
			Util.fatalError("Could not read " + inputStream, e);
		}
		return null;
	}
	
	/**
	 * Returns a string with the entire contents of an input reader.
	 * 
	 * @param inputReader
	 *        the input reader to be read from.
	 * @return all of the contents read from the input reader.
	 * @throws IOException if an error occurs.
	 */
	public static String readAll(Reader inputReader) throws IOException {
		StringWriter result = new StringWriter();
		char[] cbuf = new char[1024];
		int read = 0;
		while ((read = inputReader.read(cbuf)) != -1) {
			result.write(cbuf, 0, read);
		}
		
		return result.toString();
	}

	/**
	 * Returns the string formed by concatenating the two given strings, with a
	 * space in between if both strings are non-empty.
	 * 
	 * @param str1
	 *            the first string to join.
	 * @param str2
	 *            the second string to join.
	 * @return a concatenated version of str1 and str2 with a space in between.
	 */
	public static String join(String str1, String str2) {
		if (str1.length() == 0) {
			return str2;
		}
		if (str2.length() == 0) {
			return str1;
		}

		StringJoiner sj = new StringJoiner(" ");
		sj.add(str1).add(str2);

		return sj.toString();
	}

	/**
	 * Returns a string formed by the concatenation of string versions of the
	 * elements in a collection, separated by a given separator.
	 * 
	 * @param separator
	 *            the separator to use between elements when creating the joined
	 *            string.
	 * @param c
	 *            the collection whose elements toString() values are to be
	 *            joined together.
	 * @return a String constructed from the toString of each element of the
	 *         given collection with the given separator between each argument.
	 */
	public static String join(String separator, Collection c) {
		Iterator it = c.iterator();
		return join(separator, it);
	}

	/**
	 * Returns a string formed by the concatenation of string versions of the
	 * elements in an iterator's range, separated by a given separator.
	 * 
	 * @param separator
	 *            the separator to use between elements when creating the joined
	 *            string.
	 * @param it
	 *            the iterator whose elements toString() values are to be joined
	 *            together.
	 * @return a String constructed from the toString of each element of the
	 *         given iterator with the given separator between each argument.
	 */
	@SuppressWarnings("unchecked")
	public static String join(String separator, Iterator it) {
		StringJoiner sj = new StringJoiner(separator);
		it.forEachRemaining(e -> sj.add(e == null? "null" : e.toString()));
		return sj.toString();
	}

	/**
	 * Same as {@link #join(String, Iterator)}, with <code>", "</code> for a
	 * separator.
	 * 
	 * @param it
	 *            the iterator whose elements toString() values are to be joined
	 *            together.
	 * @return a String constructed from the toString of each element of the
	 *         given iterator with a comma (<code>", "</code>) separator between
	 *         each argument.
	 */
	public static String join(Iterator it) {
		return join(", ", it);
	}

	/**
	 * Same as {@link #join(String, Collection)}.
	 *
	 * @param c
	 *            the collection whose elements toString() values are to be
	 *            joined together.
	 * @param separator
	 *            the separator to use between elements when creating the joined
	 *            string.
	 * @return a String constructed from the toString of each element of the
	 *         given collection with the given separator between each argument.
	 */
	public static String join(Collection c, String separator) {
		return join(separator, c);
	}

	/**
	 * Calls {@link #join(String, Collection)} with ", " as separator.
	 * 
	 * @param c
	 *            the collection whose elements toString() values are to be
	 *            joined together.
	 * @return a String constructed from the toString of each element of the
	 *         given collection with a comma (<code>", "</code>) separator
	 *         between each argument.
	 */
	public static String join(Collection c) {
		return join(", ", c);
	}

	/**
	 * Calls {@link #join(Collection)} on the given array as a collection.
	 * 
	 * @param a
	 *            the array whose elements toString() values are to be joined
	 *            together.
	 * @return a String constructed from the toString of each element of the
	 *         given array with a comma (<code>", "</code>) separator between
	 *         each argument.
	 */
	public static String join(Object[] a) {
		return join(Arrays.asList(a));
	}

	/**
	 * Calls {@link #join(String, Collection)} on the given array as a
	 * collection.
	 * 
	 * @param separator
	 *            the separator to use between elements when creating the joined
	 *            string.
	 * @param a
	 *            the array whose elements toString() values are to be joined
	 *            together.
	 * @return a String constructed from the toString of each element of the
	 *         given array with the given separator between each argument.
	 */
	public static String join(String separator, Object[] a) {
		return join(separator, Arrays.asList(a));
	}

	/**
	 * Produces a string with map entry representations separated by a given
	 * entry separator, where entry representations are the key and value
	 * representations separated by a key-value separator.
	 * 
	 * @param entrySeparator
	 *            the separator to use between each map entry in the join
	 *            output.
	 * @param keyValueSeparator
	 *            the separator to use between each entry's key value pair in
	 *            the join output.
	 * @param map
	 *            the map whose key value pairs are to be joined into a String.
	 * @return a joined string with an entrySeparator between each entry in the
	 *         given map, each of which has a keyValueSeparator between the
	 *         entry's key and value.
	 */
	public static String join(String entrySeparator, String keyValueSeparator,
			Map<? extends Object, ? extends Object> map) {
		List<Object> c = new LinkedList<Object>();
		for (Map.Entry<? extends Object, ? extends Object> entry : map
				.entrySet()) {
			c.add(entry.getKey() + keyValueSeparator + entry.getValue());
		}
		return join(entrySeparator, c);
	}

	/**
	 * Same as {@link #join(String, String, Map)} with key-value separator equal
	 * to -&gt; .
	 * 
	 * @param entrySeparator
	 *            the separator to use between each map entry in the join
	 *            output.
	 * @param map
	 *            the map whose key value pairs are to be joined into a String.
	 * @return a joined string with an entrySeparator between each entry in the
	 *         given map, each of which has an arrow -&gt; separator between the
	 *         entry's key and value.
	 */
	public static String join(String entrySeparator,
			Map<? extends Object, ? extends Object> map) {
		return join(entrySeparator, " -> ", map);
	}

	/**
	 * Same as {@link #join(String, String, Map)} with entry separator equal to
	 * <code>", "</code> and key-value separator equal to -&gt;.
	 * 
	 * @param map
	 *            the map whose key value pairs are to be joined into a String.
	 * @return a joined string with a <code>", "</code> comma separator between
	 *         each entry in the given map, each of which has an arrow -&gt;
	 *         separator between the entry's key and value.
	 */
	public static String join(Map<? extends Object, ? extends Object> map) {
		return join(", ", " -> ", map);
	}

	public static String join(int[] array) {
		return join(", ", ArrayUtils.toObject(array));
	}

	public static String join(String separator, int[] array) {
		return join(separator, ArrayUtils.toObject(array));
	}

	public static String join(double[] array) {
		return join(", ", ArrayUtils.toObject(array));
	}

	public static String join(String separator, double[] array) {
		return join(separator, ArrayUtils.toObject(array));
	}

	public static List<String> split(String separator, String string) {
		if (separator.length() == 0) {
			throw new Error("Util.split cannot run on empty separator.");
		}

		List<String> result = new LinkedList<String>();
		int begin = 0;
		int end;

		while (begin <= string.length()) {
			end = string.indexOf(separator, begin);
			if (end == -1) {
				end = string.length();
			}
			result.add(string.substring(begin, end));
			begin = end + separator.length();
		}

		return result;
	}

	/**
	 * Construct an Object array of the given elements.
	 * 
	 * @param elements
	 *            the elements to construct an array from.
	 * @return the received arguments in an array.
	 * 
	 */
	public static Object[] array(Object... elements) {
		return elements;
	}

	/**
	 * Construct a list of the given type populated with the provided elements.
	 * 
	 * @param elements
	 *            the elements to construct the List from.
	 * @return the received arguments in a linked list.
	 * @param <T>
	 *            the type of the elements in the list.
	 */
	@SafeVarargs
	public static <T> LinkedList<T> list(T... elements) {
		return new LinkedList<T>(Arrays.asList(elements));
	}

	/**
	 * Construct an iterator of the given type ranging over provided elements.
	 * 
	 * @param elements
	 *            the elements to construct the List from.
	 * @return an iterator over the received arguments.
	 * @param <T>
	 *            the type of elements the iterator will range over.
	 */
	@SafeVarargs
	public static <T> Iterator<T> iterator(T... elements) {
		return Arrays.asList(elements).iterator();
	}

	@SafeVarargs
	public static <T> Iterator<T> unmodifiableIterator(T... elements) {
		return Collections.unmodifiableList(Arrays.asList(elements)).iterator();
	}

	/**
	 * Construct an {@link ArrayList} of the given type populated with the provided elements.
	 * 
	 * @param elements
	 *            the elements to construct the List from.
	 * @return the received arguments in an array list.
	 * @param <T>
	 *            the type of the List to be constructed and the elements it is
	 *            to contain.
	 */
	@SafeVarargs
	public static <T> ArrayList<T> arrayList(T... elements) {
		return new ArrayList<T>(Arrays.asList(elements));
	}
	
	/**
	 * @return an empty stack of the given type.
	 * @param <T>
	 *            the type of the Stack to instantiate.
	 */
	public static <T> Stack<T> stack() {
		Stack<T> result = new Stack<T>();
		return result;
	}

	@SafeVarargs
	public static <T> LinkedHashSet<T> set(T... elements) {
		return new LinkedHashSet<T>(Arrays.asList(elements));
	}

	public static <T> List<T> singletonListIfNotNullOrEmptyListIfNull(T element) {
		if (element == null) {
			return new LinkedList<T>();
		}
		else {
			return list(element);
		}
	}
	
	public static boolean isEven(int number) {
		return number % 2 == 0;
	}

	/**
	 * 
	 * @param keysAndValues
	 *            a sequence of key and value pairs to be placed into a new Map.
	 * 
	 * @return the received arguments (interpreted as a sequence of key and
	 *         value pairs) in a hash map.
	 * @param <K>
	 *            the type of the Map's key.
	 * @param <V>
	 *            the type of the Map's value.
	 */
	public static <K, V> LinkedHashMap<K, V> map(Object... keysAndValues) {
		if (!isEven(keysAndValues.length)) {
			fatalError("Util.map(Object ...) must receive an even number of arguments but received "
					+ keysAndValues.length
					+ ": "
					+ join(";", keysAndValues)
					+ ".");
		}
		LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();
		putAll(result, keysAndValues);
		return result;
	}

	/**
	 * @param map
	 *        a map.
	 * @param keysAndValues
	 *            a sequence of key and value pairs to be placed into a given Map.
	 * 
	 * @return the received arguments (interpreted as a sequence of key and
	 *         value pairs) in a given map.
	 * @param <K>
	 *            the type of the Map's key.
	 * @param <V>
	 *            the type of the Map's value.
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> putAll(Map<K, V> map, Object... keysAndValues) {
		int i = 0;
		while (i != keysAndValues.length) {
			map.put((K) keysAndValues[i], (V) keysAndValues[i + 1]);
			i += 2;
		}
		return map;
	}

	/**
	 * Add given element of a given collection to a collection value in a map,
	 * creating it in advance (as an instance of given class) if needed.
	 * 
	 * @param mapToCollections
	 *            the map containing the collections to be added to.
	 * @param key
	 *            the key identifying the collection in the given map to be
	 *            added to.
	 * @param element
	 *            the element to be added to the identified collection.
	 * @param newCollectionClass
	 *            the class of the collection to create if it is not currently
	 *            mapped to in the given map.
	 * @param <K>
	 *            the type of the Map's key.
	 * @param <V>
	 *            the type of the Map's value.
	 * @param <C>
	 *            the type of the Collection.
	 */
	@SuppressWarnings("unchecked")
	public static <K, V, C extends Collection<V>> void addToCollectionValuePossiblyCreatingIt(
			Map<K, C> mapToCollections, K key, V element,
			Class newCollectionClass) {
		C c = mapToCollections.get(key);
		if (c == null) {
			try {
				c = (C) newCollectionClass.getDeclaredConstructor().newInstance();
				mapToCollections.put(key, c);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		c.add(element);
	}

	/**
	 * Add all elements of a given collection to a collection value in a map,
	 * creating it in advance (as an instance of given class) if needed.
	 * 
	 * @param mapToCollections
	 *            the map containing the collections to be added to.
	 * @param key
	 *            the key identifying the collection in the given map to be
	 *            added to.
	 * @param elements
	 *            the elements to be added to the identified collection.
	 * @param newCollectionClass
	 *            the class of the collection to create if it is not currently
	 *            mapped to in the given map.
	 */
	@SuppressWarnings("unchecked")
	public static void addAllToCollectionValuePossiblyCreatingIt(
			Map mapToCollections, Object key, Collection elements,
			Class newCollectionClass) {
		Collection c = (Collection) mapToCollections.get(key);
		if (c == null) {
			try {
				c = (Collection) newCollectionClass.getDeclaredConstructor().newInstance();
				mapToCollections.put(key, c);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		c.addAll(elements);
	}

	/**
	 * Given two maps of collections, add all elements in each collection2 of second map
	 * to the corresponding (that is, associated with the same key) collection1 in first map,
	 * guaranteeing to keep collection2's instance if collection1 is absent or empty,
	 * and returns the first map, unless it is null, in which case the second map is returned.
	 *
	 * @param map1
	 *        map 1
	 * @param map2
	 *        map 2
	 * @return the first map, unless it is null, in which case the second map is returned
	 * @param <M> a map type
	 * @param <V> a value type
	 * @param <K> a key type
	 */
	public static <M extends Map<K, Collection<V>>, K, V> M addAllForEachEntry(M map1, M map2) {
		M result;
		if (map1 == null) {
			result = map2;
		}
		else {
			for (Map.Entry<K,Collection<V>> entry : map2.entrySet()) {
				Collection<V> collectionInMap1 = map1.get(entry.getKey());
				Collection<V> collectionInMap2 = entry.getValue();
				Collection<V> entryUnion = addAllOrSame(collectionInMap1, collectionInMap2);
				if (entryUnion != collectionInMap1) {
					map1.put(entry.getKey(), entryUnion);
				}
			}
			result = map1;
		}
		return result;
	}

	/**
	 * Adds to a collection all given new elements in a second collection, returning the second collection
	 * same instance if the first is null or empty.
	 * @param collection
	 *        a collection
	 * @param newElements
	 *        new elements
	 * @return if collection null then newElements, else collection with newElements added to it.
	 * @param <T> the type of the elements in the collection.
	 */
	public static <T> Collection<T> addAllOrSame(Collection<T> collection, Collection<T> newElements) {
		Collection<T> result;
		if (collection == null || collection.isEmpty()) {
			result = newElements;
		}
		else {
			collection.addAll(newElements);
			result = collection;
		}
		return result;
	}

	/**
	 * Given a map, a function on its entries, and a value combination function,
	 * returns a pair of:
	 * <ul>
	 * <li> a new map containing the transformed entries;
	 * if multiple entries have their keys transformed to the same new key,
	 * their values will be combined with the value combination function.
	 * <li> a set of keys of the original entries that were transformed;
	 * an entry is considered transformed if the returned pair is not null,
	 * and if either its key or value identities (instances) have changed
	 * </ul>
	 * The function receives an entry from the original map as its input and returns
	 * a {@link Pair} with new key and new value as output.
	 *
	 * @param map
	 *        a map
	 * @param function
	 *        a function
	 * @param valueCombination
	 *        a value combination
	 * @return a pair with values as described in description.
	 * @param <K1> type of key 1
	 * @param <V1> type of value 1
	 * @param <K2> type of key 2
	 * @param <V2> type of value 2
	 */
	public static <K1, V1, K2, V2> Pair<Map<K2,V2>, Set<K1>>
	getTransformedSubMapAndOriginalKeysOfTransformedEntries(
			Map<K1, V1> map,
			Function<Map.Entry<K1, V1>, Pair<K2, V2>> function,
			BinaryFunction<V2, V2, V2> valueCombination) {
		Map<K2, V2> transformedSubMap = new LinkedHashMap<K2, V2>();
		Set<K1> originalKeysOfTransformedEntries = new LinkedHashSet<K1>();
		for (Map.Entry<K1, V1> entry : map.entrySet()) {
			Pair<K2, V2> transformedEntry = function.apply(entry);
			if (transformedEntry != null &&
					(transformedEntry.first != entry.getKey() || transformedEntry.second != entry.getValue())) {
				V2 newValue = transformedEntry.second;
				V2 alreadyPresentValueForNewKey = transformedSubMap.get(transformedEntry.first);
				if (alreadyPresentValueForNewKey == null) {
					newValue = transformedEntry.second;
				}
				else {
					newValue = valueCombination.apply(alreadyPresentValueForNewKey, transformedEntry.second);
				}
				transformedSubMap.put(transformedEntry.first, newValue);
				originalKeysOfTransformedEntries.add(entry.getKey());
			}
		}
		return Pair.make(transformedSubMap, originalKeysOfTransformedEntries);
	}
	
	/**
	 * Adds all elements of iterator's range to collection.
	 * 
	 * @param c
	 *            the collection to add the iterator's range to.
	 * @param i
	 *            the iterator whose range is to be added to the given
	 *            collection.
	 * @return the given collection.
	 * @param <T>
	 *            the type of the elements given.
	 * @param <C> the type of the collection
	 */
	public static <T, C extends Collection<T>> C addAll(C c, Iterator<T> i) {
		while (i.hasNext()) {
			c.add(i.next());
		}
		return c;
	}

	/**
	 * Adds all elements of iterator's range to a new linked list.
	 * 
	 * @param i
	 *            the iterator whose range is to be added to the list.
	 * @return the list.
	 * @param <T>
	 *            the type of the elements given.
	 */
	public static <T> LinkedList<T> addAllToList(Iterator<T> i) {
		LinkedList<T> result = new LinkedList<T>();
		while (i.hasNext()) {
			result.add(i.next());
		}
		return result;
	}

	/**
	 * Adds all elements of iterator's range to a new linked list.
	 * 
	 * @param i
	 *            the iterator whose range is to be added to the list.
	 * @return the list.
	 * @param <T>
	 *            the type of the elements given.
	 */
	public static <T> LinkedList<T> addAllToList(Collection<T> collection) {
		return addAllToList(collection.iterator());
	}

	/**
	 * Adds all elements of iterator's range to a new array list.
	 * 
	 * @param i
	 *            the iterator whose range is to be added to the list.
	 * @return the list.
	 * @param <T>
	 *            the type of the elements given.
	 */
	public static <T> ArrayList<T> addAllToArrayList(Iterator<T> i) {
		ArrayList<T> result = new ArrayList<T>();
		while (i.hasNext()) {
			result.add(i.next());
		}
		return result;
	}

	/**
	 * Adds all elements of iterator's range to a new linked hash set.
	 * 
	 * @param i
	 *            the iterator whose range is to be added to the set.
	 * @return the set.
	 * @param <T>
	 *            the type of the elements given.
	 */
	public static <T> LinkedHashSet<T> addAllToSet(Iterator<T> i) {
		LinkedHashSet<T> result = new LinkedHashSet<T>();
		while (i.hasNext()) {
			result.add(i.next());
		}
		return result;
	}

	/**
	 * @param map
	 *            the map to look up a value using the given key.
	 * @param key
	 *            the key to look up the given map with.
	 * @param defaultValue
	 *            the defaultValue to return if the given key is not in the map.
	 * @return value indexed by given key in map, or a default value if that is
	 *         null.
	 * @param <K>
	 *            the type of the Map's key.
	 * @param <V>
	 *            the type of the Map's value.
	 */
	public static <K, V> V getOrUseDefault(Map<K, V> map, K key, V defaultValue) {
		V result = map.get(key);
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	/**
	 * @param map
	 *            the map to look up a value using the given key.
	 * @param key
	 *            the key to look up the given map with.
	 * @param defaultMaker
	 *            a function to make a default object from.
	 * @return value indexed by given key in map, or a default value made by the default maker.
	 * @param <K>
	 *            the type of the Map's key.
	 * @param <V>
	 *            the type of the Map's value.
	 */
	public static <K, V> V getOrMakeAndPut(Map<K, V> map, K key, NullaryFunction<V> defaultMaker) {
		V result = map.get(key);
		if (result == null) {
			result = defaultMaker.apply();
			map.put(key, result);
		}
		return result;
	}

	/**
	 * Gets value mapped to key in given map or a default value if absent.
	 * @param map the map
	 * @param key the key
	 * @param defaultValue the default value
	 * @param <K> the type of the Map's key.
	 * @param <V> the type of the Map's value.
	 * @return the value associated with the key or, if absent, the default value.
	 */
	public static <K, V> V getValueOrDefault(Map<K, V> map, K key, V defaultValue) {
		V result = map.containsKey(key)? map.get(key) : defaultValue;
		return result;
	}

	/**
	 * Gets a value or creates one if non-existent.
	 * @param map the map
	 * @param key the key
	 * @param newValueClass the class of new values (default constructor is used)
	 * @param <K> the type of the Map's key.
	 * @param <V> the type of the Map's value.
	 * @return the existing or created value.
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> V getValuePossiblyCreatingIt(Map<K, V> map, K key, Class<?> newValueClass) {
		V value = map.get(key);
		if (value == null) {
			try {
				value = (V) newValueClass.getDeclaredConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			map.put(key, value);
		}
		return value;
	}

	/**
	 * Gets the value of a key or makes a new one with given function on key.
	 * @param map the map
	 * @param key the key
	 * @param makerFromKey the function making a new value from key
	 * @param <K> the type of the Map's key.
	 * @param <V> the type of the Map's value.
	 * @return the existing or new value.
	 */
	public static <K, V> V getValuePossiblyCreatingIt(Map<K, V> map, K key, Function<K, V> makerFromKey) {
		V value = map.get(key);
		if (value == null) {
			value = makerFromKey.apply(key);
			map.put(key, value);
		}
		return value;
	}

	public static File createTempFileWithContent(String content,
			String pattern, String suffix) {
		File temp = null;
		try {
			// Create temp file.
			temp = File.createTempFile(pattern, "." + suffix);

			// Delete temp file when program exits.
			temp.deleteOnExit();

			// Write to temp file
			BufferedWriter out = new BufferedWriter(new FileWriter(temp));
			out.write(content);
			out.close();
		} catch (IOException e) {
			Util.fatalError("Could not create temporary file.");
		}
		return temp;
	}

	public static <T1> boolean isEmpty(Iterable<T1> i) {
		return !i.iterator().hasNext();
	}

	/**
	 * 
	 * @param element
	 *            the element to test.
	 * @param iterable
	 *            An iterable object.
	 * @param predicate
	 *            the predicate to test if the given element has found a match
	 *            in the iterable object.
	 * @return an Iterator that has just iterated past an element <code>x</code>
	 *         of <code>iterable</code> such that
	 *         <code>predicate.evaluate(element, x)</code> is true, or
	 *         <code>null</code> if there is no such element.
	 * @param <E>
	 *            the type of the element to find.
	 */
	public static <E> Iterator<E> find(E element, Iterable<E> iterable,
			BinaryPredicate<E, E> predicate) {
		Iterator<E> i = iterable.iterator();
		boolean foundIt = false;
		while (i.hasNext() && !(foundIt = predicate.apply(element, i.next()))) {
			;
		}
		if (foundIt) {
			return i;
		}
		return null;
	}

	public static <E> boolean removeAnyTwoMatchingElements(Iterable<E> a,
			Iterable<E> b, BinaryPredicate<E, E> predicate) {
		Iterator<E> aI = a.iterator();
		Iterator<E> bI = null;
		while (aI.hasNext() && (bI = find(aI.next(), b, predicate)) != null) {
			;
		}
		if (bI != null) {
			aI.remove();
			bI.remove();
			return true;
		}
		return false;
	}

	/**
	 * If there is no element in collection satisfying given predicate, returns
	 * null. Otherwise, returns list with elements in iteration order, excluding
	 * the first satisfying element.
	 * 
	 * @param c
	 *            the collection to copy elements from.
	 * @param p
	 *            the predicate to test elements on the given collection for
	 *            whether or not they should be copied or not.
	 * @return null if no element in given collection satisfying given
	 *         predicate, otherwise a list with elements in iteration order,
	 *         excluding the first element satisfying the predicate.
	 * @param <T>
	 *            the type of the elements in the collection.
	 */
	public static <T> List<T> listCopyWithoutSatisfyingElementOrNull(
			Collection<T> c, Predicate<T> p) {
		Pair<T, List<T>> satisfyingElementAndListCopyWithoutIt = findSatisfyingElementAndListCopyWithoutItOrNull(
				c, p);
		return satisfyingElementAndListCopyWithoutIt.second;
	}

	/**
	 * If there is no element in collection satisfying given predicate, returns
	 * null. Otherwise, returns pair with found element and a list with elements
	 * in iteration order, excluding the first satisfying element.
	 * 
	 * @param c
	 *            the collection to copy elements from.
	 * @param p
	 *            the predicate to test elements on the given collection for
	 *            whether or not they should be copied or not.
	 * @return null if no element in given collection satisfying given
	 *         predicate, otherwise a pair with found element and a list with
	 *         elements in iteration order, excluding the first satisfying
	 *         element.
	 * @param <T>
	 *            the type of the elements in the collection.
	 */
	public static <
			T> Pair<T, List<T>> findSatisfyingElementAndListCopyWithoutItOrNull(Collection<T> c, Predicate<T> p) {
		Pair<T, List<T>> result = null;
		final AtomicInteger elementIndex = new AtomicInteger(-1);
		Optional<T> first = c.stream().filter(e -> {
			elementIndex.incrementAndGet();
			return p.apply(e);
		}).findFirst();
		if (first.isPresent()) {
			// we create another list and add all elements, but found one, to
			// it.
			List<T> copy = makeCopyButForElementAtGivenIndex(c, elementIndex.intValue());
			result = Pair.make(first.get(), copy);
		}

		return result;
	}

	/**
	 * Given a collection and an index, returns a list with the collection's
	 * elements in iteration order, excluding the elementIndex-th element.
	 * 
	 * @param c
	 *            the collection whose elements are to be copied.
	 * @param elementIndex
	 *            the index of the element to be excluded from the copied
	 *            collection.
	 * @return a list wit the collection's elements in iteration order,
	 *         excluding the elementIndex-th element.
	 * @param <T>
	 *            the type of the elements in the collection.
	 */
	public static <T> List<T> makeCopyButForElementAtGivenIndex(
			Collection<T> c, int elementIndex) {

		final AtomicInteger currentIndex = new AtomicInteger(-1);
		List<T> result = c.stream()
				.filter(e -> currentIndex.incrementAndGet() != elementIndex)
				.collect(toLinkedList());

		return result;
	}

	/**
	 * Stores iterator's range in a new, empty list and returns it.
	 * 
	 * @param iterator
	 *            the iterator whose range is to be stored in a new List.
	 * @return a new List populated with the elements from the given iterator's
	 *         range.
	 * @param <T>
	 *            the type of the elements iterated over.
	 */
	public static <T> List<T> listFrom(Iterator<? extends T> iterator) {
		LinkedList<T> result = new LinkedList<T>();
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
	}

	public static <T> List<T> listFrom(Iterable<? extends T> iterable) {
		return listFrom(iterable.iterator());
	}

	/**
	 * Stores iterator's range in a new, empty array list and returns it.
	 * 
	 * @param iterator
	 *            the iterator whose range is to be stored in a new ArrayList.
	 * @return a new ArrayList populated with the elements from the given iterator's
	 *         range.
	 * @param <T>
	 *            the type of the elements iterated over.
	 */
	public static <T> ArrayList<T> arrayListFrom(Iterator<? extends T> iterator) {
		ArrayList<T> result = new ArrayList<T>();
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
	}

	/**
	 * Stores collection's elements in a new, empty array list and returns it.
	 * 
	 * @param collection
	 *            the collection whose elements are to be stored in a new ArrayList.
	 * @return a new ArrayList populated with the elements from the given collection's elements.
	 * @param <T>
	 *            the type of the elements in the collection.
	 */
	public static <T> ArrayList<T> arrayListFrom(Collection<? extends T> collection) {
		return arrayListFrom(collection.iterator());
	}

	/**
	 * Makes a list out of an array.
	 * 
	 * @param array
	 *            the array from which to construct a list.
	 * @return a List containing the elements of the given array.
	 * @param <T>
	 *            the type of the array's elements.
	 */
	public static <T> List<T> listFrom(T[] array) {
		LinkedList<T> result = new LinkedList<T>();
		for (int i = 0; i != array.length; i++) {
			result.add(array[i]);
		}
		return result;
	}
	
	/**
	 * Makes a list of Integers out of an array of ints.
	 * 
	 * @param array
	 *            the array of ints from which to construct a list of Integers.
	 * @return a List containing the elements of the given array.
	 * @param <T>
	 *            the type of the array's elements.
	 */
	public static List<Integer> listFrom(int[] array) {
		return listFrom(ArrayUtils.toObject(array));
	}
	
	public static <T> LinkedHashSet<T> setFrom(Iterator<? extends T> iterator) {
		LinkedHashSet<T> result = set();
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
	}

	public static <T> LinkedHashSet<T> setFrom(Iterable<? extends T> iterable) {
		return setFrom(iterable.iterator());
	}

	public static <T> HashSet<T> hashSetFrom(Iterator<? extends T> iterator) {
		HashSet<T> result = new HashSet<>();
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
	}

	public static <T> LinkedHashSet<T> setFrom(Collection<? extends T> collection) {
		return setFrom(collection.iterator());
	}

	/**
	 * Stores results of applying a function to an iterator's range in a given list.
	 * 
	 * @param iterator
	 *            the iterator's whose range a function is to be applied to.
	 * @param function
	 *            the function to be applied to the given iterator's range.
	 * @param result a list to which we add the results from the function applications on the given
	 *         iterator's range.
	 * @param <F>
	 *            the type of the iterators arguments.
	 * @param <T>
	 *            the result type of the function applied to the iterator's
	 *            range.
	 */
	public static <F, T> void mapIntoList(Iterator<? extends F> iterator, Function<F, T> function, List<T> result) {
		while (iterator.hasNext()) {
			F nextElement = iterator.next();
			result.add(function.apply(nextElement));
		}
	}

	/**
	 * Stores results of applying a function to the elements of a collection in a given list.
	 * 
	 * @param collection
	 *            the collection to whose elements a function is to be applied to.
	 * @param function
	 *            the function to be applied to the given iterator's range.
	 * @param result a list to which we add the results from the function applications on the given
	 *         collection elements.
	 * @param <F>
	 *            the type of the collection elements.
	 * @param <T>
	 *            the result type of the function applied to the collection elements.
	 */
	public static <F, T> void mapIntoList(Collection<? extends F> collection, Function<F, T> function, List<T> result) {
		mapIntoList(collection.iterator(), function, result);
	}

	
	/**
	 * Stores results of applying a function to an iterator's range in a new,
	 * empty list and returns it.
	 * 
	 * @param iterator
	 *            the iterator's whose range a function is to be applied to.
	 * @param function
	 *            the function to be applied to the given iterator's range.
	 * @return a List of the results from the function applications on the given
	 *         iterator's range.
	 * @param <F>
	 *            the type of the iterators arguments.
	 * @param <T>
	 *            the result type of the function applied to the iterator's
	 *            range.
	 */
	public static <F, T> List<T> mapIntoList(Iterator<? extends F> iterator,
			Function<F, T> function) {
		List<T> result = new LinkedList<T>();
		mapIntoList(iterator, function, result);
		return result;
	}

	/**
	 * Same as {@link #mapIntoList(Iterator, Function)} using <code>new IntegerIterator(firstInclusive, lastExclusive)</code>.
	 * @param firstInclusive
	 * @param lastExclusive
	 * @param function
	 * @return
	 */
	public static <T> List<T> mapIntegersIntoList(int firstInclusive, int lastExclusive, Function<Integer, T> function) {
		List<T> result = new LinkedList<T>();
		mapIntoList(new IntegerIterator(firstInclusive, lastExclusive), function, result);
		return result;
	}

	/**
	 * Same as {@link #mapIntoList(Iterator, Function)} using <code>new IntegerIterator(0, lastExclusive)</code>.
	 * @param lastExclusive
	 * @param function
	 * @return
	 */
	public static <T> List<T> mapIntegersIntoList(int lastExclusive, Function<Integer, T> function) {
		return mapIntegersIntoList(0, lastExclusive, function);
	}


	/**
	 * Stores results of applying a function to a collection's elements in a
	 * new, empty list and returns it.
	 * 
	 * @param collection
	 *            the collection whose elements a function is to be applied to.
	 * @param function
	 *            the function to be applied to the given collection's elements.
	 * @return a List of the results from the function applications on the given
	 *         collection's elements.
	 * @param <F>
	 *            the type of the collection's elements.
	 * @param <T>
	 *            the result type of the function applied to the collection's
	 *            elements.
	 */
	public static <F, T> List<T> mapIntoList(
			Collection<? extends F> collection, Function<F, T> function) {
		return mapIntoList(collection.iterator(), function);
	}

	/**
	 * Stores results of applying a binary function to the values of two iterators iterated in parallel.
	 * 
	 * @param iterator1
	 *            the first iterator's whose range a function is to be applied to.
	 * @param iterator2
	 *            the second iterator's whose range a function is to be applied to.
	 * @param function
	 *            the function to be applied to the pair of values from the given iterators's ranges.
	 * @param result a list to which we add the results from the function applications on the
	 * 		   the pairs of values from the iterators.
	 * @param <F1>
	 *            the type of the first iterator's arguments.
	 * @param <F2>
	 *            the type of the second iterator's arguments.
	 * @param <T>
	 *            the result type of the function applied to the iterators's elements.
	 */
	public static <F1, F2, T> void mapIntoList(
			Iterator<? extends F1> iterator1, 
			Iterator<? extends F2> iterator2, 
			BinaryFunction<F1, F2, T> function, List<T> result) {
		try {
			while (iterator1.hasNext() || iterator2.hasNext()) {
				F1 nextElement1 = iterator1.next();
				F2 nextElement2 = iterator2.next();
				result.add(function.apply(nextElement1, nextElement2));
			}
		}
		catch (NoSuchElementException e) {
			throw new Error("Util.mapIntoList requires iterators to have ranges of same size");
		}
	}

	/**
	 * Stores results of applying a binary function to the values of two collections iterated in parallel.
	 * 
	 * @param collection1
	 *            the first collection.
	 * @param collection2
	 *            the second collection.
	 * @param function
	 *            the function to be applied to the pair of values from the given collections.
	 * @return a list to which we add the results from the function applications on the
	 * 		   the pairs of values from the collections.
	 * @param <F1>
	 *            the type of the first collection arguments.
	 * @param <F2>
	 *            the type of the second collection arguments.
	 * @param <T>
	 *            the result type of the function applied to the collections' values.
	 */
	public static <F1, F2, T> List<T> mapIntoList(
			Collection<? extends F1> collection1, 
			Collection<? extends F2> collection2, 
			BinaryFunction<F1, F2, T> function) {
		List<T> result = list();
		mapIntoList(collection1.iterator(), collection2.iterator(), function, result);
		return result;
	}

	/**
	 * Stores results of applying a function to a collection's elements in a
	 * new, empty array list and returns it.
	 * 
	 * @param collection
	 *            the collection whose elements a function is to be applied to.
	 * @param function
	 *            the function to be applied to the given collection's elements.
	 * @return an ArrayList of the results from the function applications on the
	 *         given collection's elements.
	 * @param <F>
	 *            the type of the collection's elements.
	 * @param <T>
	 *            the result type of the function applied to the collection's
	 *            elements.
	 */
	public static <F, T> ArrayList<T> mapIntoArrayList(
			Collection<? extends F> collection, Function<F, T> function) {

		ArrayList<T> result = new ArrayList<T>(collection.size());
		for (F element : collection) {
			result.add(function.apply(element));
		}

		return result;
	}

	/**
	 * Stores results of applying a function to iterator's range in a
	 * new, empty array list and returns it.
	 * 
	 * @param iterator
	 *            the iterator whose range's elements a function is to be applied to.
	 * @param function
	 *            the function to be applied to the given iterator's range elements.
	 * @return an ArrayList of the results from the function applications on the
	 *         given iterator's range elements.
	 * @param <F>
	 *            the type of the iterator's range elements.
	 * @param <T>
	 *            the result type of the function applied to the iterator's range elements.
	 */
	public static <F, T> ArrayList<T> mapIntoArrayList(
			Iterator<? extends F> iterator, Function<F, T> function) {

		ArrayList<T> result = new ArrayList<T>();
		for (F element : in(iterator)) {
			result.add(function.apply(element));
		}

		return result;
	}
	
	/**
	 * Stores results of applying a function to an array's elements in a
	 * new, empty array list and returns it.
	 * 
	 * @param array
	 *            the array whose elements a function is to be applied to.
	 * @param function
	 *            the function to be applied to the given collection's elements.
	 * @return an ArrayList of the results from the function applications on the
	 *         given collection's elements.
	 * @param <F>
	 *            the type of the collection's elements.
	 * @param <T>
	 *            the result type of the function applied to the collection's
	 *            elements.
	 */
	public static <F, T> ArrayList<T> mapIntoArrayList(F[] array, Function<F, T> function) {

		ArrayList<T> result = Arrays
				.stream(array)
				.map(function::apply)
				.collect(toArrayList(array.length));

		return result;
	}
	
	/**
	 * Returns an array list containing the results of applying a given function to the integers from 0 to <code>lastExclusive - 1</code>.
	 * @param lastExclusive
	 * @param function
	 * @return
	 */
	public static <T> ArrayList<T> mapIntegersIntoArrayList(int lastExclusive, Function<Integer, T> function) {
		return mapIntegersIntoArrayList(0, lastExclusive, function);
	}
	
	/**
	 * Returns an array list containing the results of applying a given function to the integers from <code>begin</code> to <code>end - 1</code>.
	 * @param begin
	 * @param end
	 * @param function
	 * @return
	 */
	public static <T> ArrayList<T> mapIntegersIntoArrayList(int begin, int end, Function<Integer, T> function) {
		ArrayList<T> result = new ArrayList<T>(end - begin);
		for (int i = begin; i != end; i++) {
			result.add(function.apply(i));
		}
		return result;
	}

	/**
	 * Returns an array list containing the results of applying a given function to the integers from an integer iterator.
	 * @param indicesIterator
	 * @param function
	 * @return
	 */
	public static <T> ArrayList<T> mapIntegersIntoArrayList(Iterator<? extends Integer> indicesIterator, Function<Integer, T> function) {
		ArrayList<T> result = new ArrayList<T>();
		for (int i : in(indicesIterator)) {
			result.add(function.apply(i));
		}
		return result;
	}

	/**
	 * Returns an array list containing the results of applying a given function to the integers from an integer collection.
	 * @param indices
	 * @param function
	 * @return
	 */
	public static <T> ArrayList<T> mapIntegersIntoArrayList(Collection<? extends Integer> indices, Function<Integer, T> function) {
		ArrayList<T> result = new ArrayList<T>();
		for (int i : indices) {
			result.add(function.apply(i));
		}
		return result;
	}

	public static <F, T> List<T> mapIntoList(F[] array, Function<F, T> function) {
		return mapIntoList(Arrays.asList(array), function);
	}

	/**
	 * Similar to {@link #mapIntoList(Collection, Function)} but applying a function to 
	 * the elements of a list that are instances of a given class only.
	 * @param iterable
	 * @param selectedClass
	 * @param function
	 * @return the {@link LinkedList<T2>} of results of applying the function to the instances of selectedClass in iterable.
	 */
	public static <T1, T2 extends T1, R> LinkedList<R> mapInstancesOfClassIntoList(
			Iterable<? extends T1> iterable, 
			Class<T2> selectedClass, 
			Function<? super T2, ? extends R> function) {
		
		return mapInstancesOfClassIntoList(iterable, selectedClass, function, list());
	}

	/**
	 * Similar to {@link #mapIntoList(Collection, Function, List)} but applying a function to 
	 * the elements of a list that are instances of a given class only.
	 * @param iterable
	 * @param selectedClass
	 * @param function
	 * @return the {@link LinkedList<T2>} of results of applying the function to the instances of selectedClass in iterable.
	 */
	public static <T2 extends T1, R, T1, L extends List<R>> L mapInstancesOfClassIntoList(
			Iterable<? extends T1> iterable,
			Class<T2> selectedClass,
			Function<? super T2, ? extends R> function,
			L result) {
		
		for (T1 t1 : iterable) {
			if (selectedClass.isInstance(t1)) {
				T2 t2 = selectedClass.cast(t1);
				R resultForT2 = function.apply(t2);
				result.add(resultForT2);
			}
		}
		return result;
	}

	/**
	 * Similar to {@link #mapIntoList(Collection, Function)} but indicating if results are the same as original elements (using {@link Object#equals(Object)}.
	 * @param iterable
	 * @param function
	 * @return
	 */
	@SuppressWarnings("unlikely-arg-type")
	public static 
	<I, O> 
	Pair<List<O>, Boolean> 
	mapIntoListAndTellIfThereWasChange(Iterable<? extends I> iterable, Function<? super I, ? extends O> function) {
		List<O> list = list();
		boolean changed = false;
		for (I element : iterable) {
			O newElement = function.apply(element);
			list.add(newElement);
			changed = changed || !newElement.equals(element);
		}
		return pair(list, changed);
	}

	/**
	 * Returns an array containing the results of applying a given function to the integers from 0 to <code>lastExclusive - 1</code>.
	 * @param lastExclusive
	 * @param function
	 * @return
	 */
	public static <T> T[] mapIntegersIntoArray(Class<T> clazz, int lastExclusive, Function<Integer, T> function) {
		return mapIntegersIntoArray(clazz, 0, lastExclusive, function);
	}
	
	/**
	 * Returns an array containing the results of applying a given function to the integers from <code>begin</code> to <code>end - 1</code>.
	 * @param begin
	 * @param end
	 * @param function
	 * @return
	 */
	public static <T> T[] mapIntegersIntoArray(Class<T> clazz, int begin, int end, Function<Integer, T> function) {
		@SuppressWarnings("unchecked")
		T[] result = (T[]) Array.newInstance(clazz, end - begin);
		int arrayIndex = 0;
		for (int i = begin; i != end; i++) {
			result[arrayIndex] = function.apply(i);
			arrayIndex++;
		}
		return result;
	}

	/**
	 * Stores results of applying a function to a collection's elements in a
	 * new array and returns it.
	 * 
	 * @param clazz the type of the objects in the array
	 * @param collection
	 *            the collection whose elements a function is to be applied to.
	 * @param function
	 *            the function to be applied to the given collection's elements.
	 * @return an array of the results from the function applications on the
	 *         given collection's elements.
	 * @param <F>
	 *            the type of the collection's elements.
	 * @param <T>
	 *            the result type of the function applied to the collection's
	 *            elements.
	 */
	public static <F, T> T[] mapIntoArray(Class<T> clazz,
			Collection<? extends F> collection, Function<F, T> function) {

		@SuppressWarnings("unchecked")
		T[] result = (T[]) Array.newInstance(clazz, collection.size());
		int i = 0;
		for (F element : collection) {
			result[i++] = function.apply(element);
		}

		return result;
	}

	/**
	 * Stores results of applying a function to a iterator's elements in a
	 * new array and returns it.
	 * 
	 * @param clazz the type of the objects in the array
	 * @param iterator
	 *            the iterator whose elements a function is to be applied to.
	 * @param size
	 *            the size of the allocated array (must be at least as large as the iterator's range or an error will be thrown).
	 * @param function
	 *            the function to be applied to the given iterator's elements.
	 * @return an array of the results from the function applications on the
	 *         given iterator's elements.
	 * @param <F>
	 *            the type of the iterator's elements.
	 * @param <T>
	 *            the result type of the function applied to the iterator's
	 *            elements.
	 */
	public static <F, T> T[] mapIntoArray(Class<T> clazz, int size,
			Iterator<? extends F> iterator, Function<F, T> function) {

		@SuppressWarnings("unchecked")
		T[] result = (T[]) Array.newInstance(clazz, size);
		int i = 0;
		for (F element : in(iterator)) {
			result[i++] = function.apply(element);
		}

		return result;
	}
	
	/**
	 * Stores results of applying a function to an array's elements in a
	 * new array and returns it.
	 * 
	 * @param <F>  the type of the input-array's elements.
	 * @param <T>  the result type of the function applied to the input-array's elements.
	 * @param inputArray
	 * @param function
	 * @return
	 * 
	 * @author Bobak
	 */
	public static <F, T> T[] mapIntoArray(F[] inputArray, Class<T> clazz, Function<F, T> function) {

		@SuppressWarnings("unchecked")
		T[] result = (T[]) Array.newInstance(clazz, inputArray.length);
		for(int i = 0; i < inputArray.length; ++i)
		{
			result[i] = function.apply(inputArray[i]);
		}
		return result;
	}

	/**
	 * Stores results of applying a function to a collection's elements in a
	 * new array of Objects and returns it.
	 * 
	 * @param collection
	 *            the collection whose elements a function is to be applied to.
	 * @param function
	 *            the function to be applied to the given collection's elements.
	 * @return an array of the results from the function applications on the
	 *         given collection's elements.
	 * @param <F>
	 *            the type of the collection's elements.
	 * @param <T>
	 *            the result type of the function applied to the collection's
	 *            elements.
	 */
	public static <F, T> T[] mapIntoObjectArray(Collection<? extends F> collection, Function<F, T> function) {

		@SuppressWarnings("unchecked")
		T[] result = (T[]) new Object[collection.size()];
		int i = 0;
		for (F element : collection) {
			result[i++] = function.apply(element);
		}

		return result;
	}

	/**
	 * Indicates whether two collections contain the exact same instances in the
	 * same (iterable) order.
	 * 
	 * @param c1
	 *            the first collection to test.
	 * @param c2
	 *            the second collection to test.
	 * @return true if the two collections contain the eact same instances in
	 *         the same (iterable) order, false otherwise.
	 * @param <T>
	 *            the type of the elements in the given collections.
	 */
	public static <T> boolean sameInstancesInSameIterableOrder(
			Collection<T> c1, Collection<T> c2) {
		Iterator<T> i1 = c1.iterator();
		Iterator<T> i2 = c2.iterator();
		while (i1.hasNext() && i2.hasNext()) {
			if (i1.next() != i2.next()) {
				return false;
			}
		}
		boolean result = !i1.hasNext() && !i2.hasNext(); // only true if they
														 // are both done at
														 // the same time
		return result;
	}

	/**
	 * Stores results of applying a function to an set's elements in a new,
	 * empty hash set and returns it.
	 * 
	 * @param set
	 *            the set to map from.
	 * @param function
	 *            the function to apply to each element in the set.
	 * @return the results of applying the given function to the elements of the
	 *         given set.
	 * @param <F>
	 *            the type of the elements in the Set.
	 * @param <T>
	 *            the type of the result from applying the given function.
	 */
	public static <F, T> Set<T> mapIntoSet(Collection<? extends F> set,
			Function<F, T> function) {

		Set<T> result = set
				.stream()
				.map(function::apply)
				.collect(toLinkedHashSet(set.size()));

		return result;
	}

	public static <F, T> Set<T> mapIntoSet(Collection<? extends F> set, Function<F, T> function, Set<T> result) {
		for (F f : set) {
			result.add(function.apply(f));
		}
		return result;
	}

	/**
	 * Stores results of applying a function to an collection's elements in a new,
	 * empty linked hash set and returns it if any elements are distinct instances from originals,
	 * or original collection otherwise.
	 * 
	 * @param set
	 *            the set to map from.
	 * @param function
	 *            the function to apply to each element in the set.
	 * @return the results of applying the given function to the elements of the
	 *         given set, or same set if there are no changes
	 * @param <T>
	 *            the type of the result from applying the given function.
	 */
	public static <T> Collection<T> mapIntoSetOrSameIfNoDistinctElementInstances(Collection<T> set,
			Function<T, T> function) {

		Collection<T> possibleResult = new LinkedHashSet<T>();
		boolean change = false;
		for (T element : set) {
			T elementResult = function.apply(element);
			possibleResult.add(elementResult);
			change = change || elementResult != element;
		}

		return change? possibleResult : set;
	}

	/**
	 * Stores results of applying a function to the values of a map in a new,
	 * empty linked hash map and returns it.
	 * 
	 * @param map
	 *            the map whose values to apply to function on.
	 * @param function
	 *            the function to apply to each element in the set.
	 * @return the results of applying the given function to the elements of the
	 *         given set.
	 * @param <K>
	 *            the type of the keys in the maps.
	 * @param <V1>
	 *            the type of the values in the input map.
	 * @param <V2>
	 *            the type of the values in the output map.
	 */
	public static <K, V1, V2> LinkedHashMap<K, V2>
	applyFunctionToValuesOfMap(
			Map<? extends K, ? extends V1> map,
			Function<V1, V2> function) {

		LinkedHashMap<K, V2> result = map();
		for (Map.Entry<? extends K, ? extends V1> entry : map.entrySet()) {
			result.put(entry.getKey(), function.apply(entry.getValue()));
		}
		return result;
	}

	/**
	 * Collects elements in an iterator's range satisfying two different
	 * conditions, returning false if some element does not satisfy either.
	 * 
	 * @param iterator
	 *            an iterator over a collection of elements to be tested.
	 * @param condition1
	 *            the test to be applied to the iterator's range to be passed in
	 *            order to add elements to the given satisfyingCondition1
	 *            collection.
	 * @param elementsSatisfyingCondition1
	 *            populated with elements that satisfy condition1.
	 * @param condition2
	 *            the test to be applied to the iterator's range to be passed in
	 *            order to add elements to the given satisfyingCondition2
	 *            collection.
	 * @param elementsSatisfyingCondition2
	 *            populated with elements that satisfy condition2.
	 * @return false is some element in the given iterator's range does not
	 *         satisfy both given conditions, true otherwise.
	 * @param <E>
	 *            the type of the elements in the given collections.
	 */
	public static <E> boolean collectOrReturnFalseIfElementDoesNotFitEither(
			Iterator<E> iterator, Predicate<E> condition1,
			Collection<E> elementsSatisfyingCondition1, Predicate<E> condition2,
			Collection<E> elementsSatisfyingCondition2) {
		while (iterator.hasNext()) {
			E object = iterator.next();
			boolean result1;
			boolean result2;
			if (result1 = condition1.apply(object)) {
				elementsSatisfyingCondition1.add(object);
			}
			if (result2 = condition2.apply(object)) {
				elementsSatisfyingCondition2.add(object);
			}

			if (!result1 && !result2) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Collects elements in a collection satisfying two different conditions,
	 * returning false if some element does not satisfy either.
	 * 
	 * @param collection
	 *            a collection of elements to be tested.
	 * @param condition1
	 *            the test to be applied to the elements of the given collection
	 *            to be passed in order to add elements to the given
	 *            satisfyingCondition1 collection.
	 * @param elementsSatisfyingCondition1
	 *            populated with elements that satisfy condition1.
	 * @param condition2
	 *            the test to be applied to the elements of the given collection
	 *            to be passed in order to add elements to the given
	 *            satisfyingCondition2 collection.
	 * @param elementsSatisfyingCondition2
	 *            populated with elements that satisfy condition2.
	 * @return false is some element in the given collection does not satisfy
	 *         both given conditions, true otherwise.
	 * @param <E>
	 *            the type of the elements in the given collections.
	 */
	public static <E> boolean collectOrReturnFalseIfElementDoesNotFitEither(
			Collection<E> collection, Predicate<E> condition1,
			Collection<E> elementsSatisfyingCondition1, Predicate<E> condition2,
			Collection<E> elementsSatisfyingCondition2) {
		return collectOrReturnFalseIfElementDoesNotFitEither(
				collection.iterator(), condition1, elementsSatisfyingCondition1,
				condition2, elementsSatisfyingCondition2);
	}

	/**
	 * Collects elements in an iterator's range into two different given
	 * collections, one with the N first elements satisfying a condition and the
	 * other with the elements not doing so. Returns the index of the first of
	 * the n elements satisfying condition or -1 if there aren't n element
	 * satisfying the predicate.
	 * 
	 * @param iterator
	 *            the iterator over whose range the first n are to be collected
	 *            from
	 * @param n
	 *            the maximum number to be collected.
	 * @param condition
	 *            the satisfying condition predicate
	 * @param elementsSatisfyingCondition
	 *            those elements from the iterator's range that satisfy the
	 *            condition.
	 * @param remainingElements
	 *            those elements from the iterator's range that don't satisfy
	 *            the condition.
	 * @return the index of the first of the n elements satisfying the
	 *         condition, -1 if there aren't n elements satisfying the
	 *         condition.
	 * @param <E>
	 *            the type of the elements from the iterator's range.
	 */
	public static <E> int collectFirstN(Iterator<E> iterator, int n,
			Predicate<E> condition, Collection<E> elementsSatisfyingCondition,
			Collection<E> remainingElements) {
		int i = 0;
		int result = -1;
		while (iterator.hasNext()) {
			E object = iterator.next();
			if (n > 0 && condition.apply(object)) {
				elementsSatisfyingCondition.add(object);
				n--;
				if (result == -1) {
					result = i;
				}
			} else {
				remainingElements.add(object);
			}
			i++;
		}
		if (n != 0) {
			return -1;
		}
		return result;
	}

	/**
	 * Collects elements in a collection into two different given collections,
	 * one with the N first elements satisfying a condition and the other with
	 * the elements not doing so. Returns the index of the first of the n
	 * elements satisfying condition or -1 if there aren't n element satisfying
	 * the predicate.
	 * 
	 * @param c
	 *            the collection the first n elements are to be collected from.
	 * @param n
	 *            the maximum number to be collected.
	 * @param condition
	 *            the satisfying condition predicate
	 * @param elementsSatisfyingCondition
	 *            those elements from the collection that satisfy the condition.
	 * @param remainingElements
	 *            those elements from the collection that don't satisfy the
	 *            condition.
	 * @return the index of the first of the n elements satisfying the
	 *         condition, -1 if there aren't n elements satisfying the
	 *         condition.
	 * @param <E>
	 *            the type of the elements from the input collection.
	 */
	public static <E> int collectFirstN(Collection<E> c, int n,
			Predicate<E> condition, Collection<E> elementsSatisfyingCondition,
			Collection<E> remainingElements) {
		int result = collectFirstN(c.iterator(), n, condition,
				elementsSatisfyingCondition, remainingElements);
		return result;
	}

	/**
	 * Collects elements in a given iterable into two different given
	 * collections, one with the elements satisfying a condition and the other
	 * with the elements not doing so. Returns the index of the first element
	 * satisfying condition.
	 * 
	 * @param iterable
	 *            an iterable over the elements to be tested.
	 * @param condition
	 *            the predicate used to test the elements in the given iterable.
	 * @param elementsSatisfyingCondition
	 *            elements from the iterable that satisfy the given condition
	 *            will be added to this collection.
	 * @param remainingElements
	 *            elements from the iterable that do not satisfy the given
	 *            condition will be added to this collection.
	 * @return the index of the first element satisfying the condition, -1 if
	 *         none satisfy.
	 * @param <E>
	 *            the type of the elements being collected.
	 */
	public static <E> int collect(
			Iterable<? extends E> iterable,
			Predicate<? super E> condition, 
			Collection<? super E> elementsSatisfyingCondition,
			Collection<? super E> remainingElements) {
		final AtomicInteger i = new AtomicInteger(0);
		final AtomicInteger result = new AtomicInteger(-1);
		iterable.forEach(e -> {
			if (condition.apply(e)) {
				elementsSatisfyingCondition.add(e);
				if (result.intValue() == -1) {
					result.set(i.intValue());
				}
			} else {
				remainingElements.add(e);
			}
			i.incrementAndGet();
		});
		return result.intValue();
	}

	/**
	 * Collects elements in a collection satisfying a given predicate into a
	 * given collection, returning the latter.
	 * 
	 * @param collection
	 *            the collection from which elements are to be collected from.
	 * @param predicate
	 *            the test to be used to select collect elements from the given
	 *            collection.
	 * @param collected
	 *            elements from the given collection that pass the given
	 *            predicate test will be added to this collection.
	 * @return collected.
	 * @param <E>
	 *            the type of the elements being collected.
	 */
	public static <E> Collection<? super E> collect(Collection<? extends E> collection,
			Predicate<? super E> predicate, Collection<? super E> collected) {
		collection.stream().filter(predicate::apply)
				.forEach(collected::add);
		return collected;
	}

	/**
	 * Collects elements in an iterator's range satisfying a given predicate
	 * into a given collection, returning the latter.
	 * 
	 * @param iterator
	 *            an iterator over whose range elements are to be collected
	 *            from.
	 * @param predicate
	 *            the test to be used to select iterated elements from the given
	 *            iterator.
	 * @param collected
	 *            elements from the given iterator's range that pass the given
	 *            predicate test will be added to this collection.
	 * @return collected.
	 * @param <E>
	 *            the type of the elements being collected.
	 */
	public static <E> Collection<E> collect(Iterator<? extends E> iterator,
			Predicate<E> predicate, Collection<E> collected) {

		iterator.forEachRemaining(e -> {
			if (predicate.apply(e)) {
				collected.add(e);
			}
		});

		return collected;
	}

	/**
	 * Collects elements in a collection satisfying a given predicate into a new
	 * linked list and returns it.
	 * 
	 * @param collection
	 *            the collection from which elements are to be copied.
	 * @param predicate
	 *            the test to be applied to determine which elements should be
	 *            copied into the returned list.
	 * @return a List of the elements in the collection that matched the given
	 *         predicate.
	 * @param <T>
	 *            the type of the elements to collect.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> collectToList(Collection<? extends T> collection, Predicate<? super T> predicate) {
		return (List<T>) collect(collection, predicate, new LinkedList<T>());
	}

	/**
	 * Collects elements in an iterator's range satisfying a given predicate into a new
	 * linked list and returns it.
	 * 
	 * @param iterator
	 *            the iterator from which elements are to be copied.
	 * @param predicate
	 *            the test to be applied to determine which elements should be
	 *            copied into the returned list.
	 * @return a List of the elements in the collection that matched the given
	 *         predicate.
	 * @param <T>
	 *            the type of the elements to collect.
	 */
	public static <T> List<T> collectToList(Iterator<T> iterator,
			Predicate<T> predicate) {
		return (List<T>) collect(iterator, predicate, new LinkedList<T>());
	}

	/**
	 * Collects elements in a collection satisfying a given predicate into a new
	 * array list and returns it.
	 * 
	 * @param collection
	 *            the collection from which elements are to be copied.
	 * @param predicate
	 *            the test to be applied to determine which elements should be
	 *            copied into the returned list.
	 * @return a List of the elements in the collection that matched the given
	 *         predicate.
	 * @param <T>
	 *            the type of the elements to collect.
	 */
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> collectToArrayList(Collection<? extends T> collection, Predicate<T> predicate) {
		return (ArrayList<T>) collect(collection, predicate, new ArrayList<T>());
	}

	/**
	 * Collects elements in an iterator's range satisfying a given predicate into a new
	 * array list and returns it.
	 * 
	 * @param iterator
	 *            the iterator from which elements are to be copied.
	 * @param predicate
	 *            the test to be applied to determine which elements should be
	 *            copied into the returned list.
	 * @return a List of the elements in the iterator's range that matched the given
	 *         predicate.
	 * @param <T>
	 *            the type of the elements to collect.
	 */
	public static <T> ArrayList<T> collectToArrayList(Iterator<T> iterator, Predicate<T> predicate) {
		return (ArrayList<T>) collect(iterator, predicate, new ArrayList<T>());
	}

	/**
	 * Collects elements in an iterator's range satisfying a given predicate into a new
	 * linked hash set and returns it.
	 * 
	 * @param iterator
	 *            the iterator from which elements are to be copied.
	 * @param predicate
	 *            the test to be applied to determine which elements should be
	 *            copied into the returned set.
	 * @return a Set of the elements in the iterator's range that matched the given
	 *         predicate.
	 * @param <T>
	 *            the type of the elements to collect.
	 */
	public static <T> LinkedHashSet<T> collectToSet(Iterator<T> iterator, Predicate<T> predicate) {
		return (LinkedHashSet<T>) collect(iterator, predicate, new LinkedHashSet<T>());
	}

	/**
	 * Collects elements in a collection satisfying a given predicate into a new
	 * linked hash set and returns it.
	 * 
	 * @param collection
	 *            the collection from which elements are to be copied.
	 * @param predicate
	 *            the test to be applied to determine which elements should be
	 *            copied into the returned set.
	 * @return a Set of the elements in the collection that matched the given
	 *         predicate.
	 * @param <T>
	 *            the type of the elements to collect.
	 */
	public static <T> LinkedHashSet<T> collectToSet(Collection<? extends T> collection, Predicate<T> predicate) {
		return (LinkedHashSet<T>) collect(collection.iterator(), predicate, new LinkedHashSet<T>());
	}

	public static List<Integer> collectIntegers(int start, int end, Predicate<Integer> predicate) {
		return collectToList(IntegerIterator.integerIterator(start, end), predicate);
	}
	
	public static List<Integer> collectIntegers(int end, Predicate<Integer> predicate) {
		return collectIntegers(0, end, predicate);
	}
	
	/**
	 * Returns a list containing the elements in collection that satisfy the predicate.
	 * @param collection
	 *        a collection to filter
	 * @param predicate
	 *        a predicate to filter the given collection with.
	 * @return  a new list containing the elements in the give collection that satisfy the given predicate.
	 * @param <T> the type of elements in the list
	 */
	public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {
		List<T> result = new LinkedList<T>();
		for (T element : collection) {
			if (predicate.apply(element)) {
				result.add(element);
			}
		}
		return result;
	}

	/**
	 * Returns the number of elements in collection satisfying predicate.
	 * @param collection
	 *        a collection to count
	 * @param predicate
	 *        a predicate to indicate which elements should be counted.
	 * @return the number of elements in the given collection satisfying the given predicate.
	 * @param <T> 
	 *        the type of the elements in the collection.
	 */
	public static <T> int count(Collection<T> collection, Predicate<T> predicate) {
		int result = 0;
		for (T element : collection) {
			if (predicate.apply(element)) {
				result++;
			}
		}
		return result;
	}

	/**
	 * Returns the number of elements in an iterator's range satisfying predicate.
	 * @param iterator
	 *        an iterator whose range to count the elements of
	 * @return the number of elements in the given iterator's range.
	 * @param <T> 
	 *        the type of the elements in the iterator's range.
	 */
	public static <T> int count(Iterator<T> iterator) {
		int result = 0;
		while (iterator.hasNext()) {
			iterator.next();
			result++;
		}
		return result;
	}

	public static Number numberInJustNeededType(double number) {
		if (Math.floor(number) == number) {
			return Integer.valueOf((int) number);
		}
		return Double.valueOf(number);
	}

	public static <T extends Number> Number sum(Iterator<T> numbersIt) {
		double sum = 0;
		while (numbersIt.hasNext()) {
			sum += numbersIt.next().doubleValue();
		}
		return numberInJustNeededType(sum);
	}

	public static <T extends Number> Number sum(Collection<T> numbers) {
		return sum(numbers.iterator());
	}

	/**
	 * Returns the sum of the elements in an array of integers.
	 * @param array an array of ints
	 * @return the sum of the elements in an array of integers.
	 */
	public static int sum(int[] array) {
		int result = 0;
		for (int element : array) {
			result += element;
		}
		return result;
	}

	public static Rational sumArbitraryPrecision(Iterator<Number> numbersIt) {
		Rational sum = Rational.ZERO;
		while (numbersIt.hasNext()) {
			Rational number = (Rational) numbersIt.next();
			sum = sum.add(number);
		}
		return sum;
	}

	public static Rational sumArbitraryPrecision(Collection<Number> numbers) {
		return sumArbitraryPrecision(numbers.iterator());
	}

	/**
	 * Computer the maximum in the range of a {@link Rational} iterator,
	 * throwing an error if the range is empty.
	 * @param numbersIt iterator over numbers
	 * @return the max value
	 */
	public static Rational maxArbitraryPrecision(Iterator<Rational> numbersIt) {
		if (numbersIt.hasNext()) {
			Rational result = numbersIt.next();
			while (numbersIt.hasNext()) {
				Rational number = numbersIt.next();
				if (number.compareTo(result) > 0) {
					result = number;
				}
			}
			return result;
		}
		else {
			throw new Error("Iterator may not have empty range for Util.maxArbitraryPrecision(Iteator<Rational>)");
		}
	}

	public static Rational maxArbitraryPrecision(Collection<Rational> numbers) {
		return maxArbitraryPrecision(numbers.iterator());
	}

	public static Number product(Iterator<? extends Number> numbersIt) {
		double product = 1;
		while (numbersIt.hasNext()) {
			product *= numbersIt.next().doubleValue();
			if (product == 0) {
				break;
			}
		}
		return numberInJustNeededType(product);
	}

	public static Number product(Collection<? extends Number> numbers) {
		return product(numbers.iterator());
	}

	public static Rational productArbitraryPrecision(Iterator<Number> numbersIt) {
		Rational product = Rational.ONE;
		while (numbersIt.hasNext()) {
			Rational number = (Rational) numbersIt.next();
			product = product.multiply(number);
			if (product.equals(Rational.ZERO)) {
				break;
			}
		}
		return product;
	}

	public static Rational productArbitraryPrecision(Collection<Number> numbers) {
		return productArbitraryPrecision(numbers.iterator());
	}

	/**
	 * Returns a Number representing the quotient of a division, or null if
	 * denominator is zero.
	 * 
	 * @param numerator
	 *            the numerator.
	 * @param denominator
	 *            the denominator.
	 * @return a Number representing the quotient of a division, or null if
	 *         denominator is zero.
	 */
	public static Number division(Number numerator, Number denominator) {
		double denominatorValue = denominator.doubleValue();
		if (denominatorValue == 0) {
			return null;
		}
		double numeratorValue = numerator.doubleValue();
		double quotientValue = numeratorValue / denominatorValue;
		return numberInJustNeededType(quotientValue);
	}

	/**
	 * Returns a Number representing the quotient of a division, or null if
	 * denominator is zero, with arbitrary precision when possible.
	 * 
	 * @param numerator
	 *            the numerator.
	 * @param denominator
	 *            the denominator.
	 * @return a Number representing the quotient of a division, or null if
	 *         denominator is zero, with arbitrary precision when possible.
	 */
	public static Rational divisionWithArbitraryPrecisionIfPossible(
			Rational numerator, Rational denominator) {
		if (denominator.isZero()) {
			return null;
		}

		// Note: In the case of Rational as opposed to the previously used
		// BigDecimal,
		// this should always be possible.
		Rational quotient = numerator.divide(denominator);
		return quotient;
	}

	/**
	 * Returns the maximum in collection of numbers, or null if collection is
	 * empty.
	 * 
	 * @param numbers
	 *            a collection of numbers a maximum is to be found for.
	 * @return the maximum element in the given collection, or null if
	 *         collection is empty.
	 */
	public static Number max(Collection<? extends Number> numbers) {
		Number result = max(
				numbers,
				(Number n1, Number n2) -> Double.compare(n1.doubleValue(),
						n2.doubleValue()));
		if (result != null) {
			result = numberInJustNeededType(result.doubleValue());
		}
		return result;
	}

	/**
	 * Returns the minimum in collection of numbers, or null if collection is
	 * empty.
	 * 
	 * @param numbers
	 *            a collection of numbers a minimum is to be found for.
	 * @return the minimum element in the given collection, or null if
	 *         collection is empty.
	 */
	public static Number min(Collection<? extends Number> numbers) {
		Number result = min(
				numbers,
				(Number n1, Number n2) -> Double.compare(n1.doubleValue(),
						n2.doubleValue()));
		if (result != null) {
			result = numberInJustNeededType(result.doubleValue());
		}
		return result;
	}

	/**
	 * Returns the maximum element in a collection according to a comparator.
	 * 
	 * @param c
	 *            the collection to find a maximum from.
	 * @param comparator
	 *            the comparator to use to determine the maximum between
	 *            elements.
	 * @return the maximum of the given collection, or null if the collection is
	 *         empty.
	 * @param <T>
	 *            the type of the elements in the collection.
	 */
	public static <T> T max(Collection<? extends T> c, Comparator<T> comparator) {
		T result = null;
		Optional<? extends T> max = c.stream().max(comparator);
		if (max.isPresent()) {
			result = max.get();
		}
		return result;
	}

	/**
	 * Returns the maximum value of given function to an element of collection,
	 * or null if collection is empty.
	 * 
	 * @param c
	 *            the collection to find a maximum from.
	 * @param function
	 *            the function.
	 * @return the maximum value of function on any element of the given collection,
	 *            or null if the collection is empty.
	 * @param <T>
	 *            the type of the elements in the collection.
	 */
	public static <T> Integer max(Collection<? extends T> c, Function<T, Integer> function) {
		Integer result = null;
		for (T element : c) {
			Integer value = function.apply(element);
			if (result == null || value.compareTo(result) > 0) {
				result = value;
			}
		}
		return result;
	}
	
	/**
	 * Returns the minimum element in an iterator's range according to a comparator.
	 * 
	 * @param c
	 *            the iterator to find a minimum from.
	 * @param comparator
	 *            the comparator to use to determine the minimum between
	 *            elements.
	 * @return the minimum of the given iterator, or null if the iterator's range is
	 *         empty.
	 * @param <T>
	 *            the type of the elements in the iterator's range.
	 */
	public static <T> T min(Iterator<? extends T> c, Comparator<T> comparator) {
		T result = null;
		for (T element : in(c)) {
			if (result == null || comparator.compare(result, element) > 0) {
				result = element;
			}
		}
		return result;
	}

	/**
	 * Returns the minimum element in an iterator's range (containing comparables) according to their natural order.
	 * 
	 * @param c
	 *            the iterator to find a minimum from.
	 * @return the minimum of the given iterator, or null if the iterator's range is empty.
	 * @param <T>
	 *            the type of the elements in the iterator's range.
	 */
	public static <T extends Comparable<T>> T min(Iterator<? extends T> c) {
		T result = null;
		for (T element : in(c)) {
			if (result == null || result.compareTo(element) > 0) {
				result = element;
			}
		}
		return result;
	}

	/**
	 * Returns the minimum element in a collection according to a comparator.
	 * 
	 * @param c
	 *            the collection to find a minimum from.
	 * @param comparator
	 *            the comparator to use to determine the minimum between
	 *            elements.
	 * @return the minimum of the given collection, or null if the collection is
	 *         empty.
	 * @param <T>
	 *            the type of the elements in the collection.
	 */
	public static <T> T min(Collection<? extends T> c, Comparator<T> comparator) {
		T result = null;
		Optional<? extends T> min = c.stream().min(comparator);
		if (min.isPresent()) {
			result = min.get();
		}
		return result;
	}

	/**
	 * Returns the minimum value of given function to an element of collection,
	 * or null if collection is empty.
	 * 
	 * @param c
	 *            the collection to find a minimum from.
	 * @param function
	 *            the function.
	 * @return the minimum value of function on any element of the given collection,
	 *            or null if the collection is empty.
	 * @param <T>
	 *            the type of the elements in the collection.
	 */
	public static <T> Integer min(Collection<? extends T> c, Function<T, Integer> function) {
		Integer result = null;
		for (T element : c) {
			Integer value = function.apply(element);
			if (result == null || value.compareTo(result) < 0) {
				result = value;
			}
		}
		return result;
	}
	
	/**
	 * Returns the element in a collection whose value for the given function is minimum,
	 * or null if collection is empty.
	 * 
	 * @param c
	 *            the iterable to find an element with a minimum function value.
	 * @param function
	 *            the function.
	 * @return the element with a minimum function value,
	 *            or null if the collection is empty.
	 * @param <T>
	 *            the type of the elements in the collection.
	 * @param <R>
	 * 			  the type of the function's result, must be comparable.
	 */
	public static <T, R extends Comparable<R>> T argmin(Iterable<? extends T> c, Function<T, R> function) {
		R minimum = null;
		T result = null;
		for (T element : c) {
			R value = function.apply(element);
			if (minimum == null) {
				minimum = value;
				result = element;
			}
			else {
				if (value.compareTo(minimum) < 0) {
					minimum = value;
					result = element;
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns the element in a collection whose value for the given function is maximum,
	 * or null if collection is empty.
	 * 
	 * @param c
	 *            the collection to find an element with a maximum function value.
	 * @param function
	 *            the function.
	 * @return the element with a maximum function value,
	 *            or null if the collection is empty.
	 * @param <T>
	 *            the type of the elements in the collection.
	 * @param <R>
	 * 			  the type of the function's result, must be comparable.
	 */
	public static <T, R  extends Comparable<R>> T argmax(Collection<? extends T> c, Function<T, R> function) {
		R maximum = null;
		T result = null;
		for (T element : c) {
			R value = function.apply(element);
			if (maximum == null) {
				maximum = value;
				result = element;
			}
			else {
				if (value.compareTo(maximum) > 0) {
					maximum = value;
					result = element;
				}
			}
		}
		return result;
	}
	
	/**
	 * Returns the index of the element in a list whose value for the given function is maximum,
	 * or -1 if list is empty.
	 * 
	 * @param list
	 *            the list to find an element with a maximum function value.
	 * @param function
	 *            the function.
	 * @return the element with a maximum function value,
	 *            or -1 if the list is empty.
	 * @param <T>
	 *            the type of the elements in the list.
	 * @param <R>
	 * 			  the type of the function's result, must be comparable.
	 */
	public static <T, R  extends Comparable<R>> int argmaxIndex(List<? extends T> list, Function<T, R> function) {
		return argmaxAndIndex(list, function).second;
	}
	
	
	/**
	 * Returns a pair of the element in a list and its index whose value for the given function is maximum,
	 * or (null, -1) if list is empty.
	 * 
	 * @param list
	 *            the list to find an element with a maximum function value.
	 * @param function
	 *            the function.
	 * @return the pair of the element with a maximum function value and its index,
	 *            or (null, -1) if the list is empty.
	 * @param <T>
	 *            the type of the elements in the list.
	 * @param <R>
	 * 			  the type of the function's result, must be comparable.
	 */
	public static <T, R  extends Comparable<R>> Pair<T, Integer> argmaxAndIndex(List<? extends T> list, Function<T, R> function) {
		R maximum = null;
		T argmax = null;
		int index = -1;
		for (int i = 0; i != list.size(); i++) {
			T element = list.get(i);
			R value = function.apply(element);
			if (maximum == null) {
				maximum = value;
				argmax = element;
				index = i;
			}
			else {
				if (value.compareTo(maximum) > 0) {
					maximum = value;
					argmax = element;
					index = i;
				}
			}
		}
		return Pair.make(argmax, index);
	}

	public static Boolean and(Iterator<Boolean> booleansIt) {
		while (booleansIt.hasNext()) {
			if (!booleansIt.next()) {
				return false;
			}
		}
		return true;
	}

	public static Boolean and(Collection<Boolean> booleans) {
		return !booleans.stream().filter(b -> !b).findFirst().isPresent();
	}

	public static Boolean or(Collection<Boolean> booleans) {
		return booleans.stream().filter(b -> b).findFirst().isPresent();
	}

	/**
	 * Returns a list composed of all elements of given list but the first one.
	 * Throws an exception if list is empty.
	 * 
	 * @param list
	 *            the list all elements except the first are to be returned
	 *            from.
	 * @return the input list excluding the first element.
	 * @param <T>
	 *            the type of the elements in the list.
	 */
	public static <T> List<T> rest(List<T> list) {
		List<T> result = list.subList(1, list.size());
		return result;
	}

	public static <T> T applyTillIdentityDoesNotChange(Function<T, T> function,
			T object) {
		T previousOne;
		do {
			previousOne = object;
			object = function.apply(previousOne);
		} while (object != previousOne);
		return object;
	}

	public static <T> T applyFunctionsFromIteratorUntilFindingDifferentIdentityResultThenReturnIt(
			Iterator<Function<T, T>> functionIterator, T originalArgument) {
		while (functionIterator.hasNext()) {
			Function<T, T> function = functionIterator.next();
			T result = function.apply(originalArgument);
			if (result != originalArgument) {
				return result;
			}
		}
		return originalArgument;
	}

	public static String stringOf(int repetitions, String string) {
		StringBuilder buffer = new StringBuilder();
		while (repetitions-- > 0) {
			buffer.append(string);
		}
		return buffer.toString();
	}

	/**
	 * Indicates whether the elements of two iterators's ranges are equal.
	 * 
	 * @param it1
	 *            the first iterator to test.
	 * @param it2
	 *            the second iterator to test.
	 * @return true if the elements of the two iterator's ranges are equal.
	 */
	public static boolean equals(Iterator it1, Iterator it2) {
		while (it1.hasNext()) {
			if (!it2.hasNext()) {
				return false;
			}
			Object o1 = it1.next();
			Object o2 = it2.next();
			if (!o1.equals(o2)) {
				return false;
			}
		}
		if (it2.hasNext()) {
			return false;
		}
		return true;
	}

	/**
	 * A replacement for {@link Object#equals(Object)} that can deal with
	 * objects being <code>null</code>.
	 * 
	 * @param o1
	 *            the first object to test.
	 * @param o2
	 *            the second object to test.
	 * 
	 * @return true if true if the two object are null or both equal each other,
	 *         false otherwise.
	 */
	public static boolean equals(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		} else if (o2 == null) {
			return false;
		}
		return o1.equals(o2);
	}

	/**
	 * Indicates that neither of two objects are null and the first equals the
	 * second.
	 * 
	 * @param o1
	 *            the first object to test.
	 * @param o2
	 *            the second object to test.
	 * @return true if both object are not null and are equal, false otherwise.
	 */
	public static boolean notNullAndEquals(Object o1, Object o2) {
		if (o1 == null || o2 == null) {
			return false;
		}

		return o1.equals(o2);
	}

	/**
	 * Indicates that two objects are not null and not equal.
	 * 
	 * @param o1
	 *            the first object to test.
	 * @param o2
	 *            the second object to test.
	 * @return true if both object are not null and not equal, false otherwise.
	 */
	public static boolean notNullAndDistinct(Object o1, Object o2) {
		if (o1 == null || o2 == null) {
			return false;
		}

		return !o1.equals(o2);
	}

	/**
	 * Indicates whether all elements in collection satisfy the given predicate.
	 * 
	 * @param collection
	 *            the collection of elements to test.
	 * @param predicate
	 *            the predicate to test the elements within the collection.
	 * @return true if all elements in the collection match the given predicate,
	 *         false otherwise.
	 * @param <E>
	 *            the type of the collections elements.
	 */
	public static <E> boolean forAll(Collection<? extends E> collection, Predicate<E> predicate) {
		boolean result = collection.stream().allMatch(predicate::apply);
		return result;
	}

	/**
	 * Indicates whether all elements in iterator's range satisfy the given predicate.
	 * 
	 * @param iterator
	 *            the iterator over elements to test.
	 * @param predicate
	 *            the predicate to test the elements within the iterator's range.
	 * @return true if all elements in the iterator's range match the given predicate,
	 *         false otherwise.
	 * @param <E>
	 *            the type of the elements iterated over.
	 */
	public static <E> boolean forAll(Iterator<E> iterator, Predicate<E> predicate) {
		while (iterator.hasNext()) {
			E element = iterator.next();
			boolean predicateResult = predicate.apply(element);
			if ( ! predicateResult) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Indicates whether there is an element in iterator's range that satisfies
	 * the given predicate.
	 * 
	 * @param iterator
	 *            the iterator whose range of elements are to be tested.
	 * @param predicate
	 *            the predicate to test the elements within the iterator's
	 *            range.
	 * @return true if any element in the iterator's range match the given
	 *         predicate, false otherwise.
	 * @param <E>
	 *            the type of the iterators range elements.
	 */
	public static <E> boolean thereExists(Iterator<? extends E> iterator, Predicate<E> predicate) {
		while (iterator.hasNext()) {
			if (predicate.apply(iterator.next())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Indicates whether there is an element in collection that satisfies the
	 * given predicate.
	 * 
	 * @param collection
	 *            the collection of elements to test.
	 * @param predicate
	 *            the predicate to test the elements within the collection.
	 * @return true if any element in the collection match the given predicate,
	 *         false otherwise.
	 * @param <E>
	 *            the type of the collections elements.
	 */
	public static <E> boolean thereExists(Collection<? extends E> collection, Predicate<E> predicate) {
		boolean result = false;
		for (E element : collection) {
			boolean elementResult = predicate.apply(element);
			if (elementResult) {
				result = true;
				break;
			}
		}
		return result;
	}

	public static <E> boolean thereExists(E[] array, Predicate<E> predicate) {
		return thereExists(Arrays.asList(array), predicate);
	}

	/**
	 * Indicates whether there is a true element in array.
	 * 
	 * @param array the array of booleans.
	 * @return true if any element in the collection is true, false otherwise.
	 */
	public static boolean thereExists(boolean[] array) {
		for (boolean element : array) {
			if (element) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds all elements of two collections to a new LinkedList.
	 * 
	 * @param c1
	 *            the first collection to add elements from.
	 * @param c2
	 *            the second collection to add elements from.
	 * @return a LinkedList containing the elements from both input collections.
	 * @param <E>
	 *            the type of the collections elements.
	 */

	public static <E> List<E> makeListWithElementsOfTwoCollections(Collection<? extends E> c1, Collection<? extends E> c2) {
		List<E> result = new LinkedList<E>();
		result.addAll(c1);
		result.addAll(c2);
		return result;
	}

	/**
	 * Adds all elements of two collections to a new ArrayList.
	 * 
	 * @param c1
	 *            the first collection to add elements from.
	 * @param c2
	 *            the second collection to add elements from.
	 * @return a LinkedList containing the elements from both input collections.
	 * @param <E>
	 *            the type of the collections elements.
	 */

	public static <E> ArrayList<E> makeArrayListWithElementsOfTwoCollections(Collection<E> c1, Collection<E> c2) {
		ArrayList<E> result = new ArrayList<E>();
		result.addAll(c1);
		result.addAll(c2);
		return result;
	}
	
	/**
	 * Collects all elements in the iterables in a sequence (iterator's range) to a set (eliminating duplicates)
	 * and returns an array list with them.
	 * @param iterator
	 * @return
	 */
	public static <E> ArrayList<E> unionArrayList(Iterator<? extends Iterable<? extends E>> iterator) {
		Set<E> set = union(iterator);
		ArrayList<E> arrayList = new ArrayList<>(set);
		return arrayList;
	}
	
	/**
	 * Collects all elements in the iterables in a sequence (iterable's range) to a set (eliminating duplicates)
	 * and returns an array list with them.
	 * @param iterable
	 * @return
	 */
	public static <E> ArrayList<E> unionArrayList(Iterable<? extends Iterable<? extends E>> iterable) {
		Set<E> set = union(iterable);
		ArrayList<E> arrayList = new ArrayList<>(set);
		return arrayList;
	}
	
	/**
	 * Returns a fresh LinkedHashSet with all elements from iterables in an iterator's range.
	 * @param iteratorOfIterables
	 * @return
	 */
	public static <E> LinkedHashSet<E> union(Iterator<? extends Iterable<? extends E>> iteratorOfIterables) {
		LinkedHashSet<E> result = new LinkedHashSet<>();
		for (Iterable<? extends E> iterable : in(iteratorOfIterables)) {
			addAll(result, iterable);
		}
		return result;
	}
	
	/**
	 * Returns a fresh LinkedHashSet with all elements from iterables in a collection.
	 * @param collectionOfIterables
	 * @return
	 */
	public static <E> LinkedHashSet<E> union(Iterable<? extends Iterable<? extends E>> collectionOfIterables) {
		return union(collectionOfIterables.iterator());
	}
	
	/**
	 * Returns a fresh LinkedHashSet with all elements from iterables in a collection.
	 * @param arrayOfIterables
	 * @return
	 */
	@SafeVarargs
	public static <E> LinkedHashSet<E> union(Iterable<? extends E>... arrayOfIterables) {
		return union(Arrays.asList(arrayOfIterables).iterator());
	}
	
	/**
	 * Computes the union of the iterables computed by a function applied to the elements of a given iterable.
	 * @param iterable
	 * @param function
	 * @return
	 */
	public static <E, T> Set<T> union(Iterable<? extends E> iterable, Function<? super E, Iterable<? extends T>> function) {
		return union(functionIterator(iterable, function));
	}
	
	/**
	 * Returns the union of the results of the application of a function to the elements of an iterable.
	 */
	public static <T, R> LinkedHashSet<R> unionOfResults(
			Iterable<? extends T> iterable, 
			Function<? super T, ? extends Iterable<? extends R>> function) {
		
		return unionOfResults(iterable, function, set());
	}

	/**
	 * Adds all elements in the (Iterable) results of the application of a function to the elements of an iterable to a given Collection.
	 */
	public static <T, R, C extends Collection<R>> C unionOfResults(
			Iterable<? extends T> iterable,
			Function<? super T, ? extends Iterable<? extends R>> function,
			C result) {
		
		for (T t : iterable) {
			addAll(result, function.apply(t));
		}
		return result;
	}

	/**
	 * Adds all elements in a given iterable to a given collection (generalizes {@link Collection#addAll(Collection)} to {@link Iterable}.
	 * @param collection
	 * @param iterable
	 */
	public static <E> void addAll(Collection<? super E> collection, Iterable<? extends E> iterable) {
		for (E e : iterable) {
			collection.add(e);
		}
	}

	/**
	 * Adds all elements of given iterables to a new LinkedList.
	 * 
	 * @param iterables
	 *            the iterables whose elements should be added to the returned
	 *            list.
	 * @return a new Linked List containing all the elements from the given
	 *         iterables.
	 * @param <E>
	 *            the type of the iterables elements.
	 */
	@SafeVarargs
	public static <E> LinkedList<E> addAllToANewList(Iterable<? extends E>... iterables) {
		LinkedList<E> result = new LinkedList<E>();
		for (Iterable<? extends E> c : iterables) {
			addAll(result, c);
		}
		return result;
	}

	/**
	 * Adds all elements of given iterables to a new ArrayList.
	 * 
	 * @param iterables
	 *            the iterables whose elements should be added to the returned
	 *            list.
	 * @return a new ArrayList containing all the elements from the given
	 *         iterables.
	 * @param <E>
	 *            the type of the iterables elements.
	 */
	@SafeVarargs
	public static <E> ArrayList<E> addAllToANewArrayList(Iterable<? extends E>... iterables) {
		ArrayList<E> result = new ArrayList<E>();
		for (Iterable<? extends E> c : iterables) {
			addAll(result, c);
		}
		return result;
	}

	/**
	 * Adds all elements of given iterables to a new LinkedHashSet.
	 * 
	 * @param iterables
	 *            the iterables whose elements should be added to the returned
	 *            list.
	 * @return a new LinkedHashSet containing all the elements from the given
	 *         iterables.
	 * @param <E>
	 *            the type of the iterables elements.
	 */
	@SafeVarargs
	public static <E> LinkedHashSet<E> addAllToANewSet(Iterable<? extends E>... iterables) {
		LinkedHashSet<E> result = new LinkedHashSet<E>();
		for (Iterable<? extends E> c : iterables) {
			addAll(result, c);
		}
		return result;
	}

	public static String times(int level, String string) {
		StringBuilder result = new StringBuilder();
		while (level-- > 0) {
			result.append(string);
		}
		return result.toString();
	}

	/**
	 * Creates PrintStream from file name checking for errors and throwing
	 * appropriate {@link Error}s.
	 * 
	 * @param fileName
	 *            the name of the file a print stream is to be created for.
	 * @return the PrintStream against the given file.
	 */
	public static PrintStream getPrintStream(String fileName) {
		// use buffering
		PrintStream output;
		try {
			output = new PrintStream(fileName);
		} catch (IOException e) {
			throw new Error(e.getMessage());
		}
		return output;
	}

	public static <T> T getFirstOrNull(Iterator<T> i) {
		if (i.hasNext()) {
			return i.next();
		}
		return null;
	}

	public static <T> T getFirstOrNull(Collection<T> c) {
		return getFirstOrNull(c.iterator());
	}

	/**
	 * Returns the last element of a list or null, if empty.
	 *
	 * @param list
	 *            the list whose last element is to be returned.
	 * @return the last element in the list or null if the list is empty.
	 * @param <E>
	 *            the type of the elements in the list.
	 */
	public static <E> E getLast(List<E> list) {
		if (list.isEmpty()) {
			return null;
		}
		return list.get(list.size() - 1);
	}

	public static <T> T getFirst(Iterator<T> i) {
		return i.next();
	}

	public static <T> T getFirst(Collection<T> c) {
		return getFirst(c.iterator());
	}
	
	public static <T> T getOnlyElement(Collection<T> c) {
		assert(c.size() == 1);
		return getFirst(c);
	}
	
	/**
	 * Return all but the first element in an iterator's range, in a newly made
	 * list.
	 * 
	 * @param i
	 *            the iterator to get rest from.
	 * @return all but the first element in the iterator's range, in a newly
	 *         made list.
	 * @param <E>
	 *            the type of the elements in the iterator's range.
	 */
	public static <E> List<E> getRest(Iterator<E> i) {
		if (!i.hasNext()) {
			throw new Error("Util.getRest called on empty iterator");
		}
		i.next();
		List<E> result = listFrom(i);
		return result;
	}

	/**
	 * Return all but the first element in a collection, in iteration order, in
	 * a newly made list.
	 * 
	 * @param c
	 *            the collection to get rest from.
	 * @return all but the first element in the collection, in a newly made
	 *         list.
	 * @param <E>
	 *            the type of the collections elements.
	 */
	public static <E> List<E> getRest(Collection<E> c) {
		return getRest(c.iterator());
	}

	public static <E> E getFirstSatisfyingPredicateOrNull(Iterator<? extends E> i, Predicate<E> p) {
		while (i.hasNext()) {
			E o = i.next();
			if (p.apply(o)) {
				return o;
			}
		}
		return null;
	}

	public static <E> E getFirstSatisfyingPredicateOrNull(Iterable<? extends E> iterable, Predicate<E> p) {
		return getFirstSatisfyingPredicateOrNull(iterable.iterator(), p);
	}
	
	public static <E> E getFirst(
			Collection<? extends E> c, Predicate<E> p) {
		return getFirstSatisfyingPredicateOrNull(c.iterator(), p);
	}

	public static <E> E getFirstSatisfyingPredicateOrNull(E[] array, Predicate<E> p) {
		return getFirst(Arrays.asList(array), p);
	}

	/**
	 * Returns the first result of applying a given function to the elements of
	 * a collection, or <code>null</code> if all such results are
	 * <code>null</code>.
	 * 
	 * @param c
	 *            the collection whose elements are to be mapped by the given
	 *            function.
	 * @param f
	 *            the function to map the given elements with.
	 * @return the first result of applying the given function to the elements
	 *         that is not null, otherwise null.
	 * @param <A>
	 *            the type of the give collections arguments and the type of the
	 *            argument to the given function.
	 * @param <R>
	 *            the result type of applying the given function to an element
	 *            in the given collection.
	 */
	public static <A, R> R getFirstNonNullResultOrNull(Collection<? extends A> c, Function<? super A, R> f) {
		R result = null;
		Optional<R> first = c.stream().map(a -> f.apply(a))
				.filter(r -> r != null).findFirst();
		if (first.isPresent()) {
			result = first.get();
		}
		return result;
	}

	@SafeVarargs
	public static <T> T getFirstNonNullResultOrNull(NullaryFunction<T>... thunks) {
		T result = null;
		for (int i = 0; i != thunks.length; i++) {
			result = thunks[i].apply();
			if (result != null) {
				break;
			}
		}
		return result;
	}

	public static <E> E findFirst(Collection<? extends E> c, Predicate<E> p) {
		return getFirstSatisfyingPredicateOrNull(c.iterator(), p);
	}

	public static <E> E findFirst(Iterator<? extends E> i, Predicate<E> p) {
		return getFirstSatisfyingPredicateOrNull(i, p);
	}

	public static <E> int getIndexOfFirstSatisfyingPredicateOrMinusOne(Collection<? extends E> c, Predicate<E> p) {
		int i = 0;
		for (E e : c) {
			if (p.apply(e)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public static <E> int getIndexOfLastSatisfyingPredicateOrMinusOne(List<? extends E> c, Predicate<E> p) {
		ListIterator<? extends E> it = c.listIterator(c.size());
		while (it.hasPrevious()) {
			int index = it.previousIndex();
			E e = it.previous();
			if (p.apply(e)) {
				return index;
			}
		}
		return -1;
	}

	public static <E, E1 extends E> int getIndexOfFirstSatisfyingPredicateOrMinusOne(E1[] c, Predicate<E> p) {
		int result = getIndexOfFirstSatisfyingPredicateOrMinusOne(Arrays.asList(c), p);
		return result;
	}

	public static <T> Set<T> intersection(Iterable<? extends T> c1, Collection<? extends T> c2) {
		LinkedHashSet<T> result = new LinkedHashSet<T>();
		for (T element : c1) {
			if (c2.contains(element)) {
				result.add(element);
			}
		}
		return result;
	}
	
	/**
	 * Returns the intersection of the iterables in the range of an iterator or null if the iterator's range is empty.
	 */
	public static <T> Set<T> intersection(Iterable<? extends Iterable<? extends T>> iterableOfIterables) {
		return intersection(iterableOfIterables.iterator());
	}

	public static <T> Set<T> intersection(Iterator<? extends Iterable<? extends T>> iteratorOfIterables) {
		Set<T> result;
		if (iteratorOfIterables.hasNext()) {
			result = intersectionGivenIteratorOfIterablesHasAtLeastOneElement(iteratorOfIterables);
		}
		else {
			result = null;
		}
		return result;
	}

	public static <T> Set<T> intersectionGivenIteratorOfIterablesHasAtLeastOneElement(Iterator<? extends Iterable<? extends T>> iteratorOfIterables) {
		var first = iteratorOfIterables.next();
		Set<T> result = setFrom(first);
		while (!result.isEmpty() && iteratorOfIterables.hasNext()) {
			result = intersection(iteratorOfIterables.next(), result);
		}
		return result;
	}

	/**
	 * Indicates whether two collections intersect.
	 *
	 * @param c1
	 *            the first collection to test.
	 * @param c2
	 *            the second collection to test.
	 * @return true if the two collections intersect.
	 * @param <T> an upper bound type on the types of elements of both collections.
	 */
	public static <T> boolean intersect(Collection<? extends T> c1,	Collection<? extends T> c2) {
		boolean result;

		// Optimization: Traverse the smaller list.
		if (c1.size() < c2.size()) {
			result = c1.stream().filter(e -> c2.contains(e)).findAny().isPresent();
		} else {
			result = c2.stream().filter(e -> c1.contains(e)).findAny().isPresent();
		}

		return result;
	}

	/**
	 * Computes the intersection of the iterables computed by a function applied to the elements of a given iterable.
	 * @param iterable
	 * @param function
	 * @return
	 */
	public static <E, T> Set<T> intersection(Iterable<? extends E> iterable, Function<? super E, Iterable<? extends T>> function) {
		return intersection(functionIterator(iterable, function));
	}

	/**
	 * A structure for
	 * {@link Util#selectPair(List, Predicate, Predicate, BinaryPredicate)} and
	 * {@link Util#selectPairInEitherOrder(List, Predicate, Predicate, BinaryPredicate)} results.
	 * 
	 * @param <P> the type of the pairs.
	 */
	public static class SelectPairResult<P> {
		public SelectPairResult(P first, P second, int indexOfFirst,
				int indexOfSecond, P satisfiesFirstPredicate,
				P satisfiesSecondPredicate) {
			super();
			this.first = first;
			this.second = second;
			this.indexOfFirst = indexOfFirst;
			this.indexOfSecond = indexOfSecond;
			this.satisfiesFirstPredicate = satisfiesFirstPredicate;
			this.satisfiesSecondPredicate = satisfiesSecondPredicate;
		}

		/** Element appearing first. */
		public P first;

		/** Element appearing second. */
		public P second;

		/** Index of element appearing first. */
		public int indexOfFirst;

		/** Index of element appearing second. */
		public int indexOfSecond;

		/** Element satisfying first predicate. */
		public P satisfiesFirstPredicate;

		/** Element satisfying second predicate. */
		public P satisfiesSecondPredicate;
	}

	/**
	 * Returns the indices of first pair of elements of a list such that each of
	 * them satisfies a respectively given unary predicate, and them both
	 * satisfy a binary predicate, or null if there is no such pair.
	 * 
	 * @param list
	 *            the elements to test.
	 * @param unaryPredicate1
	 *            the first predicate to test with.
	 * @param unaryPredicate2
	 *            the second predicate to test with.
	 * @param binaryPredicate
	 *            the binary predicate to test with.
	 * @return the indices of first pair of elements of a list such that each of
	 *         them satisfies a respectively given unary predicate, and them
	 *         both satisfy a binary predicate, or null if there is no such
	 *         pair.
	 * @param <T>
	 *            the type of the elements in the list.
	 */
	public static <T> SelectPairResult<T> selectPair(List<? extends T> list,
			Predicate<T> unaryPredicate1, Predicate<T> unaryPredicate2,
			BinaryPredicate<T, T> binaryPredicate) {
		for (int i = 0; i != list.size(); i++) {
			final T o1 = list.get(i);
			if (unaryPredicate1.apply(o1)) {
				for (int j = i + 1; j != list.size(); j++) {
					final T o2 = list.get(j);
					if (unaryPredicate2.apply(o2)
							&& binaryPredicate.apply(o1, o2)) {

						return new SelectPairResult<T>(o1, o2, i, j, o1, o2);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Like {@link #selectPair(List, Predicate, Predicate, BinaryPredicate)} ,
	 * but the pair may be present in either order. The binary predicate, for
	 * consistency, is always applied to <code>(x1, x2)</code> where
	 * <code>x1</code> is the element satisfying the first unary predicate (as
	 * opposed to being the element that appears first), and where
	 * <code>x2</code> is the element satisfying the second unary predicate (as
	 * opposed to being the element that appears second).
	 * 
	 * @param list
	 *            the elements to test.
	 * @param unaryPredicate1
	 *            the first predicate to test with.
	 * @param unaryPredicate2
	 *            the second predicate to test with.
	 * @param binaryPredicate
	 *            the binary predicate to test with.
	 * @return Like
	 *         {@link #selectPair(List, Predicate, Predicate, BinaryPredicate)}
	 *         , but the pair may be present in either order. The binary
	 *         predicate, for consistency, is always applied to
	 *         <code>(x1, x2)</code> where <code>x1</code> is the element
	 *         satisfying the first unary predicate (as opposed to being the
	 *         element that appears first), and where <code>x2</code> is the
	 *         element satisfying the second unary predicate (as opposed to
	 *         being the element that appears second).
	 * @param <T>
	 *            the type of the elements in the list.
	 */
	public static <T> SelectPairResult<T> selectPairInEitherOrder(
			List<? extends T> list, Predicate<T> unaryPredicate1,
			Predicate<T> unaryPredicate2, BinaryPredicate<T, T> binaryPredicate) {

		// straight implementation
		// for (int i = 0; i != list.size(); i++) {
		// final T o1 = list.get(i);
		// boolean o1SatisfiesPredicate1 = unaryPredicate1.evaluate(o1);
		// boolean o1SatisfiesPredicate2 = unaryPredicate2.evaluate(o1);
		//
		// for (int j = i + 1; j != list.size(); j++) {
		// final T o2 = list.get(j);
		// boolean o2SatisfiesPredicate1 = unaryPredicate1.evaluate(o2);
		// boolean o2SatisfiesPredicate2 = unaryPredicate2.evaluate(o2);
		//
		// if (o1SatisfiesPredicate1 && o2SatisfiesPredicate2) {
		// boolean o1AndO2SatisfyBinaryPredicate = binaryPredicate.evaluate(o1,
		// o2);
		// if (o1AndO2SatisfyBinaryPredicate) {
		// return new SelectPairResult<T>(o1, o2, i, j, o1, o2);
		// }
		// }
		//
		// if (o2SatisfiesPredicate1 && o1SatisfiesPredicate2) {
		// boolean o2AndO1SatisfyBinaryPredicate = binaryPredicate.evaluate(o2,
		// o1);
		// if (o2AndO1SatisfyBinaryPredicate) {
		// return new SelectPairResult<T>(o1, o2, i, j, o2, o1);
		// }
		// }
		// }
		// }
		// return null;

		// implementation optimized for minimizing the calls to the predicates.
		for (int i = 0; i != list.size(); i++) {
			final T o1 = list.get(i);
			boolean o1SatisfiesPredicate1 = unaryPredicate1.apply(o1);
			if (o1SatisfiesPredicate1) {
				boolean o1SatisfyingOfPredicate2AlreadyComputed = false;
				boolean o1SatisfiesPredicate2 = false; // initial value is
														// irrelevant (never
														// used). Just making
														// compiler happy.
				int j;
				for (j = i + 1; j != list.size(); j++) {
					final T o2 = list.get(j);
					boolean o2SatisfiesPredicate2 = unaryPredicate2.apply(o2);
					if (o2SatisfiesPredicate2) {
						boolean o1AndO2SatisfyBinaryPredicate = binaryPredicate
								.apply(o1, o2);
						if (o1AndO2SatisfyBinaryPredicate) {
							return new SelectPairResult<T>(o1, o2, i, j, o1, o2);
						}
					} else {
						boolean o2SatisfiesPredicate1 = unaryPredicate1
								.apply(o2);
						if (o2SatisfiesPredicate1) {
							if (!o1SatisfyingOfPredicate2AlreadyComputed) {
								o1SatisfiesPredicate2 = unaryPredicate2
										.apply(o1);
								o1SatisfyingOfPredicate2AlreadyComputed = true;
							}
							if (o1SatisfiesPredicate2) {
								boolean o2AndO1SatisfyBinaryPredicate = binaryPredicate
										.apply(o2, o1);
								if (o2AndO1SatisfyBinaryPredicate) {
									return new SelectPairResult<T>(o1, o2, i,
											j, o2, o1);
								}
							}
						}
					}
				}
			} else {
				boolean o1SatisfiesPredicate2 = unaryPredicate2.apply(o1);
				if (o1SatisfiesPredicate2) {
					for (int j = i + 1; j != list.size(); j++) {
						final T o2 = list.get(j);
						boolean o2SatisfiesPredicate1 = unaryPredicate1
								.apply(o2);
						if (o2SatisfiesPredicate1) {
							boolean o2AndO1SatisfyBinaryPredicate = binaryPredicate
									.apply(o2, o1);
							if (o2AndO1SatisfyBinaryPredicate) {
								return new SelectPairResult<T>(o1, o2, i, j,
										o2, o1);
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns two lists: one containing the elements of a list with indices
	 * from 0 to i - 1, and another containing elements of indices i + 1 to the
	 * last, excluding j.
	 * 
	 * @param list
	 *            the list whose elements are to be sliced into two lists.
	 * @param i
	 *            the index of the element to slice the list into two.
	 * @param j
	 *            the index of an element &gt; i to be excluded from the second
	 *            slice.
	 * @return two lists: one containing the elements of a list with indices
	 *         from 0 to i - 1, and another containing elements of indices i + 1
	 *         to the last, excluding j.
	 * @param <E>
	 *            the type of the elements in the list.
	 */
	public static <E> Pair<List<E>, List<E>> slicesBeforeIAndRestWithoutJ(
			List<E> list, int i, int j) {
		Pair<List<E>, List<E>> result = new Pair<>(new LinkedList<>(),
				new LinkedList<>());
		for (int k = 0; k != i; k++) {
			result.first.add(list.get(k));
		}
		for (int k = i + 1; k != list.size(); k++) {
			if (k != j) {
				result.second.add(list.get(k));
			}
		}
		return result;
	}

	/**
	 * Adds elements contained in c1 but not c2 to a given collection result.
	 * 
	 * @param c1
	 *            the elements to be placed in the result if they are not in the
	 *            second collection.
	 * @param c2
	 *            the elements not be added to the result.
	 * @param result
	 *            the collection to be returned as the result.
	 * @return result with elements contained in c1 that are not in c2 added to
	 *         it.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E, C extends Collection<E>> C setDifference(Collection<? extends E> c1,
			Collection<? extends E> c2, C result) {
		c1.stream().filter(e -> !c2.contains(e)).forEach(result::add);
		return result;
	}

	/**
	 * Adds elements contained in c1 but not c2 to a new linked list and returns
	 * it.
	 * 
	 * @param c1
	 *            the elements to be placed in the result if they are not in the
	 *            second collection.
	 * @param c2
	 *            the elements not be added to the result.
	 * @return a new LinkedList with elements contained in c1 that are not in c2
	 *         added to it.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> List<E> setDifference(Collection<? extends E> c1, Collection<? extends E> c2) {
		return setDifference(c1, c2, new LinkedList<E>());
	}

	/**
	 * Adds elements contained in c1 but not c2 to a new linked list and returns
	 * it.
	 * 
	 * @param c1
	 *            the elements to be placed in the result if they are not in the
	 *            second collection.
	 * @param c2
	 *            the elements not be added to the result.
	 * @return a new LinkedList with elements contained in c1 that are not in c2
	 *         added to it.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> List<E> subtract(Collection<? extends E> c1, Collection<? extends E> c2) {
		return setDifference(c1, c2, new LinkedList<E>());
	}

	public static <E, C extends Collection<E>> C subtract(Collection<? extends E> c1, Collection<? extends E> c2, C result) {
		return setDifference(c1, c2, result);
	}

	public static <E> List<E> subtract(Collection<? extends E> c1, E element) {
		return setDifference(c1, list(element));
	}
	
	public static <E> List<? extends E> subtract(Collection<? extends E> c, Predicate<? super E> predicate) {
		return collectToList(c, e -> ! predicate.apply(e));
	}

	public static String camelCaseToSpacedString(String camel) {
		StringBuilder result = new StringBuilder();
		int i = 0;
		while (i < camel.length()) {
			char c = camel.charAt(i);
			if (Character.isUpperCase(c) && i != 0) {
				result.append(' ');
				int initialUpperCaseLetterIndex = i;
				while (i < camel.length()
						&& Character.isUpperCase(camel.charAt(i))) {

					// if this is upper case but next one is lower case, then
					// this is the first letter of a word,
					// so append space first.
					if (i > initialUpperCaseLetterIndex
							&& i + 1 < camel.length()
							&& Character.isLowerCase(camel.charAt(i + 1))) {
						result.append(' ');
					}
					result.append(Character.toLowerCase(camel.charAt(i)));
					i++;
				}
			} else {
				result.append(c);
				i++;
			}
		}
		return result.toString();
	}

	/**
	 * Gets a collection and returns it back if there are no repeated elements,
	 * or an ArrayList with unique elements.
	 * 
	 * @param c
	 *            the collection to be tested.
	 * @return the given collection if there are no repreated elements, or an
	 *         ArrayList with unique elements.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> Collection<E> removeRepeatedNonDestructively(Collection<E> c) {
		LinkedHashSet<E> s = new LinkedHashSet<E>(c);
		if (s.size() == c.size()) {
			return c;
		}
		return new ArrayList<E>(s);
	}

	public static <T> List<? extends T> removeNonDestructively(List<? extends T> list, int excludedIndex) {
		if (excludedIndex >= list.size()) {
			return list;
		}
		ArrayList<T> newList = new ArrayList<T>();
		int i = 0;
		for (T element : list) {
			if (i != excludedIndex) {
				newList.add(element);
			}
			i++;
		}
		return newList;
	}

	/**
	 * Returns a new linked list containing the elements of collection that do not
	 * satisfy a predicate.
	 * 
	 * @param collection
	 *            the collection of elements to be tested.
	 * @param predicate
	 *            the predicate to be used to test the elements.
	 * @return a new linked list containing the elements of list that do not
	 *         satisfy a predicate.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> LinkedList<? extends E> removeNonDestructively(Collection<? extends E> collection, Predicate<E> predicate) {
		LinkedList<E> result =
				collection.stream()
				.filter(e -> !predicate.apply(e))
				.collect(toLinkedList());

		return result;
	}
	
	/**
	 * Returns a new linked hash set containing the elements of set that do not
	 * satisfy a predicate.
	 * 
	 * @param set
	 *            the set of elements to be tested.
	 * @param predicate
	 *            the predicate to be used to test the elements.
	 * @return a new linked hash set containing the elements of set that do not
	 *         satisfy a predicate.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> LinkedHashSet<? extends E> removeFromSetNonDestructively(Set<? extends E> set, Predicate<E> predicate) {
		LinkedHashSet<E> result =
				set.stream()
				.filter(e -> !predicate.apply(e))
				.collect(toLinkedHashSet(set.size()));

		return result;
	}
	
	/**
	 * Returns a new linked hash set containing the elements of array list that do not
	 * satisfy a predicate.
	 * 
	 * @param arrayList
	 *            the array list of elements to be tested.
	 * @param predicate
	 *            the predicate to be used to test the elements.
	 * @return a new array list containing the elements of set that do not
	 *         satisfy a predicate.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> ArrayList<E> removeFromArrayListNonDestructively(ArrayList<E> arrayList, Predicate<E> predicate) {
		ArrayList<E> result = new ArrayList<E>(arrayList.size());
		for (E element : arrayList) {
			if (!predicate.apply(element)) {
				result.add(element);
			}
		}
		return result;
	}
	
	/**
	 * Returns a new linked list containing the elements of collection that are not equal to
	 * a given one.
	 * 
	 * @param collection
	 *            the collection of elements to be tested.
	 * @param element
	 *            the element to be removed
	 * @return a new linked list containing the elements of list that are not equal to the
	 * 			  given element.
	 * @param <T>
	 *            the type of the elements.
	 */
	public static <T> LinkedList<? extends T> removeNonDestructively(Collection<? extends T> collection, T element) {
		LinkedList<? extends T> result = removeNonDestructively(collection, Equals.make(element));
		return result;
	}

	public static <T> List<T> makeAListByReplacingFirstOccurrenceOfElementInAListWithAnotherList(
			List<T> list, T element, List<T> anotherList) {
		ArrayList<T> result = new ArrayList<T>();
		boolean replacementAlreadyHappened = false;
		for (T someElement : list) {
			if (!replacementAlreadyHappened && someElement.equals(element)) {
				result.addAll(anotherList);
				replacementAlreadyHappened = true;
			} else {
				result.add(someElement);
			}
		}
		return result;
	}

	/**
	 * Returns list of results of application of a function to pairs of elements
	 * with same indices on two given lists. This is based on a Haskell function
	 * of same name.
	 * 
	 * @param function
	 *            the function to be applied to the paired elements from each
	 *            list.
	 * @param list1
	 *            a list of the first arguments to be zipped.
	 * @param list2
	 *            a list of the second arguments to be zipped.
	 * @return a list of results of application of given function to pairs of
	 *         elements with same indices in two given lists.
	 * @param <A1>
	 *            the type of the elements of list 1.
	 * @param <A2>
	 *            the type of the elements of list 2.
	 * @param <R>
	 *            the type of the result of applying the given function to the
	 *            given pair of arguments.
	 */
	public static <A1, A2, R> List<R> zipWith(
			BinaryFunction<A1, A2, R> function, List<A1> list1, List<A2> list2) {
		List<R> result = new LinkedList<R>();
		Iterator<A1> i1 = list1.iterator();
		Iterator<A2> i2 = list2.iterator();
		while (i1.hasNext()) {
			A1 t1 = i1.next();
			A2 t2 = i2.next();
			R application = function.apply(t1, t2);
			result.add(application);
		}
		return result;
	}

	/**
	 * Returns list of results of application of a function to triples of
	 * elements with same indices on three given lists. This is based on a
	 * Haskell function of same name.
	 * 
	 * @param function
	 *            the function to be applied to the paired elements from each
	 *            list.
	 * @param list1
	 *            a list of the first arguments to be zipped.
	 * @param list2
	 *            a list of the second arguments to be zipped.
	 * @param list3
	 *            a list of the third arguments to be zipped.
	 * @return a list of results of application of given function to triples of
	 *         elements with same indices in three given lists.
	 * @param <A1>
	 *            the type of the elements of list 1.
	 * @param <A2>
	 *            the type of the elements of list 2.
	 * @param <A3>
	 *            the type of the elements of list 3.
	 * @param <R>
	 *            the type of the result of applying the given function to the
	 *            given triple of arguments.
	 */
	public static <A1, A2, A3, R> List<R> zip3With(
			TernaryFunction<A1, A2, A3, R> function, List<A1> list1,
			List<A2> list2, List<A3> list3) {
		List<R> result = new LinkedList<R>();
		Iterator<A1> i1 = list1.iterator();
		Iterator<A2> i2 = list2.iterator();
		Iterator<A3> i3 = list3.iterator();
		while (i1.hasNext() && i2.hasNext() && i3.hasNext()) {
			A1 f1 = i1.next();
			A2 f2 = i2.next();
			A3 f3 = i3.next();
			R application = function.apply(f1, f2, f3);
			result.add(application);
		}
		return result;
	}

	/**
	 * Returns same list if none of its elements gets evaluated to a distinct
	 * object by a replacement function, or a new list equal to the original one
	 * but for having elements replaced by their replacements as provided by
	 * same replacement function.
	 * 
	 * @param list
	 *            the elements to be mapped.
	 * @param replacementFunction
	 *            the element replacement function.
	 * @return the same list if none of its elements gets evaluated to a
	 *         distinct object by a replacement function, or a new list equal to
	 *         the original one but for having elements replaced by their
	 *         replacements as provided by same replacement function.
	 * @param <E>
	 *            the type of the elements in the list.
	 */
	public static <E> List<E> replaceElementsNonDestructively(List<E> list,
			Function<E, E> replacementFunction) {
		List<E> replacementList = null;

		ListIterator<E> it = list.listIterator();
		while (it.hasNext()) {
			E element = it.next();
			E replacement = replacementFunction.apply(element);
			if (replacement != element) {
				replacementList = new ArrayList<E>(list);
				replacementList.set(it.previousIndex(), replacement);
				break;
			}
		}

		if (replacementList == null) {
			return list;
		}

		it = replacementList.listIterator(it.nextIndex());
		while (it.hasNext()) {
			E element = it.next();
			E replacement = replacementFunction.apply(element);
			if (replacement != element) {
				it.set(replacement);
			}
		}

		return replacementList;
	}

	/**
	 * Evaluates replacement function on i-th element of given list
	 * and returns a new list with -ith element replaced by the result,
	 * if the result is different from the original element,
	 * or the same list if the result is equal to the original element.
	 * 
	 * @param list
	 *            the list.
	 * @param i the index of the element to be possibly replaced
	 * @param replacementFunction
	 *            the element replacement function.
	 * @return the same list if the i-th element gets evaluated to itself,
	 *         or a new (array) list with copied elements but for the i-th,
	 *         which gets replaced by the evaluation result.
	 * @param <E>
	 *            the type of the elements in the list.
	 */
	public static <E> List<E> replaceElementNonDestructively(
			List<E> list, int i, Function<E, E> replacementFunction) {
		E element = list.get(i);
		E evaluationResult = replacementFunction.apply(element);
		List<E> result;
		if (evaluationResult.equals(element)) {
			result = list;
		}
		else {
			result = new ArrayList<E>(list);
			result.set(i, evaluationResult);
		}
		return result;
	}
	
	public static <T1, T2> boolean isPairWiseTrue(BinaryPredicate<T1, T2> p,
			Collection<T1> c1, Iterator<T2> it2) {
		return isPairWiseTrue(p, c1.iterator(), it2);
	}

	public static <T1, T2> boolean isPairWiseTrue(BinaryPredicate<T1, T2> p,
			Iterator<T1> it1, Collection<T2> c2) {
		return isPairWiseTrue(p, it1, c2.iterator());
	}

	public static <T1, T2> boolean isPairWiseTrue(BinaryPredicate<T1, T2> p,
			Iterator<T1> it1, Iterator<T2> it2) {

		while (it1.hasNext() && it2.hasNext()) {
			T1 t1 = it1.next();
			T2 t2 = it2.next();
			if (!p.apply(t1, t2)) {
				return false;
			}
		}

		if (it1.hasNext() != it2.hasNext()) {
			return false;
		}

		return true;
	}

	public static <T1, T2> boolean isPairWiseTrue(BinaryPredicate<T1, T2> p,
			Collection<T1> c1, Collection<T2> c2) {

		if (c1.size() != c2.size()) {
			return false;
		}

		Iterator<T1> it1 = c1.iterator();
		Iterator<T2> it2 = c2.iterator();
		while (it1.hasNext()) {
			T1 t1 = it1.next();
			T2 t2 = it2.next();
			if (!p.apply(t1, t2)) {
				return false;
			}
		}
		return true;
	}

	public static <T> List<T> setNonDestructively(List<T> list, int index,
			T newElement) {
		List<T> result = new LinkedList<T>(list);
		result.set(index, newElement);
		return result;
	}

	/**
	 * Returns a given iterator after iterating over its first element.
	 * 
	 * @param iterator
	 *            the iterator to iterate over its first element.
	 * @return the iterator after it has been moved past its first element.
	 *         Throws error if the iterator's range is empty.
	 * @param <E>
	 *            the type of the elements in the iterators range.
	 */
	public static <E> Iterator<E> removeFirst(Iterator<E> iterator) {
		if (iterator.hasNext()) {
			iterator.next();
			return iterator;
		}
		Util.fatalError("Iterator without elements received by Util.removeFirst(Iterator)");
		return null;
	}

	public static <T> List<T> getAllButFirst(List<T> list) {
		Iterator<T> iterator = list.iterator();
		iterator.next();
		LinkedList<T> result = new LinkedList<T>();
		addAll(result, iterator);
		return result;
	}

	/**
	 * Compares two integers, taking into account that -1 means "infinite",
	 * using the same return value convention as {@link Comparator}.
	 * 
	 * @param limit1
	 *            the first integer to compare.
	 * @param limit2
	 *            the second integer to compare.
	 * @return the comparison between the two given integers, taking into
	 *         account that -1 means "infinite", using the same return value
	 *         convention as {@link Comparator}.
	 * 
	 */
	public static int compareIntegersWithMinusOneMeaningInfinite(int limit1,
			int limit2) {
		if (limit1 == -1) {
			if (limit2 == -1) {
				return 0;
			} else {
				return +1;
			}
		} else {
			if (limit2 == -1) {
				return -1;
			} else {
				return limit1 - limit2;
			}
		}
	}

	/**
	 * Indicates whether an array contains a given element.
	 * 
	 * @param array
	 *            the array to be tested.
	 * @param element
	 *            the element to test if it is in the given array.
	 * @return true if the given element is in the given array.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> boolean contains(E[] array, E element) {
		boolean result = Arrays.asList(array).contains(element);
		return result;
	}

	/**
	 * Indicates whether an iterator's range contains a given element.
	 * 
	 * @param iterator
	 *            the iterator whose range is to be tested.
	 * @param element
	 *            the element to test if it is in the given iterators range.
	 * @return true if the given element is in the given iterators range.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> boolean contains(Iterator<? extends E> iterator, E element) {
		while (iterator.hasNext()) {
			if (iterator.next().equals(element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Indicates whether an element appears in a list from a given position.
	 * 
	 * @param list
	 *            the list whose elements are to be tested.
	 * @param i
	 *            the starting position in the list to start testing from.
	 * @param element
	 *            the element to test if it is in the given sublist.
	 * @return true if the element appears in the list from a given position,
	 *         false otherwise.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> boolean listFromIContains(List<E> list, int i, E element) {
		ListIterator<E> iterator = list.listIterator(i);
		boolean result = contains(iterator, element);
		return result;
	}

	/**
	 * Indicates whether an element appears in a list in a position up to, but
	 * different from, i.
	 * 
	 * @param list
	 *            the list whose elements are to be tested.
	 * @param i
	 *            the index in the list that are to be tested to.
	 * @param element
	 *            the element to test if its is in the given sublist.
	 * @return true if the element appears in the list is a position up to, but
	 *         different from, i.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> boolean listUpToIExclusiveContains(ArrayList<E> list,
			int i, E element) {
		Iterator<E> iterator = list.iterator();
		for (int j = 0; iterator.hasNext() && j != i; j++) {
			E element2 = iterator.next();
			if (element2.equals(element)) {
				return true;
			}
		}
		return false;
	}

	public static <T> void removeAll(Set<T> c, Predicate<T> p) {
		List<T> toBeRemoved = collectToList(c, p);
		c.removeAll(toBeRemoved);
	}

	public static <K, V> void removeAll(Map<K, V> map, Collection<K> keysToRemove) {
		for (K key : keysToRemove) {
			map.remove(key);
		}
	}

	public static boolean lessThan(Rational r1, Rational r2) {
		int comparison = r1.compareTo(r2);
		return comparison == -1;
	}

	public static boolean lessThanOrEqualTo(Rational r1, Rational r2) {
		int comparison = r1.compareTo(r2);
		return comparison == -1 || comparison == 0;
	}

	public static boolean greaterThan(Rational r1, Rational r2) {
		int comparison = r1.compareTo(r2);
		return comparison == 1;
	}

	public static boolean equalValues(Rational r1, Rational r2) {
		int comparison = r1.compareTo(r2);
		return comparison == 0;
	}

	public static boolean greaterThanOrEqualTo(Rational r1, Rational r2) {
		int comparison = r1.compareTo(r2);
		return comparison == 0 || comparison == 1;
	}

	public static boolean equals(Rational rational, int integer) {
		return rational.compareTo(integer) == 0;
	}

	/**
	 * Removes the elements that satisfy a predicate from a list.
	 * 
	 * @param list
	 *            the list of elements to be tested.
	 * @param predicate
	 *            the predicate to test the elements on the list with.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> void removeElementsSatisfying(List<? extends E> list,
			Predicate<E> predicate) {
		ListIterator<? extends E> iterator = list.listIterator();
		while (iterator.hasNext()) {
			E element = iterator.next();
			if (predicate.apply(element)) {
				iterator.remove();
			}
		}
	}

	/**
	 * Removes the elements that satisfy a predicate from a set -- works for
	 * sets not containing null elements only.
	 * 
	 * @param set
	 *            the set of elements to be tested.
	 * @param predicate
	 *            the predicate to test the elements on the set with.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> void removeElementsSatisfying(Set<? extends E> set,
			Predicate<E> predicate) {
		boolean removed;
		do {
			removed = false;
			E element = getFirst(set, predicate);
			if (element != null) {
				set.remove(element);
				removed = true;
			}
		} while (removed);
	}

	public static <T1, T2> Pair<T1, T2> pair(T1 first, T2 second) {
		return new Pair<T1, T2>(first, second);
	}

	/**
	 * Returns an object's toString() result, or string "null" if object is
	 * null.
	 * 
	 * @param object
	 *            the object to be toString'd.
	 * @return the object's toString() result, or string "null" if the object is
	 *         null.
	 */
	public static String toStringOrNull(Object object) {
		if (object == null) {
			return "null";
		}
		return object.toString();
	}

	/**
	 * Returns +1 if given int is greater than 0, 0 if it is 0, and -1 if it is
	 * less than 0.
	 * 
	 * @param integer
	 *            the integer to be tested.
	 * @return +1 if given int is greater than 0, 0 if it is 0, and -1 if it is
	 *         less than 0.
	 */
	public static int signal(int integer) {
		if (integer == 0) {
			return 0;
		} else if (integer > 0) {
			return +1;
		}
		return -1;
	}

	/**
	 * Creates a map from a list of keys and a list of values.
	 * 
	 * @param keys
	 *            a list of keys to be contained in the returned map.
	 * @param values
	 *            a list of values to be contained in the returned map.
	 * @return a map from the given lists of keys and values.
	 * @param <K>
	 *            the type of the keys.
	 * @param <V>
	 *            the type of the values.
	 */
	public static <K, V> Map<K, V> mapFromListOfKeysAndListOfValues(
			List<K> keys, List<V> values) {
		if (keys.size() != values.size()) {
			throw new Error(
					"mapFromListOfKeysAndListOfValues requires two lists of same size but got "
							+ keys + " with " + keys.size() + " elements and "
							+ values + " with " + values.size() + " elements.");
		}

		LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();
		Iterator<K> keysIterator = keys.iterator();
		Iterator<V> valuesIterator = values.iterator();
		while (keysIterator.hasNext()) {
			K key = keysIterator.next();
			V value = valuesIterator.next();
			result.put(key, value);
		}
		return result;
	}

	/**
	 * Given map1 and map2, returns a new map such that map(K) = map2(map1(K))
	 * 
	 * @param map1
	 *            the first map to compose.
	 * @param map2
	 *            the second map to compose.
	 * @return a new map such that map(K) = map2(map1(K))
	 * @param <K>
	 *            the type of map1's keys.
	 * @param <V1>
	 *            the type of map1's values and map2's keys. *
	 * @param <V2>
	 *            the type of map2's values.
	 */
	public static <K, V1, V2> Map<K, V2> composeMaps(Map<K, V1> map1,
			Map<V1, V2> map2) {
		Map<K, V2> result = new LinkedHashMap<K, V2>();
		for (Map.Entry<K, V1> entry : map1.entrySet()) {
			K key = entry.getKey();
			V1 value1 = entry.getValue();
			V2 value2 = map2.get(value1);
			result.put(key, value2);
		}
		return result;
	}

	/**
	 * Wait for some time, throwing an error in case of an exception.
	 * 
	 * @param time
	 *            the time in milliseconds to wait.
	 */
	public static void waitOrThrowError(long time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
			throw new Error("Unexpected exception:", e);
		}
	}

	/**
	 * Returns the first object in an array that is an instance of a given
	 * class.
	 * 
	 * @param clazz
	 *            the class of the object to be found.
	 * @param args
	 *            the elements to be tested if they are of the given class.
	 * @return the first object in the given args array that is an instance of
	 *         the given class.
	 */
	public static Object getObjectOfClass(Class clazz, Object[] args) {
		for (Object object : args) {
			if (clazz.isInstance(object)) {
				return object;
			}
		}
		return null;
	}

	/**
	 * Incrementally calculates component-wise averages, given previously
	 * calculated averages (out of n numbers) and a list of new numbers. The
	 * average list is filled with the appropriate number of zeros if it is
	 * empty. The result is stored in-place, destroying the previous average
	 * list.
	 * 
	 * @param average
	 *            previously calculated averages.
	 * @param n
	 *            averages out of 'n' numbers.
	 * @param newItems
	 *            new numbers.
	 * @return an incrementally calculated component-wise average.
	 */
	static public List incrementalComputationOfComponentWiseAverage(
			List<Number> average, int n, List newItems) {
		if (average == null) {
			fatalError("Util.incrementalComputationOfComponentWiseAverage must receive a non-null List");
		}

		if (average.size() == 0) {
			for (int i = 0; i != newItems.size(); i++) {
				average.add(Double.valueOf(0));
			}
		}

		for (int i = 0; i != newItems.size(); i++) {
			double currentAverage = ((Double) average.get(i)).doubleValue();
			double newItem = ((Double) newItems.get(i)).doubleValue();
			double newAverage = (currentAverage * n + newItem) / (n + 1);
			average.set(i, Double.valueOf(newAverage));
		}

		return average;
	}

	/**
	 * A more general version of incrementalComputationOfComponentWiseAverage(List, int,
	 * List) that operates on lists of lists of arbitrary depth,
	 * including depth 0, that is, on {@link Number}s. It is in-place and
	 * returns <code>average</code> if given objects are lists, or returns a new
	 * Number otherwise.
	 * 
	 * @param average
	 *            previously calculated averages.
	 * @param n
	 *            averages out of 'n' numbers.
	 * @param newItems
	 *            new numbers.
	 * @return an incrementally calculated component-wise average.
	 */
	@SuppressWarnings("unchecked")
	public static Object incrementalComponentWiseAverageArbitraryDepth(
			Object average, int n, Object newItems) {
		if (average instanceof Number) {
			return (((Number) average).doubleValue() * n + ((Number) newItems)
					.doubleValue()) / (n + 1);
		}
		ListIterator averageIterator = ((List<Number>) average).listIterator();
		ListIterator newItemsIt = ((List) newItems).listIterator();
		while (averageIterator.hasNext()) {
			Object averageElement = averageIterator.next();
			Object newItemsElement = newItemsIt.next();
			Object newAverageElement = incrementalComponentWiseAverageArbitraryDepth(
					averageElement, n, newItemsElement);
			if (newAverageElement != averageElement) {
				averageIterator.set(newAverageElement);
			}
		}
		return average;
	}

	/**
	 * Given an array <code>a</code>, returns a map from each string in it to
	 * the immediately following object. More precisely, returns a map mapping
	 * each String <code>s</code> in position <code>i</code> of <code>a</code>
	 * to the object in position <code>i+1</code> of <code>a</code>, ignoring
	 * the remaining elements.
	 * 
	 * @param arguments
	 *            the arguments to be processed.
	 * @return a map from each string in the given arguments array to the
	 *         immediately following object.
	 */
	public static Map<String, Object> getMapWithStringKeys(Object[] arguments) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < arguments.length; i++) {
			Object argument = arguments[i];
			if (argument instanceof String) {
				String variable = (String) argument;
				Object value = arguments[++i];
				map.put(variable, value);
			}
		}
		return map;
	}

	/**
	 * Returns a function that returns a new iterator to the given collection
	 * each time is it invoked.
	 * 
	 * @param c
	 *            the collection the returned function is to create an iterator
	 *            over whenever it is applied.
	 * @return a function that returns a new iterator to the given collection
	 *         each time it is invoked.
	 * @param <E> the type of the elements.
	 */
	static public <E> NullaryFunction<Iterator<E>> getIteratorNullaryFunction(
			final Collection<E> c) {
		return new NullaryFunction<Iterator<E>>() {
			@Override
			public Iterator<E> apply() {
				return c.iterator();
			}
		};
	}

	/**
	 * Takes a list of lists, a dimension index (0 for rows, 1 for columns) and
	 * an index (either row or column index), and returns the corresponding
	 * slice (data[index,*] if dimension is 0, or data[*,index] if dimension is
	 * 1).
	 * 
	 * @param data
	 *            a list of lists
	 * @param dimension
	 *            a dimension index (0 for rows, 1 for columns)
	 * @param index
	 *            an row or column index.
	 * @return the corresponding slice
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> List<E> matrixSlice(List<List<E>> data, int dimension,
			int index) {
		if (dimension == 0) {
			return data.get(index);
		}
		List<E> result = new LinkedList<E>();
		for (Iterator<List<E>> rowIt = data.iterator(); rowIt.hasNext();) {
			List<E> row = rowIt.next();
			result.add(row.get(index));
		}
		return result;
	}

	public static String[] makeArrayFilledOutWith(String element, int length) {
		String[] result = new String[length];
		for (int i = 0; i < result.length; i++) {
			result[i] = element;
		}
		return result;
	}

	public static List<String> getFirstColumnAsList(String[][] stringMatrix) {
		List<String> list = new LinkedList<String>();
		for (int i = 0; i < stringMatrix.length; i++) {
			list.add(stringMatrix[i][0]);
		}
		return list;
	}

	public static String[] getSecondColumnAsArray(String[][] stringMatrix) {
		String[] array = new String[stringMatrix.length];
		for (int i = 0; i < stringMatrix.length; i++) {
			array[i] = stringMatrix[i][1];
		}
		return array;
	}

	/**
	 * Randomly picks an element of given array using a given {@link Random}
	 * number generator.
	 * 
	 * @param random
	 *            the random number generator to use.
	 * @param items
	 *            the items from which an element is to be randomly picked from.
	 * @return the randomly picked element from the given array of items.
	 */
	public static Object randomPick(Random random, Object[] items) {
		int index = random.nextInt(items.length);
		Object result = items[index];
		return result;
	}

	/**
	 * Randomly picks <code>n</code> elements (possibly repeated) of given array
	 * using a given {@link Random} number generator.
	 * 
	 * @param n
	 *            the number of items to randomly pick.
	 * @param random
	 *            the random number generator to use.
	 * @param items
	 *            the items from which an element is to be randomly picked from.
	 * @return a list of elements (possibly repeated) randomly selected from the
	 *         given array of items.
	 */
	public static ArrayList<Object> randomPick(int n, Random random,
			Object[] items) {
		ArrayList<Object> result = new ArrayList<Object>(n);
		for (int i = 0; i != n; i++) {
			result.add(randomPick(random, items));
		}
		return result;
	}

	/**
	 * Randomly picks an element of given list ({@link ArrayList}s will be most
	 * efficient) using a given {@link Random} number generator.
	 * 
	 * @param random
	 *            the random number generator to use.
	 * @param items
	 *            the items from which an element is to be randomly picked from.
	 * @return the randomly picked element from the given list of items.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> E randomPick(Random random, List<E> items) {
		int index = random.nextInt(items.size());
		E result = items.get(index);
		return result;
	}

	/**
	 * Randomly picks <code>n</code> elements (possibly repeated) of given list
	 * ({@link ArrayList}s will be most efficient) using a given {@link Random}
	 * number generator.
	 * 
	 * @param n
	 *            the number of items to randomly pick.
	 * @param random
	 *            the random number generator to use.
	 * @param items
	 *            the items from which an element is to be randomly picked from.
	 * @return a list of elements (possibly repeated) randomly selected from the
	 *         given list of items.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> List<E> randomPick(int n, Random random, List<E> items) {
		List<E> result = new ArrayList<E>(n);
		for (int i = 0; i != n; i++) {
			result.add(randomPick(random, items));
		}
		return result;
	}

	/**
	 * Determines whether all elements in collection are
	 * {@link Object#equals(Object)}, including <code>null</code>.
	 * 
	 * @param collection
	 *            the collection of elements to be tested.
	 * @return true of all elements in the given collection are equal, including
	 *         null.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> boolean allEqual(Collection<E> collection) {
		boolean result = true;
		Iterator<E> iterator = collection.iterator();
		if (iterator.hasNext()) {
			E previous = iterator.next();
			while (result && iterator.hasNext()) {
				E nextOne = iterator.next();
				if (!equals(nextOne, previous)) {
					result = false;
				}
			}
		}

		return result;
	}

	public static <E> boolean allEqual(E[] array) {
		if (array.length == 0) {
			return true;
		}
		E first = array[0];
		for (int i = 1; i != array.length; i++) {
			if (array[i] != first) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean allEqual(double[] array) {
		if (array.length == 0) {
			return true;
		}
		double first = array[0];
		for (int i = 1; i != array.length; i++) {
			if (array[i] != first) {
				return false;
			}
		}
		return true;
	}
	
	public static <T> T pickElementInFirstCollectionButNotSecondAndNotEqualTo(
			Collection<T> collection1, Collection<T> collection2,
			T valueItMustBeDifferentFrom) {
		for (T value1 : collection1) {
			if (!value1.equals(valueItMustBeDifferentFrom)
					&& !collection2.contains(value1)) {
				return value1;
			}
		}
		return null;
	}

	public static <T> Set<T> copySetWithoutThisElement(
			Collection<T> collection, T excludedElement) {
		Set<T> result;
		result = new LinkedHashSet<T>(collection);
		result.remove(excludedElement);
		return result;
	}

	/**
	 * Pushes all elements of a collection into a stack and returns the size of
	 * this collection.
	 * 
	 * @param stack
	 *            the stack to have elements pushed onto.
	 * @param toBePushed
	 *            the elements to be pushed onto the given stack.
	 * @return the number of elements pushed onto the stack.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> int pushAll(Stack<E> stack, Collection<E> toBePushed) {
		for (E element : toBePushed) {
			stack.push(element);
		}
		return toBePushed.size();
	}

	/**
	 * Pops n elements from stack.
	 * 
	 * @param stack
	 *            the stack to be popped.
	 * @param n
	 *            the number of elements to be popped from the stack.
	 * @param <E>
	 *            the type of the elements.
	 */
	public static <E> void popAll(Stack<E> stack, int n) {
		for (int i = 0; i != n; i++) {
			stack.pop();
		}
	}

	/**
	 * Receives a collection of keys and returns a map from them to their
	 * respective results from a given function.
	 * 
	 * @param keys
	 *            the keys to be used as arguments to given function.
	 * @param function
	 *            the function to apply to the given keys to generate
	 *            corresponding values.
	 * @return a Map of the given keys with values generated from applying the
	 *         given function to the keys.
	 * @param <K>
	 *            the type of the keys.
	 * @param <V>
	 *            the type of the values.
	 */
	public static <K, V> Map<K, V> getFunctionMapForGivenKeys(
			Collection<K> keys, Function<K, V> function) {
		Map<K, V> result = new LinkedHashMap<K, V>();
		for (K key : keys) {
			result.put(key, function.apply(key));
		}
		return result;
	}

	/**
	 * @return a {@link Collector} to a {@link LinkedList}.
	 * @param <E>
	 *            the type of the elements the constructed linked list should
	 *            contain.
	 */
	public static <E> Collector<E, ?, LinkedList<E>> toLinkedList() {
		return Collectors.toCollection(() -> new LinkedList<E>());
	}

	/**
	 * @return a {@link Collector} to a {@link LinkedHashSet}.
	 * @param <E>
	 *            the type of the elements the constructed linked hash set
	 *            should contain.
	 */
	public static <E> Collector<E, ?, LinkedHashSet<E>> toLinkedHashSet() {
		return Collectors.toCollection(() -> new LinkedHashSet<E>());
	}

	/**
	 * @return a {@link Collector} to a {@link LinkedHashSet} with given initial
	 *         capacity.
	 * @param initialCapacity
	 *            the initial capacity of the instantiated {@link LinkedHashSet}
	 * @param <E>
	 *            the type of the elements the constructed linked hash set
	 *            should contain.
	 */
	public static <E> Collector<E, ?, LinkedHashSet<E>> toLinkedHashSet(int initialCapacity) {
		return Collectors.toCollection(() -> new LinkedHashSet<E>(initialCapacity));
	}

	/**
	 * @return a {@link Collector} to an array list with given initial capacity.
	 * @param initialCapacity
	 *            the initial capacity of the instantiated {@link ArrayList}.
	 * @param <E>
	 *            the type of the elements the constructed array list should
	 *            contain.
	 */
	public static <E> Collector<E, ?, ArrayList<E>> toArrayList(int initialCapacity) {
		return Collectors.toCollection(() -> new ArrayList<E>(initialCapacity));
	}

	/**
	 * Attempts to cast an object to a given class,
	 * and if that does not succeed, throws an error with a message generated from a template
	 * following the format conventions of {@link String#format(String, Object...)},
	 * with formatting arguments being the object being cast, and the expected and actual received classes' simple names,
	 * in this order (for example, <code>"Could not cast %s to %s; it is an instance of %s"</code>).
	 * @param clazz the class to be cast to
	 * @param object the object to be cast
	 * @param messageTemplate the message template from which an error message is generated
	 * @return the cast object
	 * @param <T1> object type
	 * @param <T2> class type
	 */
	public static <T1, T2> T2 castOrThrowError(Class<T2> clazz, T1 object, String messageTemplate) {
		return castOrThrowError(clazz, object, () -> String.format(messageTemplate, object, clazz.getSimpleName(), object.getClass().getSimpleName()));
	}

	/**
	 * A more general version of {@link #castOrThrowError(Class, Object, String)} taking a {@link NullaryFunction} to provide the error string.
	 * @param clazz the class to be cast to
	 * @param object the object to be cast
	 * @param messageMaker a {@link NullaryFunction} generating the error string.
	 * @return the cast object
	 * @param <T1> object type
	 * @param <T2> class type
	 */
	public static <T1, T2> T2 castOrThrowError(Class<T2> clazz, T1 object, NullaryFunction<String> messageMaker ) {
		T2 result;
		try {
			result = clazz.cast(object);
		}
		catch (ClassCastException e) {
			throw new Error(messageMaker.apply());
		}
		return result;
	}

	/**
	 * Given two objects, returns a pair in which either the first element satisfies given predicate, or neither object does.
	 * @param t1
	 *        type 1
	 * @param t2
	 *        type 2
	 * @param predicate
	 *        a predicate
	 * @return a pair in which either the first element satisfies given predicate, or neither object does.
	 * @param <T> the type of the pair's elements.
	 */
	public static <T> Pair<T, T> sortPairMakingFirstOneSatisfyPredicateIfPossible(T t1, T t2, Predicate<T> predicate) {
		if (predicate.apply(t1)) {
			return Pair.make(t1, t2);
		}
		else {
			return Pair.make(t2, t1);
		}
	}

	/**
	 * A utility method throwing an error to indicate that a method used by a default implementation of another method
	 * has not been overridden.
	 * <p>
	 * This is useful in the following situation.
	 * Suppose a class Vehicle (superClassName) provides a default implementation for checkUp() (nameOfMethodWhoseDefaultImplementationUsesThisMethod)
	 * that assumes the Vehicle has tires in the first place,
	 * and uses an abstract method tiresType() (thisMethodsName). Since a lot of vehicles do use tires, such default implementation is very convenient.
	 * However, we do not want to make life harder for people extending Vehicle to represent vehicles without tires (such as a class Boat (thisClassName)),
	 * and forcing them to implement tiresType() with some dummy code would be ugly and distracting.
	 * Besides, the developer may not notice that checkUp() assumes tires, and as a result may not override it (as they should since Boat violates that assumption).
	 * In that case, the dummy code in tiresType() code will be invoked even though it should not.
	 * <p>
	 * The solution is to change tiresType() from abstract to an implemented method simply invoking this Error-throwing safeguard utility.
	 * If the developer overrides checkUp(), as they should in the case of Boat, the fact that tiresType() is no longer an abstract method
	 * will prevent the compiler from insisting on its unnecessary implementation.
	 * If the developer does not override checkUp() (for example, while extending Vehicle to a class Car),
	 * but forgets to override tiresType() (since it is not abstract anymore, the compiler will not complain),
	 * then there will be an Error thrown at run-time by this utility safeguard method.
	 * <p>
	 * In other words, because tiresType() is "abstract" only as a result of using a default, optional implementation,
	 * we make it not technically abstract and required in compile-time, but still "abstract" in a run-time sense of
	 * an implementation being demanded in case it is actually needed.
	 * 
	 * @param thisClassName 
	 * 			the name of the class
	 * @param thisMethodsName 
	 * 			the method name
	 * @param superClassName 
	 * 			the super class name
	 * @param namesOfMethodsWhoseDefaultImplementationUsesThisMethod 
	 * 			names of methods whose default implementation uses this method
	 * @throws Error if one occurs
	 */
	public static void throwSafeguardError(
			String thisClassName,
			String thisMethodsName,
			String superClassName,
			String... namesOfMethodsWhoseDefaultImplementationUsesThisMethod) throws Error {

		String oneOf = namesOfMethodsWhoseDefaultImplementationUsesThisMethod.length > 1? "one of " : "";
		throw new Error(
				thisMethodsName + " is being invoked but has not been overridden by " + thisClassName + ". " +
						"It is probably being invoked by the default implementation of " + oneOf + 
						join(namesOfMethodsWhoseDefaultImplementationUsesThisMethod) + " in " + superClassName +
						", unless some other overriding method invokes it. "
						+ "If the default implementation of " +
						join(namesOfMethodsWhoseDefaultImplementationUsesThisMethod) + " in " + superClassName +
						" is being used, " + thisMethodsName + " should be overridden,"
						+ " or if " + thisMethodsName + " does not make sense for " + thisClassName +
						" (because of some assumption made by the default implementation of " +
						join(namesOfMethodsWhoseDefaultImplementationUsesThisMethod) + " that does not hold in " + thisClassName +
						"), then a new implementation of " + join(namesOfMethodsWhoseDefaultImplementationUsesThisMethod) + 
						" should override the default one;"
						+ " if on the other hand the default implementation of " + join(namesOfMethodsWhoseDefaultImplementationUsesThisMethod) +
						" has been overridden, then its new version, or some other overriding method, is invoking " + thisMethodsName +
						", which (typically) should not happen (because " + thisMethodsName + " conforms to the assumptions made by the default"
						+ " implementation of " + join(namesOfMethodsWhoseDefaultImplementationUsesThisMethod)
						+ " and the overriding of the latter indicates that those assumptions probably do not hold anymore.");
	}

	public static <T> PairOf<List<T>> collectToLists(List<T> collection, Predicate<T> predicate) {
		List<T> positive = new LinkedList<T>();
		List<T> negative = new LinkedList<T>();
		collect(collection, predicate, positive, negative);
		PairOf<List<T>> result = makePairOf(positive, negative);
		return result;
	}

	/**
	 * Non-destructively replaces each element in collection by the elements of a list, if expander produces a non-null one when given the element,
	 * returning the same List instance if no expansion is done.
	 * @param list
	 *        a list
	 * @param expander
	 *        an expander
	 * @return if expansion occurred a new list with all the old elements plus the expansions, otherwise the input list.
	 * @param <T> the type of the list's elements.
	 */
	public static <T> List<T> nonDestructivelyExpandElementsIfFunctionReturnsNonNullCollection(List<T> list, Function<T, Collection<T>> expander) {
		List<T> result = new LinkedList<T>();
		boolean expansionOccurred = false;
		for (T element : list) {
			Collection<T> expansion = expander.apply(element);
			if (expansion == null) {
				result.add(element);
			}
			else {
				result.addAll(expansion);
				expansionOccurred = true;
			}
		}
		
		if ( ! expansionOccurred) {
			result = list;
		}
		
		return result;
	}
	
	public static final String MY_ASSERT_OFF = "com.sri.ai.util.myAssertOff";

//	/**
//	 * A java <code>assert</code> substitute that, unlike the standard one, is on by default and
//	 * can be turned off by setting any value to property {@link #MY_ASSERT_OFF}.
//	 * It throws an {@link AssertionError} with the given message.
//	 * @param test
//	 *        result of the test
//	 * @param message
//	 *        message to display if test failed.
//	 */
//	public static void myAssert(boolean test, String message) {
//		if ( ! test && System.getProperty(MY_ASSERT_OFF) == null) {
//			throw new AssertionError(message);
//		}
//	}
// Commented out because it can have unsuspected performance impact even when testing is turned off.	
	
	/**
	 * Checks for a condition that needs to be true, or
	 * throws an error with a given message.
	 * This is similar to {@link #myAssert(boolean, NullaryFunction)},
	 * but is performed even if property {@link #MY_ASSERT_OFF} is not null,
	 * since it is meant for errors that may occur due to
	 * incorrect input by a user.
	 * It throws an {@link Error} with the given message.
	 * @param test
	 *        result of the test
	 * @param message
	 *        message in thrown {@link Error}.
	 */
	public static void check(boolean test, NullaryFunction<String> message) {
		if ( ! test) {
			throw new Error(message.apply());
		}
	}

	/**
	 * Checks for a condition that needs to be true, or
	 * throws an error with a given message.
	 * This is similar to {@link #myAssert(NullaryFunction, NullaryFunction)},
	 * but is performed even if property {@link #MY_ASSERT_OFF} is not null,
	 * since it is meant for errors that may occur due to
	 * incorrect input by a user.
	 * It throws an {@link Error} with the given message.
	 * @param test
	 *        result of the test
	 * @param message
	 *        message in thrown {@link Error}.
	 */
	public static void check(NullaryFunction<Boolean> test, NullaryFunction<String> message) {
		if (! test.apply()) {
			throw new Error(message.apply());
		}
	}

	/**
	 * A java <code>assert</code> substitute that, unlike the standard one, is on by default and
	 * can be turned off by setting any value to property {@link #MY_ASSERT_OFF}.
	 * It throws an {@link AssertionError} with the given message.
	 * @param test
	 *        result of the test
	 * @param message
	 *        message to display if test failed.
	 */
	public static void myAssert(boolean test, NullaryFunction<String> message) {
		if ( ! test && System.getProperty(MY_ASSERT_OFF) == null) {
			throw new AssertionError(message.apply());
		}
	}

	public static void assertOrThrow(boolean test, NullaryFunction<Error> errorMaker) {
		if ( ! test && System.getProperty(MY_ASSERT_OFF) == null) {
			throw errorMaker.apply();
		}
	}

	/**
	 * A java <code>assert</code> substitute that, unlike the standard one, is on by default and
	 * can be turned off by setting any value to property {@link #MY_ASSERT_OFF}.
	 * It throws an {@link AssertionError} with the given message.
	 * @param test
	 *        result of the test
	 * @param the object whose class is requiring the condition; the class name is added to the beginning of the message, separated by a space
	 * @param message
	 *        message to display if test failed.
	 */
	public static void myAssert(boolean test, Object requirer, NullaryFunction<String> message) {
		if ( ! test && System.getProperty(MY_ASSERT_OFF) == null) {
			throw new AssertionError(requirer.getClass().getSimpleName() + " " + message.apply());
		}
	}

	/**
	 * Similar to {@link #myAssert(boolean, NullaryFunction)}, but takes nullary functions
	 * for the test and the error message, and only executes them if property {@link #MY_ASSERT_OFF} is null,
	 * thus maximizing performance when it <i>isn't</i> null.
	 * 
	 * @param test
	 *        result of the test
	 * @param message
	 *        message to display if test failed.
	 */
	public static void myAssert(NullaryFunction<Boolean> test, NullaryFunction<String> message) {
		if (System.getProperty(MY_ASSERT_OFF) == null && ! test.apply()) {
			throw new AssertionError(message.apply());
		}
	}

//	/**
//	 * Similar to {@link #myAssert(boolean, String)}, but takes a nullary function
//	 * for the test, and only executes it if property {@link #MY_ASSERT_OFF} is null,
//	 * thus maximizing performance when it <code>isn't</code> null.
//	 * 
//	 * @param test
//	 *        result of the test
//	 * @param message
//	 *        message to display if test failed.
//	 */
//	public static void myAssert(NullaryFunction<Boolean> test, String message) {
//		if (System.getProperty(MY_ASSERT_OFF) == null && ! test.apply()) {
//			throw new AssertionError(message);
//		}
//	}
// Commented out because it can have unsuspected performance impact even when testing is turned off.	

	/**
	 * Requires a test to be true, or throws a {@link IllegalArgumentException} with message provided by nullary function.
	 * @param test
	 *        result of the test
	 * @param message
	 *        message to display if test failed.
	 */
	public static void requires(boolean test, NullaryFunction<String> message) {
		if ( ! test) {
			throw new IllegalArgumentException(message.apply());
		}
	}

	/**
	 * Similar to {@link #requires(boolean, NullaryFunction)}, but takes nullary functions
	 * for the test and the error message.
	 * 
	 * @param test
	 *        result of the test
	 * @param message
	 *        message to display if test failed.
	 */
	public static void requires(NullaryFunction<Boolean> test, NullaryFunction<String> message) {
		if (! test.apply()) {
			throw new IllegalArgumentException(message.apply());
		}
	}

	/**
	 * Returns the entryIndex-th entry in a {@link LinkedHashMap},
	 * assuming there is such entry (throws an exception otherwise).
	 * @param map
	 *        a map
	 * @param entryIndex
	 *        an entry index
	 * @return the entry at the given index
	 * @param <K> the map's key type
	 * @param <V> the map's value type       
	 */
	public static <K, V> Map.Entry<K, V> getIthEntry(LinkedHashMap<K, V> map, int entryIndex) {
		Map.Entry<K, V> result;
		Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
		for (int i = 0; i != entryIndex; i++) { iterator.next(); }
		result = iterator.next();
		return result;
	}
	
	/**
	 * Iterates given iterator till it has no more elements or it is past a given element (ignored if null),
	 * detecting the passage through them with the identity comparison (==).
	 * @param iterator
	 *        an iterator
	 * @param element
	 *        an element
	 * @param <T> the type of the elements.
	 */
	public static <T> void iterateTillPastElementByIdentity(Iterator<T> iterator, T element) {
		boolean elementRequirementSatisfied = element == null; 
		while (iterator.hasNext() && (!elementRequirementSatisfied)) {
			T current = iterator.next();
			elementRequirementSatisfied = elementRequirementSatisfied || current == element;
		}
	}
	
	/**
	 * Iterates given iterator till it has no more elements or it is past both given elements (ignored if null),
	 * detecting the passage through them with the identity comparison (==).
	 * @param iterator
	 *        an iterator
	 * @param element1
	 *        an element
	 * @param element2
	 *        an element
	 * @param <T> the type of the elements.
	 */
	public static <T> void iterateTillPastBothElementsByIdentity(Iterator<T> iterator, T element1, T element2) {
		boolean element1RequirementSatisfied = element1 == null; 
		boolean element2RequirementSatisfied = element2 == null;
		while (iterator.hasNext() && (!element1RequirementSatisfied || !element2RequirementSatisfied)) {
			T current = iterator.next();
			element1RequirementSatisfied = element1RequirementSatisfied || current == element1;
			element2RequirementSatisfied = element2RequirementSatisfied || current == element2;
		}
	}
	
	
	/**
	 * Computes the rising factorial <code>start^(n)</code> defined as <code>start(start + 1)...(start + n - 1)</code>.
	 * @param start the first number to be multiplied
	 * @param n the number of numbers to multiply, starting and rising from <code>start</code>
	 * @return the rising factorial <code>start^(n)</code>
	 */
	public static Rational risingFactorial(long start, long n) {
		// more efficient implementation that works with smaller numbers than just multiplying numbers in sequence.
		// there are more efficient ways of doing this, though.
	    long i;
	    if (n == 0) {
	    	return Rational.ONE;
	    }
	    else if (n <= 16) { 
	    	Rational r = new Rational(start);
	        for (i = start + 1; i < start + n; i++) {
	        	r = r.multiply(i);
	        }
	        return r;
	    }
	    i = n / 2;
	    Rational result = risingFactorial(start, i).multiply(risingFactorial(start + i, n - i));
		return result;
	}
	
	/**
	 * Computes the falling factorial <code>(start)_n</code> defined as <code>start(start - 1)...(start - n + 1)</code>.
	 * It does that by reducing the problem to a rising factorial ({@link #risingFactorial(long, long)})
	 * due to the equality <code>(start)_n = (start - n + 1)^(n)</code>.
	 * @param start the first number to be multiplied
	 * @param n the number of numbers to multiply, starting and decreasing from start
	 * @return the falling factorial <code>(start)_n</code>
	 */
	public static Rational fallingFactorial(long start, long n) {
		Rational result = risingFactorial(start - n + 1, n);
		return result;
	}
	
	/**
	 * Computes the factorial of <code>n</code>.
	 * It does that by reducing the problem to a rising factorial ({@link #risingFactorial(long, long)})
	 * due to the equality <code>n! = 1^(n)</code>.
	 * @param n the number whose factorial is returned
	 * @return the factorial of n
	 */
	public static Rational factorial(long n) {
		Rational result = risingFactorial(1, n);
		return result;
	}
	
	/**
	 * Computes the binomial coefficient <code>choose(n, k) = n! / ((n - k)! k!)</code>.
	 * It does that by reducing the problem to a falling factorial ({@link #fallingFactorial(long, long)})
	 * and a factorial ({@link #factorial(long)})
	 * due to the equality <code>n! / ((n - k)! k!) = (n)_k / k!</code>.
	 * @param n the number of elements
	 * @param k the number of elements selected for the set
	 * @return the binomial coefficient
	 */
	public static Rational binomialCoefficient(long n, long k) {
		Rational result = fallingFactorial(n, k).divide(factorial(k));
		return result;
	}
	
	/**
	 * Applies a function to the values of a map, returning a new map,
	 * or the same map instance if the function has always returned the same value object instances.
	 * @param map the map
	 * @param function the function
	 * @param <K> the type of keys
	 * @param <V> the type of values
	 * @return a new map with each value v replaced by function(v), or the same map if f(v) == v for all v.
	 */
	public static <K,V> Map<K,V> mapValuesNonDestructively(Map<K,V> map, Function<V,V> function) {
		Map<K,V> result = new LinkedHashMap<K,V>();
		boolean instanceChange = false;
		for (Map.Entry<K,V> entry : map.entrySet()) {
			V value = entry.getValue();
			V newValue = function.apply(value);
			if (newValue != value) {
				instanceChange = true;
			}
			result.put(entry.getKey(), newValue);
		}
		if (instanceChange) {
			return result;
		}
		else {
			return map;
		}
	}
	
	/**
	 * Creates a new map with same keys and values equal to the application of a function to the original values.
	 * @param map
	 * @param function
	 * @return
	 */
	public static <K, V1, V2> Map<K, V2> mapValues(Map<K, V1> map, Function<V1, V2> function) {
		Map<K, V2> result = map();
		for (Map.Entry<K, V1> entry : map.entrySet()) {
			result.put(entry.getKey(), function.apply(entry.getValue()));
		}
		return result;
	}
	
	public static <K, V> Map<K, V> mapIntoMap(Iterator<? extends K> iterator, Function<K, V> function) {
		Map<K, V> result = map();
		while (iterator.hasNext()) {
			K key = iterator.next();
			result.put(key, function.apply(key));
		}
		return result;
	}

	public static <K, V> Map<K, V> mapIntoMap(Iterable<? extends K> iterable, Function<K, V> function) {
		return mapIntoMap(iterable.iterator(), function);
	}

	public static <K, V> Map<K, V> mapIntegersIntoMap(int firstInclusive, int lastExclusive, Function<Integer, Pair<K, V>> function) {
		Map<K,V> result = map();
		for (int i = firstInclusive; i != lastExclusive; i++) {
			Pair<K, V> entry = function.apply(i);
			result.put(entry.first, entry.second);
		}
		return result;
	}

	/**
	 * Iterates over all elements in iterator's range and picks one with uniform probability.
	 * 
	 * @param iterator the iterator
	 * @param random a random number generator
	 * @param <T> the type of elements
	 * @return a uniformly sampled element from the iterator's range using the random number generator or null if there are no elements in the range.
	 */
	public static <T> T pickUniformly(Iterator<T> iterator, Random random) {
		if (iterator.hasNext()) {
			List<T> list = listFrom(iterator);
			T result = list.get(random.nextInt(list.size()));
			return result;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Iterates over elements in collection and picks one with uniform probability.
	 * 
	 * @param collection the collection
	 * @param random a random number generator
	 * @param <T> the type of elements
	 * @return a uniformly sampled element from the collection using the random number generator or null if collection is empty.
	 */
	public static <T> T pickUniformly(Collection<T> collection, Random random) {
		int i = random.nextInt(collection.size());
		int counter = 0;
		for (T element : collection) {
			if (counter == i) {
				return element;
			}
			counter++;
		}
		return null;
	}
	
	/**
	 * Iterates over a random subset of another iterator's range,
	 * by selecting whether each element in it belong to the subset or not
	 * with 0.5 probability.
	 * @param iterator iterator
	 * @param random random generator
	 * @param <T> the type
	 * @return a random sub-set
	 */
	public static <T> Iterator<T> pickSubSet(Iterator<T> iterator, Random random) {
		return new EZIterator<T>() {
			@Override
			protected T calculateNext() {
				if (iterator.hasNext()) {
					T next = iterator.next();
					return random.nextBoolean()? next : null;
				}
				return null;
			}
		};
	}

	/**
	 * Returns an array list with <code>k</code> elements out of the given list, without replacement.
	 * This means that the returned list contains the elements of <code>k</code> unique positions
	 * in the original list.
	 * If the list contains unique elements, then so will the returned list,
	 * but elements may be repeated if they appear more than once in the original list.
	 * Naturally, <code>k</code> needs to be no greater than the list's size, or an assertion error will be thrown.
	 * @param list list
	 * @param k number of elements to pick
	 * @param random random generator
	 * @param <T> type of elements
	 * @return a list of elements at <code>k</code> unique positions in the given list
	 */
	public static <T> ArrayList<T> pickKElementsWithoutReplacement(ArrayList<T> list, int k, Random random) {
		ArrayList<T> result;
		if (k == list.size()) { // unnecessary, but faster
			result = list;
		}
		else {
			myAssert(() -> k < list.size(), () -> "pickKElementsWithoutReplacement received k = " + k + " greater than list size " + list.size() + ". List is " + list);
			Set<Integer> alreadyPicked = set();
			result = new ArrayList<T>(k);
			for (int i = 0; i != k; i++) {
				int j;
				do {
					j = random.nextInt(list.size());
				} while (alreadyPicked.contains(j));
				result.add(list.get(j));
				alreadyPicked.add(j);
			}
		}
		return result;
	}
	
	/**
	 * Returns an array list with up to <code>k</code> elements out of the given list
	 * satisfying a given predicate, without replacement.
	 * This means that the returned list contains the elements of <code>k</code> unique positions
	 * in the original list.
	 * If the list contains unique elements, then so will the returned list,
	 * but elements may be repeated if they appear more than once in the original list.
	 * The number of picked elements is the maximum of <code>k</code>
	 * and the number of positions in the input list whose elements satisfy the predicate.
	 * @param list the list of elements
	 * @param k number of elements to pick
	 * @param requirement requirement
	 * @param random random generator
	 * @param <T> type of elements
	 * @return a list of up to <code>k</code> elements at unique positions in the given list (picked without replacement).
	 */
	public static <T> ArrayList<T> pickUpToKElementsWithoutReplacement(ArrayList<T> list, int k, Predicate<T> requirement, Random random) {
		ArrayList<T> result;
		Set<Integer> alreadyPicked = set();
		result = new ArrayList<T>(k);
		for (int i = 0; i != k; i++) {
			int j;
			do {
				if (alreadyPicked.size() == list.size()) {
					return result;
				}
				do {
					j = random.nextInt(list.size());
				} while (alreadyPicked.contains(j));
				alreadyPicked.add(j);
			} while (!requirement.apply(list.get(j)));
			result.add(list.get(j));
		}
		return result;
	}
	
	/**
	 * Stores, in a given array list, up to <code>k</code> elements out of the input list 
	 * satisfying a given predicate, without replacement.
	 * The number of elements collected is the maximum of <code>k</code>
	 * and the number of positions in the input list whose elements satisfy the predicate.
	 * @param list the list of elements
	 * @param k number of elements to pick
	 * @param requirement requirement
	 * @param random random generator
	 * @param destination the array list to store the results
	 * @param <T> type of elements
	 */
	public static <T> void pickUpToKElementsWithoutReplacement(ArrayList<T> list, int k, Predicate<T> requirement, Random random, ArrayList<T> destination) {
		Set<Integer> alreadyPicked = set();
		for (int i = 0; i != k; i++) {
			int j;
			do {
				if (alreadyPicked.size() == list.size()) {
					return;
				}
				do {
					j = random.nextInt(list.size());
				} while (alreadyPicked.contains(j));
				alreadyPicked.add(j);
			} while (!requirement.apply(list.get(j)));
			destination.add(list.get(j));
		}
	}
	
	/** Picks an int in <code>[minimum, maximum)</code>. */
	public static int pickInt(int minimum, int maximum, Random random) {
		return minimum + random.nextInt(maximum - minimum);
	}

	/**
	 * Adapts an {@link Iterator} to an {@link Iterable} for use in enhanced for
	 * loops. If {@link Iterable#iterator()} is invoked more than once, an
	 * {@link IllegalStateException} is thrown.
	 * @param iterator the iterator
	 * @param <T> the type of elements
	 * @return the iterable for iterator
	 */
	public static <T> Iterable<T> in(final Iterator<T> iterator) {
		assert iterator != null;
		class SingleUseIterable implements Iterable<T> {
			private boolean used = false;

			@Override
			public Iterator<T> iterator() {
				if (used) {
					throw new IllegalStateException("SingleUseIterable already invoked");
				}
				used = true;
				return iterator;
			}
		}
		return new SingleUseIterable();
	}

	/**
	 * Same as {@link #unionOfMaps(Iterator)} applied to given collection's iterator.
	 * @param maps the collection of maps
	 * @param <K> the type of keys
	 * @param <V> the type of values
	 * @return a new map with all entries from all maps in a collection
	 */
	public static <K,V> Map<K,V> unionOfMaps(Collection<Map<K,V>> maps) {
		return unionOfMaps(maps.iterator());
	}

	/**
	 * Returns a new map with all entries from all maps in an iterator's range.
	 * @param mapsIterator the maps iterator
	 * @param <K> the type of keys
	 * @param <V> the type of values
	 * @return a new map with all entries from all maps in an iterator's range
	 */
	public static <K,V> Map<K,V> unionOfMaps(Iterator<Map<K,V>> mapsIterator) {
		return putAllFromAll(new LinkedHashMap<K,V>(), mapsIterator);
	}

	/**
	 * Puts all entries from all maps in the range of an iterator to a given map.
	 * @param map the map
	 * @param mapsIterator the maps iterator
	 * @param <K> the type of keys
	 * @param <V> the type of values
	 * @return the given map with all entries from all maps in interator's range
	 */
	public static <K,V> Map<K,V> putAllFromAll(Map<K,V> map, Iterator<Map<K,V>> mapsIterator) {
		for (Map<K,V> eachMap : in(mapsIterator)) {
			map.putAll(eachMap);
		}
		return map;
	}

	/**
	 * Returns a map mapping keys to lists containing all the values to which those
	 * keys map in all maps given as arguments.
	 * @param maps the maps whose values we want to union
	 * @param <K> the key type
	 * @param <V> the value type
	 * @return a a map mapping keys to lists containing all the values to which those
	 * keys map in all maps given as arguments.
	 */
	public static <K,V> Map<K, LinkedList<V>> unionOfValues(Collection<Map<? extends K, ? extends V>> maps) {
		Map<K, LinkedList<V>> result = map();
		for (Map<? extends K, ? extends V> map : maps) {
			for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
				LinkedList<V> listOfValues = Util.getOrMakeAndPut(result, entry.getKey(), () -> new LinkedList<V>());
				listOfValues.add(entry.getValue());
			}
		}
		return result;
	}
	
	/**
	 * Accumulates the values for each key using an accumulator function,
	 * returning an empty map if no maps are provided, the first map (the instance itself)
	 * if it is equal to the result, or a new map with the accumulated values otherwise.
	 * @param maps the maps iterator
	 * @param accumulate the accumulating function
	 * @param <K> the type of the Map's key.
	 * @param <V> the type of the Map's value.
	 * @return the map with accumulated values
	 */
	public static <K,V> Map<K,V> accumulateMapValues(Iterable<Map<K,V>> maps, BinaryFunction<V, V, V> accumulate) {
		return accumulateMapValues(maps.iterator(), accumulate);
	}
	
	/**
	 * Accumulates the values for each key using an accumulator function,
	 * returning an empty map if no maps are provided, the first map (the instance itself)
	 * if it is equal to the result, or a new map with the accumulated values otherwise.
	 * @param mapsIterator the maps iterator
	 * @param accumulate the accumulating function
	 * @param <K> the type of the Map's key.
	 * @param <V> the type of the Map's value.
	 * @return the map with accumulated values
	 */
	public static <K,V> Map<K,V> accumulateMapValues(Iterator<Map<K,V>> mapsIterator, BinaryFunction<V, V, V> accumulate) {
		Map<K,V> accumulator;
		if (mapsIterator.hasNext()) {
			Map<K, V> firstMap = mapsIterator.next();
			accumulator = firstMap;
			boolean accumulatorIsFirstMap = true;
			Pair<Map<K,V>,Boolean> accumulatorAndAccumulatorIsFirstMap =
					accumulateAllRemainingMaps(accumulator, accumulatorIsFirstMap, mapsIterator, accumulate);
			accumulator = accumulatorAndAccumulatorIsFirstMap.first;
			accumulatorIsFirstMap = accumulatorAndAccumulatorIsFirstMap.second;
		}		
		else {
			accumulator = new LinkedHashMap<>();
		}
		return accumulator;
	}

	private static 
	<K,V> 
	Pair<Map<K,V>, Boolean> 
	accumulateAllRemainingMaps(
			Map<K,V> accumulator,
			boolean accumulatorIsFirstMap, 
			Iterator<Map<K,V>> mapsIterator, 
			BinaryFunction<V,V,V> accumulate) {

		while (mapsIterator.hasNext()) {
			Map<K,V> nextMap = mapsIterator.next();
			Pair<Map<K,V>,Boolean> accumulatorAndAccumulatorIsFirstMap =
					accumulateAllEntries(accumulator, accumulatorIsFirstMap, nextMap, accumulate);
			accumulator = accumulatorAndAccumulatorIsFirstMap.first;
			accumulatorIsFirstMap = accumulatorAndAccumulatorIsFirstMap.second;
		}
		return Pair.pair(accumulator, accumulatorIsFirstMap);
	}
	
	private static 
	<K,V> 
	Pair<Map<K,V>, Boolean> 
	accumulateAllEntries(
			Map<K,V> accumulator,
			boolean accumulatorIsFirstMap, 
			Map<K,V> nextMap, 
			BinaryFunction<V,V,V> accumulate) {
		
		for (Map.Entry<K,V> entry : nextMap.entrySet()) {
			K key   = entry.getKey();
			V value = entry.getValue();
			V accumulatedValue;
			if (accumulator.containsKey(key)) {
				V previousAccumulatedValue = accumulator.get(key);
				accumulatedValue = accumulate.apply(value, previousAccumulatedValue);
			}
			else {
				accumulatedValue = value;
			}
			if (accumulatorIsFirstMap) {
				// We need to modify the accumulator but we cannot modify original maps,
				// so make a copy
				accumulator = new LinkedHashMap<K,V>(accumulator);
				accumulatorIsFirstMap = false;
			}
			accumulator.put(key, accumulatedValue);
		}
		
		return Pair.pair(accumulator, accumulatorIsFirstMap);
	}
	
	/**
	 * Returns a map mapping keys to lists containing all the values to which those
	 * keys map in all maps given as arguments.
	 * @param maps the iterator over maps whose values we want to union
	 * @param <K> the key type
	 * @param <V> the value type
	 * @return a a map mapping keys to lists containing all the values to which those
	 * keys map in all maps given as arguments.
	 */
	public static <K,V> Map<K, LinkedList<V>> combineMapsIntoNewMapGroupingValuesUnderTheSameKey(Iterator<Map<? extends K, ? extends V>> maps) {
		Map<K, LinkedList<V>> result = map();
		for (Map<? extends K, ? extends V> map : in(maps)) {
			for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
				LinkedList<V> listOfValues = Util.getOrMakeAndPut(result, entry.getKey(), () -> new LinkedList<V>());
				listOfValues.add(entry.getValue());
			}
		}
		return result;
	}
	
	/**
	 * Returns a map containing the union of entries of given maps if shared keys map to the same value,
	 * or null otherwise.
	 */
	@SafeVarargs
	public static <K,V> Map<K, V> unionOfMapsIfCompatibleOrNull(Map<K, V>... maps) {
		Map<K,V> result = map();
		for (Map<K,V> map : maps) {
			result = putAllIfCompatibleOrNull(result, map);
			if (result == null) {
				return null;
			}
		}
		return result;
	}
	
	/**
	 * Adds all entries in second map to first map and return first map if all entries
	 * in second map are new or agree with entries in first map, or null otherwise.
	 * @param result
	 * @param map
	 * @return
	 */
	public static <K,V> Map<K, V> putAllIfCompatibleOrNull(Map<K, V> result, Map<K, V> map) {
		for (Map.Entry<K,V> entry : map.entrySet()) {
			V valueInResult = result.get(entry.getKey());
			if (valueInResult != null) {
				if (valueInResult.equals(entry.getValue())) {
					// nothing to do; result already contains the entry with the same value
				}
				else {
					return null;
				}
			}
			else {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	/**
	 * Creates an array list of size n, filled with a given value.
	 * @param n the size of the array
	 * @param value the value to fill the array with
	 * @param <T> the type of value
	 * @return an array list of size n, filled with a given value.
	 */
	public static <T> ArrayList<T> fill(int n, T value) {
		ArrayList<T> result = new ArrayList<T>(n);
		for (int i = 0; i != n; i++) {
			result.add(value);
		}
		return result;
	}

	/**
	 * Creates an array list of size n, filled with the results of a given {@link NullaryFunction}.
	 * @param n the size of the array
	 * @param function the nullary function
	 * @param <T> the type of value
	 * @return an array list of size n, filled with a given value.
	 */
	public static <T> ArrayList<T> fill(int n, NullaryFunction<T> function) {
		ArrayList<T> result = new ArrayList<T>(n);
		for (int i = 0; i != n; i++) {
			result.add(function.apply());
		}
		return result;
	}

	public static <T> ArrayList<T> fillArrayList(int size, T value) {
		ArrayList<T> result = new ArrayList<T>(size);
		for (int i = 0; i != size; i++) {
			result.add(value);
		}
		return result;
	}

	/**
	 * Given an array list and a list of integers, returns an array list with the indexed elements in the indices order.
	 * @param array the array from which to extract sub array.
	 * @param indices the array of indices
	 * @param <E> the type of values
	 * @return array list with the indexed elements in the indices order.
	 */
	public static <E> ArrayList<E> makeCopyWithGivenIndices(ArrayList<E> array, List<Integer> indices) {
		ArrayList<E> result = new ArrayList<E>(indices.size());
		for (int i = 0; i != indices.size(); i++) {
			Integer ithIndex = indices.get(i);
			E elementAtIthIndex = array.get(ithIndex);
			result.add(elementAtIthIndex);
		}
		return result;
	}

	/**
	 * Stores the elements from an iterable of iterables in an array list of array lists.
	 * @param iterableOfIterables iterable of iterables
	 * @return array of arrays
	 * @param <T> type
	 */
	public static <T> ArrayList<ArrayList<T>> storeIterableOfIterablesInArrayListOfArrayLists(Iterable<Iterable<T>> iterableOfIterables) {
		ArrayList<ArrayList<T>> result;
		result = arrayList();
		for (Iterable<T> iterable : iterableOfIterables) {
			ArrayList<T> innerArrayList = arrayList();
			for (T element : iterable) {
				innerArrayList.add(element);
			}
			result.add(innerArrayList);
		}
		return result;
	}

	/**
	 * Put given value in a list indexed by key, creating a new linked list if the map still does not contain the key.
	 * @param mapToLists map to lists
	 * @param key a key
	 * @param value a value
	 * @param <K> key type
	 * @param <V> value type
	 */
	public static <K,V> void putInListValue(Map<K, List<V>> mapToLists, K key, V value) {
		List<V> list;
		if (mapToLists.containsKey(key)) {
			list = mapToLists.get(key);
		}
		else {
			list = new LinkedList<V>();
			mapToLists.put(key, list);
		}
		list.add(value);
	}

	/**
	 * Allocates an array list of given size filled with given value.
	 * @param value a value
	 * @param size size of list
	 * @param <T> type of elements
	 * @return an array list of given size with all elements equal to given value
	 */
	public static <T> ArrayList<T> arrayListFilledWith(T value, int size) {
		ArrayList<T> result = new ArrayList<>(size);
		for (int i = 0; i != size; i++) {
			result.add(value);
		}
		return result;
	}

	/**
	 * If there is more than one element in iterator's range and they are all equal to the first one,
	 * returns the first one;
	 * otherwise, returns null.
	 * @param elements the elements
	 * @param <T> type of elements
	 * @return the first element if all elements are equal, or null.
	 */
	public static <T> T ifAllTheSameOrNull(Iterator<T> elements) {
		if (elements.hasNext()) {
			T candidate = elements.next();
			while (elements.hasNext()) {
				T next = elements.next();
				if (! next.equals(candidate)) {
					return null;
				}
			}
			return candidate;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Shorthand for <code>System.out.print</code>.
	 * @param object the object to be printed.
	 * @param <T> the type
	 */
	public static <T> void print(T object) {
		System.out.print(object);
	}

	public static void print() {
		System.out.print("");
	}
	
	/**
	 * Shorthand for <code>System.out.println</code>.
	 * @param object the object to be printed.
	 * @param <T> the type
	 */
	public static <T> void println(T object) {
		System.out.println(object);
	}

	public static void println() {
		System.out.println();
	}
	
	/**
	 * Same as <code>println(join(" ", objects))</code>.
	 */
	public static void println(Object... objects) {
		println(join(" ", objects));
	}

	/**
	 * Get class object for given class name or throws an IllegalArgumentException.
	 * @param className the class name
	 * @return the class object
	 */
	public static Class<?> getClassOrIllegalArgumentException(String className) {
		try {
			return Class.forName(className);
		} catch (Throwable throwable) {
			throw new IllegalArgumentException(throwable);
		}
	}

	/**
	 * Computes a 0h0min0.000sec string for a number of milliseconds.
	 * @param milliseconds the total number of milliseconds.
	 * @return the string in 0h0min0.000sec format.
	 */
	public static String toHoursMinutesAndSecondsString(long milliseconds) {
		long hours = 0L, minutes = 0L, seconds = 0L, remainingMilliseconds = 0L;
		long remainingDuration = milliseconds;
		
		if (remainingDuration != 0) {
			hours    = remainingDuration / 3600000;
			remainingDuration = remainingDuration % 3600000; 
		}
		if (remainingDuration != 0) {
			minutes  = remainingDuration / 60000;
			remainingDuration = remainingDuration % 60000;
		}
		if (remainingDuration != 0) {
			seconds  = remainingDuration / 1000;
			remainingDuration = remainingDuration % 1000;
		}
		remainingMilliseconds = remainingDuration;
		
		return hours + "h" + minutes + "m" + seconds + "." + remainingMilliseconds + "s";
	}

	public static String getFileContent(File file) {
		try {
			String fileContents = Files.readAllLines(file.toPath()).stream().collect(Collectors.joining("\n"));
			return fileContents;
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	/**
	 * Makes a map with given keys mapping to the same given value.
	 * @param keys the keys
	 * @param value the value
	 * @param <K> the key type
	 * @param <V> the value type
	 * @return the map.
	 */
	public static <K, V> Map<K, V> mapWithTheseKeysMappedTo(Collection<? extends K> keys, V value) {
		Map<K, V> result = new LinkedHashMap<>();
		for (K key : keys) {
			result.put(key, value);
		}
		return result;
	}

	/**
	 * Increments the integer value associated with given key.
	 * @param map the map
	 * @param key the key
	 * @param <K> the key type
	 */
	public static <K> void incrementValue(Map<K, Integer> map, K key) {
		Integer valueObject = map.get(key);
		int value = valueObject == null? 0 : valueObject.intValue();
		map.put(key, value + 1);
	}
	
	/**
	 * Measures how long it takes to execute thunk.
	 * @param thunk a function without arguments returning a value
	 * @return a pair with the thunk's result and how long it took to execute in milliseconds.
	 * @param <T> the function return type
	 */
	public static <T> Pair<T, Long> time(NullaryFunction<T> thunk) {
		long start = System.currentTimeMillis();
		T thunkResult = thunk.apply();
		long end = System.currentTimeMillis();
		long time = end - start;
		Pair<T, Long> result = Pair.pair(thunkResult, time);
		return result;
	}

	/**
	 * Returns given value if not null, or use nullary function to make a default value otherwise.
	 * @param value the value
	 * @param defaultMaker the default maker
	 * @param <T> the type
	 * @return the value or a freshly made default
	 */
	public static <T> T valueOrDefaultIfNull(T value, NullaryFunction<T> defaultMaker) {
		T result;
		if (value == null) {
			result = defaultMaker.apply();
		}
		else {
			result = value;
		}
		return result;
	}
	
	public static <T> T valueOrDefaultIfNull(T value, T defaultValue) {
		T result;
		if (value == null) {
			result = defaultValue;
		}
		else {
			result = value;
		}
		return result;
	}
	
	/**
	 * Copies list without the element at a given position.
	 * @param list the list
	 * @param i the index
	 * @param <T> the type
	 * @return the copy without the i-th original element.
	 */
	public static <T> LinkedList<T> listWithoutElementAt(List<? extends T> list, int i) {
		LinkedList<T> result = list();
		ListIterator<? extends T> iterator = list.listIterator();
		for (int j = 0; j != list.size(); j++) {
			T jThElement = iterator.next();
			if (j != i) {
				result.add(jThElement);
			}
		}
		return result;
	}

	/**
	 * Returns <code>identity</code> if iterator is empty
	 * or <code>function(iterator.next(), accumulate(iterator, function identity))</code> otherwise.
	 * This function is also known as <code>fold</code> in functional programming.
	 * @param iterator the iterator
	 * @param function the function
	 * @param initial the initial value
	 * @param <T> the type
	 * @return <code>identity</code> if iterator is empty
	 * or <code>function(iterator.next(), accumulate(iterator, function identity))</code> otherwise.
	 */
	public static <T> T accumulate(Iterator<? extends T> iterator, BinaryFunction<T, T, T> function, T initial) {
		T result = initial;
		for (T element : in(iterator)) {
			result = function.apply(result, element);
		}
		return result;
	}

	/**
	 * Equivalent to <code>accumulate(collection.iterator(), function, identity))</code>.
	 * @param collection the collection
	 * @param function the function
	 * @param initial the initial value
	 * @param <T> the type
	 * @return <code>accumulate(collection.iterator(), function, identity))</code>
	 */
	public static <T> T accumulate(Collection<? extends T> collection, BinaryFunction<T, T, T> function, T initial) {
		return accumulate(collection.iterator(), function, initial);
	}

	public static <T> Set<T> unionOfCollections(Collection<? extends Collection<? extends T>> collectionsOfCollections) {
		LinkedHashSet<T> result = set();
		for (Collection<? extends T> collection : collectionsOfCollections) {
			result.addAll(collection);
		}
		return result;
	}

	public static <T> List<T> makeListAndAdd(Collection<T> collection, T element) {
		LinkedList<T> result = new LinkedList<>(collection);
		result.add(element);
		return result;
	}

	public static boolean isNullOrEmptyString(String string) {
		boolean result = string == null || string.trim().equals("");
		return result;
	}
	
	public static <T> LinkedList<T> mergeElementsIntoOneList(T firstElement, Collection<T> otherElements) {
		LinkedList<T> allElements = new LinkedList<>();
		allElements.add(firstElement);
		allElements.addAll(otherElements);
		return allElements;
	}

	/**
	 * Returns a sub-list of a list corresponding to its first half (with rounding UP in the case of an odd-sized list).
	 * @param list
	 * @return
	 */
	public static <T> List<T> getFirstHalfSubList(List<T> list) {
		int numberOfHalfTheElements = (list.size() + 1)/2;
		List<T> firstHalfElements = list.subList(0, numberOfHalfTheElements);
		return firstHalfElements;
	}
	
	/**
	 * Returns a sub-list of a list corresponding to its LAST half (with rounding UP in the case of an odd-sized list).
	 */
	public static <T> List<T> getLastHalfSubList(List<T> list) {
		int numberOfHalfTheElements = (list.size())/2;
		List<T> firstHalfElements = list.subList(numberOfHalfTheElements, list.size());
		return firstHalfElements;
	}

	public static int getIndexOf(Object[] array, Object element) {
		return Arrays.asList(array).indexOf(element);
	}

	/**
	 * Returns index of first element in collection equal to given element
	 * according to natural iteration order, or -1 if not found.
	 * @param collection
	 * @param element
	 * @return
	 */
	public static <T> int getIndexOf(Collection<? extends T> collection, T element) {
		Iterator<? extends T> iterator = collection.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			if (iterator.next().equals(element)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public static String readContentsOfFile(String fileName) throws Error {
		StringBuilder resultBuilder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName));) {
			String line;
			while ((line = reader.readLine()) != null) {
				resultBuilder.append(line + "\n");
			}
		} catch (IOException e) {
			throw new Error("Could not read " + fileName, e);
		}
		String result = resultBuilder.toString();
		return result;
	}

	/**
	 * Equivalent to:
	 * <p> 
	 * <code>myAssert(classToBe.isInstance(object), () -> requester + " requires " + classToBe + " but got " + object + " of class " + object.getClass())</code>
	 * 
	 * @param object
	 * @param classToBe
	 * @param requester
	 */
	@SuppressWarnings("unchecked")
	public static <T> T assertType(Object object, Class<T> classToBe, Object requester) {
		myAssert(classToBe.isInstance(object), () -> requester + " requires " + classToBe + " but got " + object + " of class " + object.getClass());
		return (T) object;
	}

	/**
	 * Normalize n weights into a probability distribution,
	 * while smoothing by adding ((1 + smoothingCoefficient)*sum weights)/n to each weight
	 * (or making it a uniform distribution if partition is 0),
	 * returning both the probabilities and the smoothened partition.
	 * @param weights
	 * @param smoothingCoefficient
	 * @return
	 */
	public static Pair<ArrayList<Double>, Double> probabilities(ArrayList<Double> weights, double smoothingCoefficient) {
		Double partition = sum(weights).doubleValue();
		int n = weights.size();
		if (partition == 0) {
			return Pair.pair(uniformDistributionArray(n), 1.0/n);
		}
		else {
			double smoothingAdded = partition*smoothingCoefficient;
			double smoothenedPartition = partition + smoothingAdded;
			double smoothingPerItem = smoothingAdded/n;
			ArrayList<Double> probabilities = new ArrayList<Double>(n);
			for (int i = 0; i != n; i++) {
				probabilities.add((weights.get(i) + smoothingPerItem) / smoothenedPartition);
			}
			return Pair.make(probabilities, smoothenedPartition);
		}
	}

	public static ArrayList<Double> uniformDistributionArray(int n) {
		return fillArrayList(n, 1.0/n);
	}

	/**
	 * Sample from an array of probabilities.
	 * @param probabilities
	 * @param random
	 * @return
	 * @throws Error
	 */
	public static int sample(ArrayList<Double> probabilities, Random random) throws Error {
		double point = random.nextDouble();
		int i = 0;
		double behindIthElement = 0;
		while (i != probabilities.size()) {
			behindIthElement += probabilities.get(i);
			if (point < behindIthElement) {
				return i;
			}
			i++;
		}
		throw new Error("Should have sampled a value but picked point " + point + " which did not work for probabilities " + probabilities);
	}

	/**
	 * Returns a string formed by repeating a character n times.
	 * @param n
	 * @param character
	 * @return
	 */
	public static String fill(int n, char character) {
		return Strings.padStart("", n, character);
	}

	public static <K,V> Map<K, V> fillMap(Collection<? extends K> keys, java.util.function.Function<K, V> valueMaker) {
		Map<K, V> map = map();
		for (K key : keys) {
			map.put(key, valueMaker.apply(key));
		}
		return map;
	}

	public static <T> T get(Iterable<T> iterable, int i) {
		  if (iterable instanceof List) {
			  return getFromIterableKnownToBeList(iterable, i);
		  }
		  else {
			  return getByExplicitlyIterating(iterable, i);
		  }
	  }

	@SuppressWarnings("unchecked")
	  public static <T> T getFromIterableKnownToBeList(Iterable<T> iterable, int i) {
		  T result = (T) ((List) iterable).get(i);
		  return result;
	  }

	public static <T> T getByExplicitlyIterating(Iterable<T> iterable, int i) {
		  int j = 0;
		  for (T element : iterable) {
			  if (j == i) {
				  return element;
			  }
			  j++;
		  }
		  return null;
	  }

	/**
	 * Same as {@link #flattenOneLevel(ArrayList, Function, ArrayList)} with an empty array list as the last argument.
	 * @param list
	 * @param expand
	 * @return
	 */
	public static <T> ArrayList<? extends T> flattenOneLevelToArrayList(
			ArrayList<? extends T> list, 
			java.util.function.Function<T, ArrayList<? extends T>> expand) {
		
		return flattenOneLevel(list, expand, arrayList());
	}

	/**
	 * Returns a list obtained by iterating over a list, applying a function to its elements that generate lists of sub-elements,
	 * and placing sub-elements in a given list.
	 * @param list
	 * @param expand
	 * @return
	 */
	public static <T> ArrayList<? extends T> flattenOneLevel(
			ArrayList<? extends T> list,
			java.util.function.Function<T, ArrayList<? extends T>> expand, 
			ArrayList<T> result) {
		
		for (T factor : list) {
			result.addAll(expand.apply(factor));
		}
		return result;
	}

	/**
	 * Returns a {@link java.util.function.Predicate<Integer>} that is true for i if <code>booleanArray[i]</code> is true.
	 * @param booleanArray
	 * @return
	 */
	public static java.util.function.Predicate<Integer> asPredicate(ArrayList<Boolean> booleanArray) {
		return i -> booleanArray.get(i);
	}

	public static <T> List<T> collectThoseWhoseIndexSatisfy(Collection<? extends T> list, java.util.function.Predicate<Integer> predicate) {
		List<T> result = list();
		int i = 0;
		for (T element : list) {
			if (predicate.test(i)) {
				result.add(element);
			}
			i++;
		}
		return result;
	}

	public static <T> List<? extends T> collectThoseWhoseIndexSatisfyArrayList(Collection<? extends T> collection, ArrayList<Boolean> booleanArrayList) {
		return collectThoseWhoseIndexSatisfy(collection, asPredicate(booleanArrayList));
	}

	/**
	 * Increments a given position in an array list of integers.
	 * @param array
	 * @param i
	 */
	public static void increment(ArrayList<Integer> array, int i) {
		array.set(i,  1 + array.get(i));
	}

	public static double percentageWithTwoDecimalPlaces(int i, long total) {
		return Math.round(i*1.0/total * 10000)/100.0;
	}

	/**
	 * Collect indices of elements in collection that satisfy predicate.
	 * @param collection
	 * @param predicate
	 * @return
	 */
	public static <T> List<Integer> collectIndices(Collection<? extends T> collection, Predicate<T> predicate) {
		List<Integer> result = list();
		Iterator<? extends T> iterator = collection.iterator();
		for (int i = 0; i != collection.size(); i++) {
			T element = iterator.next();
			if (predicate.apply(element)) {
				result.add(i);
			}
		}
		return result;
	}

	/**
	 * Creates a new list with elements from a list corresponding to given indices (using random access, which is constant time for array lists but linear time for linked lists).
	 * @param list
	 * @param indices
	 * @return
	 */
	public static <T> List<T> splice(List<? extends T> list, Collection<? extends Integer> indices) {
		List<T> result = list();
		for (int index : indices) {
			result.add(list.get(index));
		}
		return result;
	}

	/**
	 * Creates a list containing the elements in the range of a default {@link NestedIterator}.
	 * @param objects
	 * @return
	 */
	public static <T> List<T> flatList(Object... objects) {
		NestedIterator<T> nestedIterator = new NestedIterator<T>(objects);
		List<T> result = listFrom(nestedIterator);
		return result;
	}

	/**
	 * Returns an iterator over the elements of a collection whose indices satisfy a predicate.
	 * @param collection
	 * @param predicate
	 * @return
	 */
	public static <T> Iterator<T> filterByIndexIterator(Collection<T> collection, Predicate<Integer> predicate) {
		return filterByIndexIterator(collection.iterator(), predicate);
	}

	/**
	 * Returns an iterator over the elements of an iterator whose indices satisfy a predicate.
	 * @param iterator
	 * @param predicate
	 * @return
	 */
	public static <T> Iterator<T> filterByIndexIterator(Iterator<T> iterator, Predicate<Integer> predicate) {
		Iterator<Pair<T, Integer>> indexedIterator = functionIterator(iterator, new IndexingFunction<T>());
		Iterator<Pair<T, Integer>> filteredIndexedIterator = predicateIterator(indexedIterator, p -> predicate.apply(p.second));
		Iterator<T> filteredIterator = functionIterator(filteredIndexedIterator, p -> p.first);
		return filteredIterator;
	}

	/**
	 * Accumulates the result of a binary operator by applying it to the elements in an iterator's range or,
	 * more formally, returns <code>initial</code> if <code>iterator.hasNext()</code> is false,
	 * or <code>operator(fold(iterator, operator, initial), iterator.next())</code> otherwise.
	 * @param iterator
	 * @param operator
	 * @param initial
	 * @return
	 */
	public static <T> T fold(Iterator<? extends T> iterator, BinaryFunction<T, T, T> operator, T initial) {
		T result = initial;
		for (T element : in(iterator)) {
			result = operator.apply(result, element);
		}
		return result;
	}

	/** 
	 * Same as {@link #fold(Iterator, BinaryFunction, Object)} but for a collection.
	 * @param collection
	 * @param operator
	 * @param initial
	 * @return
	 */
	public static <T> T fold(Collection<? extends T> collection, BinaryFunction<T, T, T> operator, T initial) {
		return fold(collection.iterator(), operator, initial);
	}
	
	/**
	 * Accumulates the result of a binary operator by applying it to the elements in an iterator's range
	 * while short-circuiting if an absorbing element
	 * (an element <code>t</code> such that <code>t operator u = t for all u</code>) is reached or,
	 * more formally, returns <code>initial</code> if <code>iterator.hasNext()</code> is false or <code>initial</code> is absorbing,
	 * or <code>operator(fold(iterator, operator, initial), iterator.next())</code> otherwise.
	 * @param iterator
	 * @param operator the operator
	 * @param initial the initial value
	 * @param isAbsorbingElement indicates whether a value is an absorbing element
	 * @return
	 */
	public static <T> T fold(Iterator<? extends T> iterator, BinaryFunction<T, T, T> operator, T initial, Predicate<T> isAbsorbingElement) {
		T result = initial;
		for (T element : in(iterator)) {
			if (isAbsorbingElement.apply(result)) {
				break;
			}
			result = operator.apply(result, element);
		}
		return result;
	}

	/**
	 * Same as {@link #fold(Iterator, BinaryFunction, Object, Predicate)} but for a collection.
	 * @param collection
	 * @param operator
	 * @param initial
	 * @param isAbsorbingElement
	 * @return
	 */
	public static <T> T fold(Collection<? extends T> collection, BinaryFunction<T, T, T> operator, T initial, Predicate<T> isAbsorbingElement) {
		return fold(collection.iterator(), operator, initial, isAbsorbingElement);
	}
	
	/**
	 * Creates a map given collections of keys and values (associated through iteration order).
	 * @param keys
	 * @param values
	 * @return
	 */
	public static <K, V> Map<K, V> mapFromLists(Collection<? extends K> keys, Collection<? extends V> values) {
		myAssert(keys.size() == values.size(), () -> "mapFromLists requires the same number of keys and values, but got " + keys.size() + " and " + values.size());
		Map<K, V> result = map();
		Iterator<? extends K> keyIterator = keys.iterator();
		Iterator<? extends V> valueIterator = values.iterator();
		while (keyIterator.hasNext()) {
			K key = keyIterator.next();
			V value = valueIterator.next();
			result.put(key, value);
		}
		return result;
	}

	/** Executes a given {@link NullaryProcedure} a given number of times. */
	public static void repeat(int n, NullaryProcedure procedure) {
		for (int i = 0; i != n; i++) {
			procedure.apply();
		}
	}

	/** Executes a given {@link NullaryProcedure} a given number of times. */
	public static void repeat(int n, Procedure<Integer> procedure) {
		for (int i = 0; i != n; i++) {
			procedure.apply(i);
		}
	}

	/**
	 * Returns a new double array which is a repetition of a given array n times.
	 */
	public static double[] repeat(int n, double[] array) {
		var result = new double[array.length * n];
		int index = 0;
		for (int i = 0; i != n; i++) {
			for (int j = 0; j != array.length; j++) {
				result[index++] = array[j];
			}
		}
		return result;
	}
	
	public static boolean containsAllCaseInsensitive(String string, String... subStrings) {
		return containsAllCaseInsensitive(string, Arrays.asList(subStrings));
	}

	public static boolean containsAllCaseInsensitive(String string, List<String> subStrings) {
		String lowerCase = string.toLowerCase();
		boolean result = forAll(subStrings, s -> lowerCase.contains(s.toLowerCase()));
		return result;
	}

	public static Double parseAsDoubleOrNull(String string) {
		try {
			return Double.parseDouble(string);
		}
		catch (Exception e) {
			return null;
		}
	}

	public static boolean objectStringEqualsOneOf(Object object, String... strings) {
		String objectString = object.toString();
		boolean result = thereExists(strings, s -> objectString.equals(s));
		return result;
	}

	/**
	 * While a condition applies to elements in an iterator's range, applies an action to them.
	 * @param iterator
	 * @param condition
	 * @param action
	 */
	public static <T> void whileDo(Iterator<? extends T> iterator, Predicate<T> condition, Procedure<T> action) {
		while (iterator.hasNext()) {
			T element = iterator.next();
			if ( ! condition.apply(element)) {
				break;
			}
			action.apply(element);
		}
	}

	/**
	 * While a condition applies to elements in collection, applies an action to them.
	 * @param iterator
	 * @param condition
	 * @param action
	 */
	public static <T> void whileDo(Collection<? extends T> collection, Predicate<T> condition, Procedure<T> action) {
		whileDo(collection.iterator(), condition, action);
	}

	/**
	 * While a nullary condition applies to elements in an iterator's range, applies an action to them.
	 * @param iterator
	 * @param condition
	 * @param action
	 */
	public static <T> void whileDo(Iterator<? extends T> iterator, NullaryPredicate condition, Procedure<T> action) {
		while (iterator.hasNext()) {
			T element = iterator.next();
			if ( ! condition.apply()) {
				break;
			}
			action.apply(element);
		}
	}

	/**
	 * While a nullary condition applies to elements in collection, applies an action to them.
	 * @param iterator
	 * @param whileCondition
	 * @param action
	 */
	public static <T> void whileDo(Collection<? extends T> collection, NullaryPredicate condition, Procedure<T> action) {
		whileDo(collection.iterator(), condition, action);
	}

	/**
	 * Convenience method for making a proxy for a target interface.
	 * This makes the following assumptions:
	 * <ul>
	 * <li> the target interface defines an {@link InvocationHandler} class
	 * <li> the {@link InvocationHandler} class has a single constructor
	 * receiving exactly the same arguments as this method receives
	 * after the target interface argument.
	 * </ul>
	 * If the handler class has a "proxy" field, the method stores
	 * the proxy in the handler's field.
	 * <p>
	 * The class loader used for the proxy is the same as the
	 * first argument after the target interface.
	 * @param targetInterface
	 * @param args
	 * @return
	 */
	public static <T> T makeProxy(Class<T> targetInterface, Object... args) {
	
			Object wrappedObject = args[0];
			InvocationHandler handler = makeInvocationHandler(targetInterface, args);
			T proxy = 
					targetInterface.cast(
							newProxyInstance(
									wrappedObject.getClass().getClassLoader(),
									new Class[] { targetInterface },
									handler));

			tryToStoreProxyInHandler(handler, proxy);
			
			return proxy;
	}

	private static <T> void tryToStoreProxyInHandler(InvocationHandler handler, T proxy) throws Error {
		Field handlerProxyField;
		try {
			Class<? extends InvocationHandler> handlerClass = handler.getClass();
			handlerProxyField = handlerClass.getField("proxy");
			handlerProxyField.set(handler, proxy);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
		}
	}

	/**
	 * Convenience method for making an {@link InvocationHandler} for a proxy for a target interface.
	 * This makes the following assumptions:
	 * <ul>
	 * <li> the target interface defines an {@link InvocationHandler} class
	 * <li> the {@link InvocationHandler} class has a single constructor
	 * receiving exactly the same arguments as this method receives
	 * after the target interface argument.
	 * </ul>
	 * @param targetInterface
	 * @param args
	 * @return
	 */
	public static InvocationHandler makeInvocationHandler(Class targetInterface, Object... args) {
		try {
	
			InvocationHandler handler;
	
			Class<?>[] classes = targetInterface.getClasses();
			
			Class handlerClass = 
					getFirstSatisfyingPredicateOrNull(
							classes,
							c -> InvocationHandler.class.isAssignableFrom(c));
	
			handler = (InvocationHandler) handlerClass.getConstructors()[0].newInstance(args);
			
			return handler;
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			throw new Error(e);
		}
	}

	/**
	 * Returns the first string of the form <code>base + i</code> where <code>i</code> is
	 * an integer greater than 0 and less than <code>Integer.MAX_VALUE</code>
	 * that satisfies a given predicate,
	 * or throws an error if there isn't any.
	 * @param base
	 * @param isNew
	 * @return
	 */
	public static String makeNewIdentifier(String base, Predicate<String> isNew) {
		String candidate;
		int i = 1;
		boolean foundNew = false;
		do {
			candidate = base + i++;
			foundNew = isNew.apply(candidate);
		} while (!foundNew && i != Integer.MAX_VALUE);
		if (foundNew) {
			return candidate;
		}
		else {
			throw new Error("Searching for new interpreter with base " + base + " but all completions with natural numbers up to Integer.MAX_VALUE are taken");
		}
	}

	/** Returns a {@link ListIterator} positioned at the very end of a given list. */
	public static <T> ListIterator<T> backIterator(List<T> list) {
		return list.listIterator(list.size());
	}

	/** 
	 * Returns a {@link ListIterator} that iterates in reverse direction from a list.
	 * Note that this is <b>not</not> a {@link ListIterator} to be iterated with {@link ListIterator#previous()};
	 * iterating it with {@link Iterator#next()} will move on the list from last to first.
	 * Likewise, using {@link ListIterator#previous()} will move it forward in the list.
	 * This is achieved by using {@link ReverseListIterator} and its purpose is the same,
	 * namely to be able to use methods using regular iterators with {@link Iterator#next()},
	 * but going backwards.
	 */
	public static <T> ListIterator<T> reverseListIterator(List<T> list) {
		return new ReverseListIterator<T>(backIterator(list));
	}

	/** 
	 * Returns an {@link Iterator} that iterates in reverse direction from a list.
	 * Note that this is <b>not</not> a {@link ListIterator} to be iterated with {@link ListIterator#previous()};
	 * iterating it with {@link Iterator#next()} will move on the list from last to first.
	 * This is achieved by using {@link ReverseIterator} and its purpose is the same,
	 * namely to be able to use methods using regular iterators with {@link Iterator#next()},
	 * but going backwards.
	 */
	public static <T> ReverseIterator<T> reverseIterator(List<T> list) {
		return new ReverseIterator<T>(backIterator(list));
	}

	/** 
	 * Returns an {@link Iterator} that iterates an iterator in the reverse direction.
	 * This can only be achieved by storing the iterated elements in a list, though, so
	 * the iterator must have a finite range.
	 */
	public static <T> ReverseIterator<T> reverse(Iterator<T> iterator) {
		return new ReverseIterator<T>(backIterator(addAllToList(iterator)));
	}

	/**
	 * Gets the index-th elements in iterator's range
	 * @param iterator
	 * @param index
	 * @return
	 */
	public static <T> T get(Iterator<? extends T> iterator, int index) {
		while (iterator.hasNext()) {
			T next = iterator.next();
			if (index-- == 0) {
				return next;
			}
		}
		return null;
	}

	public static <T> Predicate<? super T> notContainedBy(Collection<? extends T> collection) {
		return t -> ! collection.contains(t);
	}
	
	/**
	 * Given an iterable, returns a {@link IdentityHashMap} mapping
	 * elements in the iterable to the results of a given function applied to the elements.
	 * This is useful for example for collecting a given common property from a collection of objects.
	 * @param iterable
	 * @param property
	 * @return
	 */
	public static <T, P> IdentityHashMap<T, P> collectProperties(Iterable<? extends T> iterable, Function<T, P> property) {
		IdentityHashMap<T, P> result = new IdentityHashMap<>();
		for (T t : iterable) {
			result.put(t, property.apply(t));
		}
		return result;
	}
	
	/**
	 * Given an {@link IdentityHashMap} from objects to the values of a certain common property,
	 * and a binary procedure for setting the property of an object,
	 * this method sets the property for every key in the map.
	 * @param properties
	 * @param setProperty
	 */
	public static <T, P> void restoreProperties(IdentityHashMap<T, P> properties, BinaryProcedure<T, P> setProperty) {
		for (Map.Entry<T, P> entry : properties.entrySet()) {
			setProperty.apply(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * A better estimate of free memory than {@link Runtime#freeMemory()} that works
	 * even if the current garbage collector changes the amount of memory currently allocated to the JVM process.
	 * This is based on <a href="https://stackoverflow.com/a/55198260/3358488">a Stack Overflow answer</a>.
	 * @return
	 */
	public static long actualFreeMemory() {
		Runtime r = Runtime.getRuntime();
		long freeMemoryNotCurrentlyAvailableToTheProcess = r.maxMemory() - r.totalMemory();
		long freeMemoryCurrentlyAvailableToTheProcess = r.freeMemory();
		long actualFreeMemory = freeMemoryNotCurrentlyAvailableToTheProcess + freeMemoryCurrentlyAvailableToTheProcess;
		return actualFreeMemory;
	}

	/**
	 * Return 0.0 if value is -0.0 or value itself otherwise.
	 * @param value
	 * @return
	 */
	public static double normalizeDoubleZeroToPositiveZero(double value) {
		if (value == -0.0) {
			return 0.0;
		}
		return value;
	}

	public static Double getDoubleValue(Object number) {
		myAssert(number instanceof Number, () -> (new Enclosing(){}).methodName() + " requires object to be Number but was " + number + " of class " + number.getClass()); 
		return ((Number) number).doubleValue();
	}

	public static Double getDoubleValueWithDoubleZeroNormalizedToPositive(Object number) {
		myAssert(number instanceof Number, () -> (new Enclosing(){}).methodName() + " requires object to be Number but was " + number + " of class " + number.getClass()); 
		double doubleValue = ((Number) number).doubleValue();
		double result = normalizeDoubleZeroToPositiveZero(doubleValue);
		return result;
	}

	public static Object getDoubleValueWithDoubleZeroNormalizedToPositiveOrSelfOtherwise(Object object) {
		if (object instanceof Number) {
			return getDoubleValueWithDoubleZeroNormalizedToPositive(object);
		}
		else {
			return object;
		}
	}

	/**
	 * Returns the longest prefix of a given string up to a given length limit.
	 */
	public static String prefix(String string, int limit) {
		return string.substring(Math.min(limit, string.length()));
	}

	/**
	 * Returns the longest postfix of a given string up to a given length limit.
	 */
	public static String postfix(String string, int limit) {
		if (limit > string.length()) {
			return string;
		}
		else {
			return string.substring(string.length() - limit);
		}
	}

	/**
	 * Puts all given new (key, value) pairs to a map.
	 * Keys and values are associated by position in their respective iterables.
	 * If iterables range over a different number of elements, the larger one's remaining elements are ignored.
	 * @param map
	 * @param keys
	 * @param values
	 */
	public static <K, V> void putAll(Map<K, V> map, Iterable<? extends K> keys, Iterable<? extends V> values) {
		Iterator<? extends K> keyIterator = keys.iterator();
		Iterator<? extends V> valuesIterator = values.iterator();
		while (keyIterator.hasNext() && valuesIterator.hasNext()) {
			map.put(keyIterator.next(), valuesIterator.next());
		}
	}

	/**
	 * Compare two iterables of numbers component-wise with a given tolerance for the difference of the ratio of each pair of components to 1.
	 * An assertion error is thrown if they do not produce the same amount of elements.
	 * @param c1
	 * @param c2
	 */
	public static void compareNumbersComponentWise(Iterable<? extends Number> c1, Iterable<? extends Number> c2, double maximumRatioDistanceFromOne) {
		Iterator<? extends Number> iterator1 = c1.iterator();
		Iterator<? extends Number> iterator2 = c2.iterator();
		while (iterator1.hasNext() && iterator2.hasNext()) {
			compareNumbers(iterator1.next(), iterator2.next(), maximumRatioDistanceFromOne);
		}
		if (iterator1.hasNext() || iterator2.hasNext()) {
			throw new AssertionError("Compared iterables do not have the same size.");
		}
	}

	/**
	 * Compares two numbers with a given tolerance for the difference between their ratio and 1.
	 * @param number1
	 * @param number2
	 * @param maximumRatioDistanceFromOne
	 */
	public static void compareNumbers(Number number1, Number number2, double maximumRatioDistanceFromOne) {
		double ratio = Math.abs(number1.doubleValue() / number2.doubleValue() - 1.0);
		myAssert(ratio <= maximumRatioDistanceFromOne, () -> "Ratio of " + number1 + " and " + number2 + " is greater than " + maximumRatioDistanceFromOne);
		// TODO: we do not want to use myAssert here if this is being used for testing, since deactiving myAssert may render tests that are not passing silent.
	}

	/**
	 * Returns an int array containing the original indices (in an original ArrayList) of the elements in an reindexed Collection.
	 * @param original
	 * @param reindexed
	 * @return an int array containing the original indices (in an original ArrayList) of the elements in an reindexed Collection
	 */
	public static <T> int[] indexOf(ArrayList<? extends T> original, Collection<? extends T> reindexed) {
		int[] result = new int[reindexed.size()];
		Iterator<? extends T> reindexedIterator = reindexed.iterator();
		for (int i = 0; i != reindexed.size(); i++) {
			T reindexedElement = reindexedIterator.next();
			result[i] = original.indexOf(reindexedElement);
		}
		return result;
	}

	public static int[] toIntArray(List<Integer> integerList) {
		return toPrimitive(integerList.toArray(new Integer[integerList.size()]));
	}

	public static boolean ratioisInOnePlusOrMinusEpsilon(double value1, double value2, double epsilon) {
		boolean areEqual;
		if (value2 == 0.0 || value2 == -0.0) {
			if (value1 == 0.0 || value1 == -0.0) {
				areEqual = true;
			}
			else {
				// value2 is 0, absolute value of value1 must be very small
				areEqual = Math.abs(value1) < epsilon;
			}
		}
		else if (value1 == 0.0 || value1 == -0.0) {
			// value1 is 0, absolute value of value2 must be very small
			areEqual = Math.abs(value2) < epsilon;
		}
		else {
			double ratio = value1 / value2;
			areEqual = ratio > 1 - epsilon && ratio < 1 + epsilon;
		}
		return areEqual;
	}

	/**
	 * Returns a {@link ListIterator} that has just provided the first element satisfying a given predicate,
	 * or <code>null</code> if there aren't any elements satisfying the predicate.
	 * @param list
	 * @param predicate
	 * @return
	 */
	public static <T> ListIterator<T> find(List<T> list, Predicate<? super T> predicate) {
		var iterator = list.listIterator();
		while (iterator.hasNext()) {
			if (predicate.apply(iterator.next())) {
				return iterator;
			}
		}
		return null;
	}

	/**
	 * Given two collections and an equivalence relation, indicates whether there is a one-to-one matching between them.
	 */
	public static <T> boolean thereIsAOneToOneMatching(
			Collection<? extends T> c1, 
			Collection<? extends T> c2,
			BinaryPredicate<? super T, ? super T> equivalenceRelation) {
		
		if (c1.size() != c2.size()) {
			return false;
		}
		
		List<T> l2 = listFrom(c2);
		Iterator<? extends T> iterator1 = c1.iterator();
		boolean equivalentSoFar = true;
		while (equivalentSoFar && iterator1.hasNext()) {
			T e1 = iterator1.next();
			var iterator2 = find(l2, e2 -> equivalenceRelation.apply(e1, e2));
			if (iterator2 != null) {
				iterator2.remove();
			}
			else {
				equivalentSoFar = false;
			}
		}
		return equivalentSoFar;
	}

	/**
	 * Compares two collections regardless of order (by making sets with their elements and comparing them).
	 */
	public static <T> boolean unorderedEquals(Collection<? extends T> c1, Collection<? extends T> c2) {
		return setFrom(c1).equals(setFrom(c2));
	}

	/**
	 * Makes a list copy of a collection and sort it by the result of {@link Object#toString()} of its elements.
	 */
	public static <T> List<T> sortByString(Collection<? extends T> c) {
		List<T> result = listFrom(c);
		result.sort((t1, t2) -> t1.toString().compareTo(t2.toString()));
		return result;
	}
	
	public static <T> void forEach(Iterable<? extends T> iterable, Procedure<? super T> procedure) {
		for (T e : iterable) {
			procedure.apply(e);
		}
	}

	private static double[] bases = {1.0, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, 10000000.0, 100000000.0, 1000000000.0};

	public static double round(double d, int decimalPlaces) {
		double base;
		if (decimalPlaces < bases.length) {
			base = bases[decimalPlaces];
		}
		else {
			base = pow(10.0, decimalPlaces);
		}
		return Math.round(d*base)/base;
	}
}
