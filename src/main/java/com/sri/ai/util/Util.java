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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.sri.ai.util.base.BinaryFunction;
import com.sri.ai.util.base.BinaryPredicate;
import com.sri.ai.util.base.Equals;
import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.base.Pair;
import com.sri.ai.util.base.PairOf;
import com.sri.ai.util.base.TernaryFunction;
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
	 *            the type of the List to be constructed and the elements it is
	 *            to contain.
	 */
	public static <T> List<T> list(T... elements) {
		return new LinkedList<T>(Arrays.asList(elements));
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
	public static <T> ArrayList<T> arrayList(T... elements) {
		return new ArrayList<T>(Arrays.asList(elements));
	}

	/**
	 * Construct an iterator of the given type populated that iterates over the
	 * provided elements.
	 * 
	 * @param elements
	 *            the elements the iterator is to walk over.
	 * @return an iterator over the received arguments.
	 * @param <T>
	 *            the type of the Iterator to be constructed and the elements it
	 *            is to iterate over.
	 */
	public static <T> Iterator<T> iterator(T... elements) {
		return Arrays.asList(elements).iterator();
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

	public static <T> Set<T> set(T... elements) {
		return new LinkedHashSet<T>(Arrays.asList(elements));
	}

	@SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> map(Object... keysAndValues) {
		if (!isEven(keysAndValues.length)) {
			fatalError("Util.map(Object ...) must receive an even number of arguments but received "
					+ keysAndValues.length
					+ ": "
					+ join(";", keysAndValues)
					+ ".");
		}
		Map<K, V> result = new LinkedHashMap<K, V>();
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
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> void addToCollectionValuePossiblyCreatingIt(
			Map<K, Collection<V>> mapToCollections, K key, V element,
			Class newCollectionClass) {
		Collection<V> c = mapToCollections.get(key);
		if (c == null) {
			try {
				c = (Collection<V>) newCollectionClass.newInstance();
				mapToCollections.put(key, c);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
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
				c = (Collection) newCollectionClass.newInstance();
				mapToCollections.put(key, c);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
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
	 */
	public static <T> Collection<T> addAll(Collection<T> c, Iterator<T> i) {
		while (i.hasNext()) {
			c.add(i.next());
		}
		return c;
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

	@SuppressWarnings("unchecked")
	public static <K, V> V getValuePossiblyCreatingIt(Map<K, V> map, K key,
			Class<?> newValueClass) {
		V value = map.get(key);
		if (value == null) {
			try {
				value = (V) newValueClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
				System.exit(-1);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			map.put(key, value);
		}
		return value;
	}

	public static <K, V> V getValuePossiblyCreatingIt(Map<K, V> map, K key,
			Function<K, V> makerFromKey) {
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
	public static <T> Pair<T, List<T>> findSatisfyingElementAndListCopyWithoutItOrNull(
			Collection<T> c, Predicate<T> p) {
		Pair<T, List<T>> result = null;
		final AtomicInteger elementIndex = new AtomicInteger(-1);
		Optional<T> first = c.stream().filter(e -> {
			elementIndex.incrementAndGet();
			return p.apply(e);
		}).findFirst();
		if (first.isPresent()) {
			// we create another list and add all elements, but found one, to
			// it.
			List<T> copy = makeCopyButForElementAtGivenIndex(c,
					elementIndex.intValue());
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
	public static <T> List<T> listFrom(Iterator<T> iterator) {
		LinkedList<T> result = new LinkedList<T>();
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
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
		while (iterator.hasNext()) {
			F nextElement = iterator.next();
			result.add(function.apply(nextElement));
		}

		return result;
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

		ArrayList<T> result = collection
				.stream()
				.map(function::apply)
				.collect(toArrayList(collection.size()));

//		ArrayList<T> result = new ArrayList<T>(collection.size());
//		for (F element : collection) {
//			result.add(function.apply(element));
//		}

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

	public static <F, T> List<T> mapIntoList(F[] array, Function<F, T> function) {
		return mapIntoList(Arrays.asList(array), function);
	}

	/**
	 * Stores results of applying a function to a collection's elements in a
	 * new array and returns it.
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
	public static <F, T> T[] mapIntoArray(
			Collection<? extends F> collection, Function<F, T> function) {

		@SuppressWarnings("unchecked")
		T[] result = (T[]) new Object[collection.size()];
		int i = 0;
		for(F element : collection) {
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
	 * Collects elements in an iterator's range satisfying two different
	 * conditions, returning false if some element does not satisfy either.
	 * 
	 * @param iterator
	 *            an iterator over a collection of elements to be tested.
	 * @param satisfyingCondition1
	 *            populated with elements that satisfy condition1.
	 * @param condition1
	 *            the test to be applied to the iterator's range to be passed in
	 *            order to add elements to the given satisfyingCondition1
	 *            collection.
	 * @param satisfyingCondition2
	 *            populated with elements that satisfy condition2.
	 * @param condition2
	 *            the test to be applied to the iterator's range to be passed in
	 *            order to add elements to the given satisfyingCondition2
	 *            collection.
	 * @return false is some element in the given iterator's range does not
	 *         satisfy both given conditions, true otherwise.
	 * @param <E>
	 *            the type of the elements in the given collections.
	 */
	public static <E> boolean collectOrReturnFalseIfElementDoesNotFitEither(
			Iterator<E> iterator, Collection<E> satisfyingCondition1,
			Predicate<E> condition1, Collection<E> satisfyingCondition2,
			Predicate<E> condition2) {
		while (iterator.hasNext()) {
			E object = iterator.next();
			boolean result1;
			boolean result2;
			if (result1 = condition1.apply(object)) {
				satisfyingCondition1.add(object);
			}
			if (result2 = condition2.apply(object)) {
				satisfyingCondition2.add(object);
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
	 * @param satisfyingCondition1
	 *            populated with elements that satisfy condition1.
	 * @param condition1
	 *            the test to be applied to the elements of the given collection
	 *            to be passed in order to add elements to the given
	 *            satisfyingCondition1 collection.
	 * @param satisfyingCondition2
	 *            populated with elements that satisfy condition2.
	 * @param condition2
	 *            the test to be applied to the elements of the given collection
	 *            to be passed in order to add elements to the given
	 *            satisfyingCondition2 collection.
	 * @return false is some element in the given collection does not satisfy
	 *         both given conditions, true otherwise.
	 * @param <E>
	 *            the type of the elements in the given collections.
	 */
	public static <E> boolean collectOrReturnFalseIfElementDoesNotFitEither(
			Collection<E> collection, Collection<E> satisfyingCondition1,
			Predicate<E> condition1, Collection<E> satisfyingCondition2,
			Predicate<E> condition2) {
		return collectOrReturnFalseIfElementDoesNotFitEither(
				collection.iterator(), satisfyingCondition1, condition1,
				satisfyingCondition2, condition2);
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
	 * @param satisfyingCondition
	 *            those elements from the iterator's range that satisfy the
	 *            condition.
	 * @param remaining
	 *            those elements from the iterator's range that don't satisfy
	 *            the condition.
	 * @return the index of the first of the n elements satisfying the
	 *         condition, -1 if there aren't n elements satisfying the
	 *         condition.
	 * @param <E>
	 *            the type of the elements from the iterator's range.
	 */
	public static <E> int collectFirstN(Iterator<E> iterator, int n,
			Predicate<E> condition, Collection<E> satisfyingCondition,
			Collection<E> remaining) {
		int i = 0;
		int result = -1;
		while (iterator.hasNext()) {
			E object = iterator.next();
			if (n > 0 && condition.apply(object)) {
				satisfyingCondition.add(object);
				n--;
				if (result == -1) {
					result = i;
				}
			} else {
				remaining.add(object);
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
	 * @param satisfyingCondition
	 *            those elements from the collection that satisfy the condition.
	 * @param remaining
	 *            those elements from the collection that don't satisfy the
	 *            condition.
	 * @return the index of the first of the n elements satisfying the
	 *         condition, -1 if there aren't n elements satisfying the
	 *         condition.
	 * @param <E>
	 *            the type of the elements from the input collection.
	 */
	public static <E> int collectFirstN(Collection<E> c, int n,
			Predicate<E> condition, Collection<E> satisfyingCondition,
			Collection<E> remaining) {
		int result = collectFirstN(c.iterator(), n, condition,
				satisfyingCondition, remaining);
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
	 * @param satisfyingCondition
	 *            elements from the iterable that satisfy the given condition
	 *            will be added to this collection.
	 * @param condition
	 *            the predicate used to test the elements in the given iterable.
	 * @param remaining
	 *            elements from the iterable that do not satisfy the given
	 *            condition will be added to this collection.
	 * @return the index of the first element satisfying the condition, -1 if
	 *         none satisfy.
	 * @param <E>
	 *            the type of the elements being collected.
	 */
	public static <E> int collect(Iterable<E> iterable,
			Collection<E> satisfyingCondition, Predicate<E> condition,
			Collection<E> remaining) {
		final AtomicInteger i = new AtomicInteger(0);
		final AtomicInteger result = new AtomicInteger(-1);
		iterable.forEach(e -> {
			if (condition.apply(e)) {
				satisfyingCondition.add(e);
				if (result.intValue() == -1) {
					result.set(i.intValue());
				}
			} else {
				remaining.add(e);
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
	 * @param collected
	 *            elements from the given collection that pass the given
	 *            predicate test will be added to this collection.
	 * @param predicate
	 *            the test to be used to select collect elements from the given
	 *            collection.
	 * @return collected.
	 * @param <E>
	 *            the type of the elements being collected.
	 */
	public static <E> Collection<E> collect(Collection<E> collection,
			Collection<E> collected, Predicate<E> predicate) {
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
	 * @param collected
	 *            elements from the given iterator's range that pass the given
	 *            predicate test will be added to this collection.
	 * @param predicate
	 *            the test to be used to select iterated elements from the given
	 *            iterator.
	 * @return collected.
	 * @param <E>
	 *            the type of the elements being collected.
	 */
	public static <E> Collection<E> collect(Iterator<E> iterator,
			Collection<E> collected, Predicate<E> predicate) {

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
	public static <T> List<T> collectToList(Collection<T> collection,
			Predicate<T> predicate) {
		return (List<T>) collect(collection, new LinkedList<T>(), predicate);
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

	public static Number numberInJustNeededType(double number) {
		if (Math.floor(number) == number) {
			return Integer.valueOf((int) number);
		}
		return new Double(number);
	}

	public static Number sum(Iterator<Number> numbersIt) {
		double sum = 0;
		while (numbersIt.hasNext()) {
			sum += numbersIt.next().doubleValue();
		}
		return numberInJustNeededType(sum);
	}

	public static Number sum(Collection<Number> numbers) {
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

	public static Number product(Iterator<Number> numbersIt) {
		double product = 1;
		while (numbersIt.hasNext()) {
			product *= numbersIt.next().doubleValue();
			if (product == 0) {
				break;
			}
		}
		return numberInJustNeededType(product);
	}

	public static Number product(Collection<Number> numbers) {
		return product(numbers.iterator());
	}

	public static Rational productArbitraryPrecision(Iterator<Number> numbersIt) {
		Rational product = Rational.ONE;
		while (numbersIt.hasNext()) {
			Rational number = (Rational) numbersIt.next();
			product = product.multiply(number);
			if (product.equals(0)) {
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
	 *            the collection to find an element with a minimum function value.
	 * @param function
	 *            the function.
	 * @return the element with a minimum function value,
	 *            or null if the collection is empty.
	 * @param <T>
	 *            the type of the elements in the collection.
	 */
	public static <T> T argmin(Collection<? extends T> c, Function<T, Integer> function) {
		Integer minimum = null;
		T result = null;
		for (T element : c) {
			Integer value = function.apply(element);
			if (minimum == null || value.compareTo(minimum) < 0) {
				minimum = value;
				result = element;
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
	 */
	public static <T> T argmax(Collection<? extends T> c, Function<T, Integer> function) {
		Integer maximum = null;
		T result = null;
		for (T element : c) {
			Integer value = function.apply(element);
			if (maximum == null || value.compareTo(maximum) < 0) {
				maximum = value;
				result = element;
			}
		}
		return result;
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
	public static <E> boolean forAll(Collection<E> collection,
			Predicate<E> predicate) {
		boolean result = collection.stream().allMatch(predicate::apply);
		return result;
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
	public static <E> boolean thereExists(Iterator<E> iterator, Predicate<E> predicate) {
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
	public static <E> boolean thereExists(Collection<E> collection, Predicate<E> predicate) {
		boolean result = collection.stream().anyMatch(predicate::apply);
		return result;
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

	public static <E> List<E> union(Collection<E> c1, Collection<E> c2) {
		List<E> result = new LinkedList<E>();
		result.addAll(c1);
		result.addAll(c2);
		return result;
	}

	/**
	 * Adds all elements of given collections to a new LinkedList.
	 * 
	 * @param collections
	 *            the collections whose elements should be added to the returned
	 *            list.
	 * @return a new Linked List containing all the elements from the given
	 *         collections.
	 * @param <E>
	 *            the type of the collections elements.
	 */
	public static <E> List<E> addAllToANewList(Collection<E>... collections) {
		LinkedList<E> result = new LinkedList<E>();
		for (Collection<E> c : collections) {
			result.addAll(c);
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

	public static <E> E getFirstSatisfyingPredicateOrNull(
			Iterator<? extends E> i, Predicate<E> p) {
		while (i.hasNext()) {
			E o = i.next();
			if (p.apply(o)) {
				return o;
			}
		}
		return null;
	}

	public static <E> E getFirstSatisfyingPredicateOrNull(
			Collection<? extends E> c, Predicate<E> p) {
		return getFirstSatisfyingPredicateOrNull(c.iterator(), p);
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
	public static <A, R> R getFirstNonNullResultOrNull(Collection<A> c,
			Function<A, R> f) {
		R result = null;
		Optional<R> first = c.stream().map(a -> f.apply(a))
				.filter(r -> r != null).findFirst();
		if (first.isPresent()) {
			result = first.get();
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

	public static <T> Set<T> intersection(Collection<T> c1, Collection<T> c2) {
		LinkedHashSet<T> result = new LinkedHashSet<T>();
		for (T element : c1) {
			if (c2.contains(element)) {
				result.add(element);
			}
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
	 * @param <E1>
	 *            the type of the elements in the first collection.
	 * @param <E2>
	 *            the type of the elements in the second collection.
	 */
	public static <E1, E2> boolean intersect(Collection<E1> c1,
			Collection<E2> c2) {
		boolean result;

		// Optimization: Traverse the smaller list.
		if (c1.size() < c2.size()) {
			result = c1.stream().filter(e -> c2.contains(e)).findAny()
					.isPresent();
		} else {
			result = c2.stream().filter(e -> c1.contains(e)).findAny()
					.isPresent();
		}

		return result;
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
	public static <E> Collection<E> setDifference(Collection<E> c1,
			Collection<E> c2, Collection<E> result) {
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
	public static <E> List<E> setDifference(Collection<E> c1, Collection<E> c2) {
		return (List<E>) setDifference(c1, c2, new LinkedList<E>());
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
	public static <E> List<E> subtract(Collection<E> c1, Collection<E> c2) {
		return (List<E>) setDifference(c1, c2, new LinkedList<E>());
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

	public static <T> List<T> removeNonDestructively(List<T> list, int excludedIndex) {
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
	public static <E> LinkedList<E> removeNonDestructively(Collection<E> collection, Predicate<E> predicate) {
		LinkedList<E> result =
				collection.stream()
				.filter(e -> !predicate.apply(e))
				.collect(toLinkedList());

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
	public static <T> List<T> removeNonDestructively(Collection<T> collection, T element) {
		List<T> result = removeNonDestructively(collection, Equals.make(element));
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
	public static <E> boolean contains(Iterator<E> iterator, E element) {
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
	public static <E> boolean listFromIContains(ArrayList<E> list, int i,
			E element) {
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
			E element = getFirstSatisfyingPredicateOrNull(set, predicate);
			if (element != null) {
				set.remove(element);
				removed = true;
			}
		} while (removed);
	}

	public static <T> Pair<T, T> pair(T first, T second) {
		return new Pair<T, T>(first, second);
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
				average.add(new Double(0));
			}
		}

		for (int i = 0; i != newItems.size(); i++) {
			double currentAverage = ((Double) average.get(i)).doubleValue();
			double newItem = ((Double) newItems.get(i)).doubleValue();
			double newAverage = (currentAverage * n + newItem) / (n + 1);
			average.set(i, new Double(newAverage));
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
	 * in this order.
	 * @param clazz the class to be cast to
	 * @param object the object to be cast
	 * @param messageTemplate the message template from which an error message is generated
	 * @return the cast object
	 * @param <T1> object type
	 * @param <T2> class type
	 */
	public static <T1, T2> T2 castOrThrowError(Class<T2> clazz, T1 object, String messageTemplate) {
		T2 result;
		try {
			result = clazz.cast(object);
		}
		catch (ClassCastException e) {
			String message = String.format(messageTemplate, object, clazz.getSimpleName(), object.getClass().getSimpleName());
			throw new Error(message);
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
		collect(collection, positive, predicate, negative);
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
}
