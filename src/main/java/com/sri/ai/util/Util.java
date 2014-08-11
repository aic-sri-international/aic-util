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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.sri.ai.util.base.BinaryFunction;
import com.sri.ai.util.base.BinaryPredicate;
import com.sri.ai.util.base.NullaryFunction;
import com.sri.ai.util.base.Pair;
import com.sri.ai.util.base.TernaryFunction;
import com.sri.ai.util.collect.SubRangeIterator;
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
	 */
	public static void fatalError(Throwable e) {
		fatalError(e, true);
	}

	/**
	 * Logs a top level message, the error message and stack trace for the given
	 * exception, and exits the program, returning code 1.
	 */
	public static void fatalError(String topLevelMessage, Throwable e) {
		fatalError(topLevelMessage, e, true);
	}

	/**
	 * Logs the error message for the given exception, and optionally logs a
	 * stack trace. Then exits the program with return code 1.
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
	 */
	public static void fatalError(String topLevelMessage, Throwable e,
			boolean trace) {
		if (trace) {
			if (e.getCause() != null) {
				System.err.println(topLevelMessage + "\n" + e.getMessage()
						+ "\n" + join("\n", e.getStackTrace()) + "\n"
						+ e.getCause().getMessage() + "\n"
						+ join("\n", e.getCause().getStackTrace()));
			} 
			else {
				System.err.println(topLevelMessage + "\n" + e.getMessage());
			}
		} 
		else {
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
		} 
		else {
			System.err.println(msg);
		}
		System.exit(1);
	}

	/**
	 * Returns a string with the entire context of an input stream.
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
	 * Returns the string formed by concatenating the two given strings, with a
	 * space in between if both strings are non-empty.
	 */
	public static String join(String str1, String str2) {
		if (str1.length() == 0) {
			return str2;
		}
		if (str2.length() == 0) {
			return str1;
		}

		StringBuilder buf = new StringBuilder(str1);
		buf.append(' ');
		buf.append(str2);
		return buf.toString();
	}

	/**
	 * Returns a string formed by the concatenation of string versions of the
	 * elements in a collection, separated by a given separator.
	 */
	public static String join(String separator, Collection c) {
		Iterator it = c.iterator();
		return join(separator, it);
	}

	/**
	 * Returns a string formed by the concatenation of string versions of the
	 * elements in an iterator's range, separated by a given separator.
	 */
	public static String join(String separator, Iterator it) {
		StringBuilder buffer = new StringBuilder();
		if (it.hasNext()) {
			buffer.append(it.next());
		}
		while (it.hasNext()) {
			buffer.append(separator);
			buffer.append(it.next());
		}
		return buffer.toString();
	}

	/**
	 * Same as {@link #join(String, Iterator)}, with <code>", "</code> for a
	 * separator.
	 */
	public static String join(Iterator it) {
		return join(", ", it);
	}

	/**
	 * Same as {@link #join(String, Collection)}.
	 */
	public static String join(Collection c, String separator) {
		return join(separator, c);
	}

	/**
	 * Calls {@link #join(String, Collection)} with ", " as separator.
	 */
	public static String join(Collection c) {
		return join(", ", c);
	}

	/**
	 * Calls {@link #join(Collection)} on the given array as a collection.
	 */
	public static String join(Object[] a) {
		return join(Arrays.asList(a));
	}

	/**
	 * Calls {@link #join(String, Collection)} on the given array as a
	 * collection.
	 */
	public static String join(String separator, Object[] a) {
		return join(separator, Arrays.asList(a));
	}

	/**
	 * Produces a string with map entry representations separated by a given
	 * entry separator, where entry representations are the key and value
	 * representations separated by a key-value separator.
	 */
	public static String join(String entrySeparator, String keyValueSeparator,
			Map<? extends Object, ? extends Object> map) {
		List<Object> c = new LinkedList<Object>();
		for (Map.Entry<? extends Object, ? extends Object> entry : map.entrySet()) {
			c.add(entry.getKey() + keyValueSeparator + entry.getValue());
		}
		return join(entrySeparator, c);
	}

	/**
	 * Same as {@link #join(String, String, Map)} with key-value separator equal
	 * to <code>" -> "</code>.
	 */
	public static String join(String entrySeparator, Map<? extends Object, ? extends Object> map) {
		return join(entrySeparator, " -> ", map);
	}

	/**
	 * Same as {@link #join(String, String, Map)} with entry separator equal to
	 * <code>", "</code> and key-value separator equal to <code>" -> "</code>.
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

	/** Returns the received arguments in an array. */
	public static Object[] array(Object... elements) {
		return elements;
	}

	/** Returns the received arguments in a linked list. */
	public static <T> List<T> list(T... elements) {
		return new LinkedList<T>(Arrays.asList(elements));
	}

	/** Returns the received arguments in an iterator. */
	public static <T> Iterator<T> iterator(T... elements) {
		return Arrays.asList(elements).iterator();
	}

	/** Returns an empty stack. */
	public static <T> Stack<T> stack() {
		Stack<T> result = new Stack<T>();
		return result;
	}

	/** Returns the received argument in a stack. */
	public static <T> Stack<T> stack(T object) {
		Stack<T> result = new Stack<T>();
		result.push(object);
		return result;
	}

	/** Returns the received arguments in an array list. */
	public static <T> ArrayList arrayList(T... elements) {
		return new ArrayList<T>(Arrays.asList(elements));
	}

	/**
	 * Generates a LinkedList with integers <code>{start, ..., end - 1}</code>,
	 * skipping <code>step</code> values at a time.
	 */
	public static List<Integer> listFromTo(int start, int end, int step) {
		List<Integer> result = new LinkedList<Integer>();
		for (int i = start; i < end; i += step) {
			result.add(i);
		}
		return result;
	}

	public static <T> Set<T> set(T... elements) {
		return new LinkedHashSet<T>(Arrays.asList(elements));
	}

	public static boolean isEven(int number) {
		return number % 2 == 0;
	}

	/**
	 * Returns the received arguments (interpreted as a sequence of key and
	 * value pairs) in a hash map.
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
		int i = 0;
		while (i != keysAndValues.length) {
			result.put((K) keysAndValues[i], (V) keysAndValues[i + 1]);
			i += 2;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> void addToCollectionValuePossiblyCreatingIt(
			Map<K,Collection<V>> mapToCollections, K key, V element, Class newCollectionClass) {
		Collection<V> c = (Collection<V>) mapToCollections.get(key);
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

	/** Adds all elements of iterator's range to collection. */
	public static <T> Collection<T> addAll(Collection<T> c, Iterator<T> i) {
		while (i.hasNext()) {
			c.add(i.next());
		}
		return c;
	}

	/**
	 * Returns value indexed by given key in map, or a default value if that is null.
	 */
	public static <K,V> V getOrUseDefault(Map<K,V> map, K key, V defaultValue) {
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
	 * Returns an Iterator that has just iterated past an element <code>x</code>
	 * of <code>iterable</code> such that
	 * <code>predicate.evaluate(element, x)</code> is true, or <code>null</code>
	 * if there is no such element.
	 */
	public static <E> Iterator<E> find(E element, Iterable<E> iterable,
			BinaryPredicate<E, E> predicate) {
		Iterator<E> i = iterable.iterator();
		boolean foundIt = false;
		while (i.hasNext()
				&& !(foundIt = predicate.apply(element, i.next()))) {
			;
		}
		if (foundIt) {
			return i;
		}
		return null;
	}

	/**
	 * Returns the first element in an iterator's range equal to a given
	 * element.
	 */
	public static <T> T find(T t, Iterator<T> it) {
		while (it.hasNext()) {
			T another = it.next();
			if (t.equals(another)) {
				return another;
			}
		}
		return null;
	}

	/**
	 * Returns the first element in a collection equal to a given element.
	 */
	public static <T> T find(T t, Collection<T> c) {
		return find(t, c.iterator());
	}

	public static <E> boolean removeAnyTwoMatchingElements(Iterable<E> a, Iterable<E> b,
			BinaryPredicate<E, E> predicate) {
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
	 * Adds all elements satisfying a predicate to a given
	 * collection.
	 */
	public static <T> Collection<T> addElementsSatisfying(Iterator<T> i,
			Predicate<T> predicate, Collection<T> result) {
		while (i.hasNext()) {
			T element = i.next();
			if (predicate.apply(element)) {
				result.add(element);
			}
		}
		return result;
	}

	/**
	 * Adds all elements satisfying a predicate to a given result
	 * collection. Returns original collection if all elements satisfied the
	 * predicate, or given result collection otherwise.
	 */
	public static <T> Collection<T> addElementsSatisfying(Collection<T> c,
			Predicate<T> predicate, Collection<T> result) {
		Iterator<T> i = c.iterator();
		boolean someElementDidNotSatisfyPredicate = false;
		while (i.hasNext()) {
			T element = i.next();
			if (predicate.apply(element)) {
				result.add(element);
			} 
			else {
				someElementDidNotSatisfyPredicate = true;
			}
		}
		if (someElementDidNotSatisfyPredicate) {
			return result;
		}
		return c;
	}

	/**
	 * Adds all elements satisfying a predicate to a new linked list.
	 * Returns new linked list if some element did not satisfy the predicate, or
	 * original one otherwise.
	 */
	public static <T> Collection<T> addElementsSatisfying(Collection<T> c,
			Predicate<T> predicate) {
		return addElementsSatisfying(c, predicate, new LinkedList<T>());
	}

	/**
	 * Adds all elements satisfying a predicate to a new linked list.
	 * Returns new linked list if some element did not satisfy the predicate, or
	 * original one otherwise.
	 */
	public static <T> Collection<T> addElementsSatisfying(Iterator<T> i,
			Predicate<T> predicate) {
		return addElementsSatisfying(i, predicate, new LinkedList<T>());
	}

	/**
	 * Returns first element satisfying predicate, also removing it from given
	 * list, or returns <null>.
	 */
	public static <T> T findAndRemoveOrNull(List<T> list, Predicate<T> p) {
		ListIterator<T> iterator = list.listIterator();
		while (iterator.hasNext()) {
			T element = iterator.next();
			if (p.apply(element)) {
				iterator.remove();
				return element;
			}
		}
		return null;
	}

	/**
	 * If there is no element in collection satisfying given predicate, returns
	 * <null>. Otherwise, returns list with elements in iteration order,
	 * excluding the satisfying element.
	 */
	public static <T> List<T> listCopyWithoutSatisfyingElementOrNull(
			Collection<T> c, Predicate<T> p) {
		Pair<T, List<T>> satisfyingElementAndListCopyWithoutIt = findSatisfyingElementAndListCopyWithoutItOrNull(
				c, p);
		return satisfyingElementAndListCopyWithoutIt.second;
	}

	/**
	 * If there is no element in collection satisfying given predicate, returns
	 * <null>. Otherwise, returns pair with found element and a list with
	 * elements in iteration order, excluding the satisfying element.
	 */
	public static <T> Pair<T, List<T>> findSatisfyingElementAndListCopyWithoutItOrNull(
			Collection<T> c, Predicate<T> p) {
		int elementIndex = -1;
		Iterator<T> iterator = c.iterator();
		while (iterator.hasNext()) {
			T element = iterator.next();
			elementIndex++;
			if (p.apply(element)) {
				// there is such an element,
				// so we create another list and add all elements, but found
				// one, to it.
				List<T> copy = makeCopyButForElementAtGivenIndex(c,
						elementIndex);
				Pair<T, List<T>> result = Pair.make(element, copy);
				return result;
			}
		}
		return null;
	}

	/**
	 * Given a collection and an index, returns a list with the collection's
	 * elements in iteration order, excluding the elementIndex-th element.
	 */
	public static <T> List<T> makeCopyButForElementAtGivenIndex(
			Collection<T> c, int elementIndex) {
		List<T> copy = new LinkedList<T>();
		Iterator<T> copyIterator = c.iterator();
		for (int i = 0; copyIterator.hasNext() && i != elementIndex; i++) {
			copy.add(copyIterator.next());
		}
		if (copyIterator.hasNext()) {
			copyIterator.next(); // this skips the i-th element
			while (copyIterator.hasNext()) {
				copy.add(copyIterator.next());
			}
		}
		return copy;
	}

	/**
	 * Destructively determines whether there is a one-to-one matching between
	 * two collections (the iterator of which support <code>remove</code>)
	 * according to a match predicate.
	 */
	public static <E> boolean destructivelyTellsIfThereIsAOneToOneMatching(
			Iterable<E> expected, Iterable<E> actual, BinaryPredicate<E, E> predicate) {
		boolean noLonerFoundSoFar = true;
		while (noLonerFoundSoFar && !isEmpty(expected) && !isEmpty(actual)) {
			noLonerFoundSoFar = removeAnyTwoMatchingElements(expected, actual,
					predicate);
		}
		return isEmpty(expected) && isEmpty(actual);
	}

	/**
	 * Stores iterator's range in a new, empty list and returns it.
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
	 */
	public static <T> List<T> listFrom(T[] array) {
		LinkedList<T> result = new LinkedList<T>();
		for (int i = 0; i != array.length; i++) {
			result.add(array[i]);
		}
		return result;
	}

	/**
	 * Stores iterator's range in a new, empty array list and returns it.
	 */
	public static <T> ArrayList<T> arrayListFrom(Iterator<T> iterator) {
		ArrayList<T> result = new ArrayList<T>();
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
	}

	/**
	 * Stores results of applying a function to an iterator's range in a new,
	 * empty list and returns it.
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
	 * Stores results of applying a function to an iterator's range in a new,
	 * empty list and returns it.
	 */
	public static <F, T> List<T> mapIntoList(
			Collection<? extends F> collection,
			Function<F, T> function) {
		return mapIntoList(collection.iterator(), function);
	}

	/**
	 * Stores results of applying a function to an iterator's range in a new,
	 * empty array list and returns it.
	 */
	public static <F, T> ArrayList<T> mapIntoArrayList(
			Iterator<? extends F> iterator,
			Function<F, T> function) {
		ArrayList<T> result = new ArrayList<T>();
		while (iterator.hasNext()) {
			F nextElement = iterator.next();
			result.add(function.apply(nextElement));
		}
		return result;
	}

	/**
	 * Stores results of applying a function to an iterator's range in a new,
	 * empty array list and returns it.
	 */
	public static <F, T> ArrayList<T> mapIntoArrayList(
			Collection<? extends F> collection,
			Function<F, T> function) {
		ArrayList<T> result = new ArrayList<T>(collection.size());
		for (F nextElement : collection) {
			result.add(function.apply(nextElement));
		}
		return result;
	}
	
	/**
	 * Map the results of a function on the elements of a list to another list,
	 * but only allocating the latter if some result is a distinct instance than its corresponding original element.
	 * If not are, returns the original list (same instance).
	 * This is meant to help prevent unnecessary creation of objects.
	 */
	public static <T> List<T> conservativeMap(List<T> list, Function<T, T> function) {
		List<T> result = null;
		int size = list.size();
		Iterator<T> iterator = list.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			T element = iterator.next();
			T mapped = function.apply(element);
			result = store(list, result, element, mapped, i, size);
			i++;
		}
		return result;
	}
	
	private static <F, T> List<T> store(List<T> original, List<T> result, F element, T mappedElement, int i, int size) {
		if (mappedElement != element) {
			if (result == null) {
				result = new ArrayList<T>(original);
			}
			result.set(i, mappedElement);
		}
		return result;
	}

	public static <F, T> List<T> mapIntoList(F[] array, Function<F, T> function) {
		return mapIntoList(Arrays.asList(array), function);
	}

	/**
	 * Stores the results of applying a function to the elements of a collection to a given, adequately sized, array.
	 */
	public static <F, T> T[] mapIntoArray(Collection<F> collection, Function<F, T> function, T[] result) {
		int i = 0;
		for (F element : collection) {
			T fOfElement = function.apply(element);
			result[i++] = fOfElement;
		}
		return result;
	}
	
	/**
	 * Stores results of applying a given function to a list in an array and returns the array,
	 * returning null if there has been no (identity) changes.
	 */
	public static <T> T[] mapOrNullIfNoChanges(List<T> list, Function<T, T> function) {
		T[] result = null;
		Iterator<T> iterator = list.iterator();
		for (int i = 0; i != list.size(); i++) {
			T element = iterator.next();
			T newElement = function.apply(element);
			if (newElement != element) {
				result = makeSureItsAllocatedAndIsACopyIfNull(result, list);
				result[i] = newElement;
			}
		}
		return result;
	}

	/**
	 * If given array is null, allocates it, copies all elements from list, and returns it.
	 * Otherwise, returns given array.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] makeSureItsAllocatedAndIsACopyIfNull(T[] array, List<T> list) {
		if (array == null) {
			array = (T[]) list.toArray();
		}
		return array;
	}
	
	/**
	 * Indicates whether two collections contain the exact same instances in the same (iterable) order.
	 */
	public static <T> boolean sameInstancesInSameIterableOrder(Collection<T> c1, Collection<T> c2) {
		Iterator<T> i1 = c1.iterator();
		Iterator<T> i2 = c2.iterator();
		while (i1.hasNext() && i2.hasNext()) {
			if (i1.next() != i2.next()) {
				return false;
			}
		}
		boolean result = ! i1.hasNext() && ! i2.hasNext(); // only true if they are both done at the same time
		return result;
	}

	/**
	 * Applies a unary function to all elements in an iterator's range, without
	 * collecting results.
	 */
	public static <F> void applyToAll(Function<F, ?> function,
			Iterator<? extends F> iterator) {
		while (iterator.hasNext()) {
			function.apply(iterator.next());
		}
	}

	/**
	 * Applies a unary function to all elements in a collection, without
	 * collecting results.
	 */
	public static <F> void applyToAll(Function<F, ?> function,
			Collection<? extends F> collection) {
		applyToAll(function, collection.iterator());
	}

	/**
	 * Stores iterator's range in a new, empty hash set and returns it.
	 */
	public static <T> Set<T> setFrom(Iterator<? extends T> iterator) {
		HashSet<T> result = new LinkedHashSet<T>();
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
	}

	/**
	 * Stores iterator's range in a new, empty linked hash set and returns it.
	 */
	public static <T> LinkedHashSet<T> linkedHashSetFrom(Iterator<? extends T> iterator) {
		LinkedHashSet<T> result = new LinkedHashSet<T>();
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
	}

	/**
	 * Stores results of applying a function to an iterator's range in a new,
	 * empty hash set and returns it.
	 */
	public static <F, T> Set<T> mapIntoSet(Iterator<? extends F> iterator,
			Function<F, T> function) {
		Set<T> result = new LinkedHashSet<T>();
		while (iterator.hasNext()) {
			result.add(function.apply(iterator.next()));
		}
		return result;
	}

	/**
	 * Stores results of applying a function to an set's elements in a new,
	 * empty hash set and returns it.
	 */
	public static <F, T> Set<T> mapIntoSet(Set<? extends F> set, Function<F, T> function) {
		return mapIntoSet(set.iterator(), function);
	}

	/**
	 * Collects elements in an iterator's range satisfying two different
	 * conditions, returning false if some element does not satisfy either.
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
	 * collections, one with the N first elements satisfying a condition and the other
	 * with the elements not doing so. Returns the index of the first of the n elements
	 * satisfying condition or -1 if there aren't n element satisfying the predicate.
	 */
	public static <E> int collectFirstN(
			Iterator<E> iterator, int n,
			Predicate<E> condition,
			Collection<E> satisfyingCondition, Collection<E> remaining) {
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
			} 
			else {
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
	 * Collects elements in a collection into two different given
	 * collections, one with the N first elements satisfying a condition and the other
	 * with the elements not doing so. Returns the index of the first of the n elements
	 * satisfying condition or -1 if there aren't n element satisfying the predicate.
	 */
	public static <E> int collectFirstN(
			Collection<E> c, int n,
			Predicate<E> condition,
			Collection<E> satisfyingCondition, Collection<E> remaining) {
		int result = collectFirstN(c.iterator(), n, condition, satisfyingCondition, remaining);
		return result;
	}

	/**
	 * Collects elements in an iterator's range into two different given
	 * collections, one with the elements satisfying a condition and the other
	 * with the elements not doing so. Returns the index of the first element
	 * satisfying condition or -1 if there is none.
	 */
	public static <E> int collect(Iterator<E> iterator,
			Collection<E> satisfyingCondition, Predicate<E> condition,
			Collection<E> remaining) {
		int i = 0;
		int result = -1;
		while (iterator.hasNext()) {
			E object = iterator.next();
			if (condition.apply(object)) {
				satisfyingCondition.add(object);
				if (result == -1) {
					result = i;
				}
			} 
			else {
				remaining.add(object);
			}
			i++;
		}
		return result;
	}

	/**
	 * Collects elements in a collection into two different given collections,
	 * one with the elements satisfying a condition and the other with the
	 * elements not doing so. Returns the index of the first element satisfying
	 * condition.
	 */
	public static <E> int collect(Collection<E> collection,
			Collection<E> satisfyingCondition, Predicate<E> condition,
			Collection<E> remaining) {
		return collect(collection.iterator(), satisfyingCondition, condition,
				remaining);
	}

	/**
	 * Collects elements in a collection satisfying a given predicate into a
	 * given collection, returning the latter.
	 */
	public static <T> Collection<T> collect(Collection<T> collection,
			Collection<T> collected, Predicate<T> predicate) {
		return collect(collection.iterator(), collected, predicate);
	}

	/**
	 * Collects elements in an iterator's range satisfying a given predicate
	 * into a given collection, returning the latter.
	 */
	public static <T> Collection<T> collect(Iterator<T> iterator,
			Collection<T> collected, Predicate<T> predicate) {
		while (iterator.hasNext()) {
			T element = iterator.next();
			if (predicate.apply(element)) {
				collected.add(element);
			}
		}
		return collected;
	}

	/**
	 * Collects elements in a collection satisfying a given predicate into a new
	 * linked list and returns it.
	 */
	public static <T> List<T> collectToList(Collection<T> collection, Predicate<T> predicate) {
		return (List<T>) collect(collection, new LinkedList<T>(), predicate);
	}

	/**
	 * Collects elements in a collection satisfying a given predicate into a new
	 * linked list and returns it.
	 */
	public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {
		return (List<T>) collect(collection, new LinkedList<T>(), predicate);
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
	 */
	public static Rational divisionWithArbitraryPrecisionIfPossible(Rational numerator, Rational denominator) {
		if (denominator.isZero()) {
			return null;
		}
		
		// Note: In the case of Rational as opposed to the previously used BigDecimal,
		// this should always be possible.
		Rational quotient = numerator.divide(denominator);
		return quotient;
	}

	/**
	 * Returns the maximum in range of given iterator over numbers, or <null> if
	 * range is empty.
	 */
	public static Number max(Iterator<? extends Number> numbersIt) {
		if (!numbersIt.hasNext()) {
			return null;
		}
		double max = numbersIt.next().doubleValue();
		while (numbersIt.hasNext()) {
			final double value = numbersIt.next().doubleValue();
			if (value > max) {
				max = value;
			}
		}
		return numberInJustNeededType(max);
	}

	/**
	 * Returns the maximum in collection of numbers, or <null> if collection is
	 * empty.
	 */
	public static Number max(Collection<? extends Number> numbers) {
		return max(numbers.iterator());
	}

	/**
	 * Returns the minimum in range of given iterator over numbers, or <null> if
	 * range is empty.
	 */
	public static Number min(Iterator<Number> numbersIt) {
		if (!numbersIt.hasNext()) {
			return null;
		}
		double min = numbersIt.next().doubleValue();
		while (numbersIt.hasNext()) {
			final double value = numbersIt.next().doubleValue();
			if (value < min) {
				min = value;
			}
		}
		return numberInJustNeededType(min);
	}

	/**
	 * Returns the minimum in collection of numbers, or <null> if collection is
	 * empty.
	 */
	public static Number min(Collection<Number> numbers) {
		return min(numbers.iterator());
	}

	/** Returns the minimum element in a collection according to a comparator. */
	public static <T> T min(Collection<T> c, Comparator<T> comparator) {
		if (c.isEmpty()) {
			return null;
		}
		T min = null;
		Iterator<T> it = c.iterator();
		while (it.hasNext()) {
			T element = it.next();
			if (min == null || comparator.compare(element, min) == -1) {
				min = element;
			}
		}
		return min;
	}

	/** Returns the maximum element in a collection according to a comparator. */
	public static <T> T max(Collection<T> c, Comparator<T> comparator) {
		if (c.isEmpty()) {
			return null;
		}
		T max = null;
		Iterator<T> it = c.iterator();
		while (it.hasNext()) {
			T element = it.next();
			if (max == null || comparator.compare(element, max) == +1) {
				max = element;
			}
		}
		return max;
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
		return and(booleans.iterator());
	}

	public static Boolean or(Iterator<Boolean> booleansIt) {
		while (booleansIt.hasNext()) {
			if (booleansIt.next()) {
				return true;
			}
		}
		return false;
	}

	public static Boolean or(Collection<Boolean> booleans) {
		return or(booleans.iterator());
	}

	/**
	 * A sadly inefficient version of Lisp's cons -- returning a list composed
	 * of first and rest.
	 */
	public static <E> List<E> cons(E first, List<E> rest) {
		@SuppressWarnings("unchecked")
		List<E> result = Util.list(first);
		result.addAll(rest);
		return result;
	}

	/**
	 * Returns a list composed of all elements of given list but the first one.
	 * Throws an exception if list is empty.
	 */
	public static <T> List<T> rest(List<T> list) {
		List<T> result = new LinkedList<T>(list);
		result.remove(0);
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

	/** Indicates whether the elements of two iterators's ranges are equal. */
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
	 */
	public static boolean equals(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		}
		else if (o2 == null) {
			return false;
		}
		return o1.equals(o2);
	}

	/**
	 * Indicates that neither of two objects are <null> and the first equals the
	 * second.
	 */
	public static boolean notNullAndEquals(Object o1, Object o2) {
		if (o1 == null || o2 == null) {
			return false;
		}

		return o1.equals(o2);
	}

	/**
	 * Indicates that two objects are not <null> and not equal.
	 */
	public static boolean notNullAndDistinct(Object o1, Object o2) {
		if (o1 == null || o2 == null) {
			return false;
		}

		return ! o1.equals(o2);
	}

	public static <T> boolean equals(T[] array, List<T> list) {
		boolean result = Arrays.asList(array).equals(list);
		return result;
	}
	
	/**
	 * Indicates whether all elements in an iterator's range equal a given
	 * object.
	 */
	public static <T> boolean allEqual(Iterator<? extends T> iterator, T object) {
		while (iterator.hasNext()) {
			if (!equals(iterator.next(), object)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Indicates whether all elements in iterator's range satisfy the given
	 * predicate.
	 */
	public static <E> boolean forAll(Iterator<E> iterator, Predicate<E> predicate) {
		while (iterator.hasNext()) {
			if (!predicate.apply(iterator.next())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Indicates whether all elements in collection satisfy the given predicate.
	 */
	public static <E> boolean forAll(Collection<E> collection, Predicate<E> predicate) {
		return forAll(collection.iterator(), predicate);
	}

	/**
	 * Indicates whether there is an element in iterator's range that satisfies
	 * the given predicate.
	 */
	public static <T> boolean thereExists(Iterator<T> iterator, Predicate<T> predicate) {
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
	 */
	public static <T> boolean thereExists(Collection<T> collection, Predicate<T> predicate) {
		return thereExists(collection.iterator(), predicate);
	}

	/** Adds all elements of two collections to another. */
	public static <T> Collection<T> union(Collection<T> c1, Collection<T> c2,
			Collection<T> result) {
		for (T element : c1) {
			result.add(element);
		}
		for (T element : c2) {
			result.add(element);
		}
		return result;
	}

	/** Adds all elements of two collections to a new LinkedList. */
	public static <T> List<T> union(Collection<T> c1, Collection<T> c2) {
		return (List<T>) union(c1, c2, new LinkedList<T>());
	}

	/** Adds all elements of given collections to a new LinkedList. */
	public static <T> List<T> addAllToANewList(
			Collection<T>... collections) {
		LinkedList<T> result = new LinkedList<T>();
		for (Collection<T> c : collections) {
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

	/** Returns the last element of a list or null, if empty. */
	public static <T> T getLast(List<T> list) {
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
	 */
	public static <T> List<T> getRest(Iterator<T> i) {
		if (!i.hasNext()) {
			throw new Error("Util.getRest called on empty iterator");
		}
		i.next();
		List<T> result = listFrom(i);
		return result;
	}

	/**
	 * Return all but the first element in a collection, in iteration order, in
	 * a newly made list.
	 */
	public static <T> List<T> getRest(Collection<T> c) {
		return getRest(c.iterator());
	}

	public static <E> E getFirstSatisfyingPredicateOrNull(Iterator<? extends E> i,
			Predicate<E> p) {
		while (i.hasNext()) {
			E o = i.next();
			if (p.apply(o)) {
				return o;
			}
		}
		return null;
	}

	public static <E> E getFirstSatisfyingPredicateOrNull(Collection<? extends E> c,
			Predicate<E> p) {
		return getFirstSatisfyingPredicateOrNull(c.iterator(), p);
	}

	/**
	 * Returns the first result of applying a given function to the elements of a collection,
	 * or <code>null</code> if all such results are <code>null</code>.
	 */
	public static <T1, T2> T2 getFirstNonNullResultOrNull(Collection<T1> c, Function<T1, T2> f) {
		for (T1 t1 : c) {
			T2 result = f.apply(t1);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	public static <E> E findFirst(Collection<? extends E> c, Predicate<E> p) {
		return getFirstSatisfyingPredicateOrNull(c.iterator(), p);
	}

	public static <E> E findFirst(Iterator<? extends E> i, Predicate<E> p) {
		return getFirstSatisfyingPredicateOrNull(i, p);
	}

	public static <E> int getIndexOfFirstSatisfyingPredicateOrMinusOne(
			List<? extends E> c, Predicate<E> p) {
		for (int i = 0; i != c.size(); i++) {
			if (p.apply(c.get(i))) {
				return i;
			}
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

	/** Return's a collection's content in an array. */
	public static Object[] asArray(Collection c) {
		Object[] result = new Object[c.size()];
		int i = 0;
		for (Object o : c) {
			result[i++] = o;
		}
		return result;
	}

	/** Indicates whether given string is capitalized. */
	public static boolean isCapitalized(String string) {
		return Character.isUpperCase(string.charAt(0));
	}

	/**
	 * Indicates whether all elements of collection are instances of a given
	 * class.
	 */
	public static boolean allAreInstancesOf(Collection c, Class clazz) {
		for (Object o : c) {
			if (!clazz.isInstance(o)) {
				return false;
			}
		}
		return true;
	}

	/** Indicates whether two collections intersect. */
	public static <T1, T2> boolean intersect(Collection<T1> c1,
			Collection<T2> c2) {
		Collection smaller;
		Collection larger;

		if (c1.size() < c2.size()) {
			smaller = c1;
			larger = c2;
		} 
		else {
			smaller = c2;
			larger = c1;
		}

		for (Object o : smaller) {
			if (larger.contains(o)) {
				return true;
			}
		}

		return false;
	}

	/** Indicates whether a collection and an iterator's range intersect. */
	public static <T1, T2> boolean intersect(Collection<T1> c, Iterator<T2> i) {
		while (i.hasNext()) {
			T2 t2 = i.next();
			if (c.contains(t2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * A structure for
	 * {@link Util#selectPair(List, Predicate, Predicate, BinaryPredicate)}
	 * and
	 * {@link Util#selectPairInEitherOrder(List, Predicate, Predicate, BinaryPredicate)
	 * results.
	 */
	public static class SelectPairResult<T> {
		public SelectPairResult(T first, T second, int indexOfFirst,
				int indexOfSecond, T satisfiesFirstPredicate,
				T satisfiesSecondPredicate) {
			super();
			this.first = first;
			this.second = second;
			this.indexOfFirst = indexOfFirst;
			this.indexOfSecond = indexOfSecond;
			this.satisfiesFirstPredicate = satisfiesFirstPredicate;
			this.satisfiesSecondPredicate = satisfiesSecondPredicate;
		}

		/** Element appearing first. */
		public T first;

		/** Element appearing second. */
		public T second;

		/** Index of element appearing first. */
		public int indexOfFirst;

		/** Index of element appearing second. */
		public int indexOfSecond;

		/** Element satisfying first predicate. */
		public T satisfiesFirstPredicate;

		/** Element satisfying second predicate. */
		public T satisfiesSecondPredicate;
	}

	/**
	 * Returns the indices of first pair of elements of a list such that each of
	 * them satisfies a respectively given unary predicate, and them both
	 * satisfy a binary predicate, or null if there is no such pair.
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
	 * Like
	 * {@link #selectPair(List, Predicate, Predicate, BinaryPredicate)}
	 * , but the pair may be present in either order. The binary predicate, for
	 * consistency, is always applied to <code>(x1, x2)</code> where
	 * <code>x1</code> is the element satisfying the first unary predicate (as
	 * opposed to being the element that appears first), and where
	 * <code>x2</code> is the element satisfying the second unary predicate (as
	 * opposed to being the element that appears second).
	 */
	public static <T> SelectPairResult<T> selectPairInEitherOrder(List<? extends T> list,
			Predicate<T> unaryPredicate1, Predicate<T> unaryPredicate2,
			BinaryPredicate<T, T> binaryPredicate) {

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
					boolean o2SatisfiesPredicate2 = unaryPredicate2
							.apply(o2);
					if (o2SatisfiesPredicate2) {
						boolean o1AndO2SatisfyBinaryPredicate = binaryPredicate
								.apply(o1, o2);
						if (o1AndO2SatisfyBinaryPredicate) {
							return new SelectPairResult<T>(o1, o2, i, j, o1, o2);
						}
					} 
					else {
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
			} 
			else {
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
	 */
	public static <T> Pair<List<T>, List<T>> slicesBeforeIAndRestWithoutJ(
			List<T> list, int i, int j) {
		Pair<List<T>, List<T>> result = new Pair<List<T>, List<T>>(
				new LinkedList<T>(), new LinkedList<T>());
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
	 * Returns a {@link TreeSet} using given comparator, with elements of given
	 * collection.
	 */
	public static <T> TreeSet<T> treeSet(Collection<T> c,
			Comparator<T> comparator) {
		TreeSet<T> set = new TreeSet<T>(comparator);
		set.addAll(c);
		return set;
	}

	/**
	 * Adds elements contained in c1 but not c2 to a given collection result.
	 */
	public static <T> Collection<T> setDifference(Collection<T> c1,
			Collection<T> c2, Collection<T> result) {
		for (T element : c1) {
			if (!c2.contains(element)) {
				result.add(element);
			}
		}
		return result;
	}

	/**
	 * Adds elements contained in c1 but not c2 to a new linked list and returns
	 * it.
	 */
	public static <T> List<T> setDifference(Collection<T> c1, Collection<T> c2) {
		return (List<T>) setDifference(c1, c2, new LinkedList<T>());
	}

	/**
	 * Adds elements contained in c1 but not c2 to a new linked list and returns
	 * it.
	 */
	public static <T> List<T> subtract(Collection<T> c1, Collection<T> c2) {
		return (List<T>) setDifference(c1, c2, new LinkedList<T>());
	}

	/**
	 * Destructively removes as many instances of each element in l1 as there
	 * are in l2.
	 */
	public static <T> void multiSetDifference(List<T> l1, List<T> l2) {
		for (T e2 : l2) {
			l1.remove(e2);
		}
	}

	/**
	 * Destructively removes common elements of l1 and l2.
	 */
	public static <T> void multiSetSymmetricalDifference(List<T> l1, List<T> l2) {
		// removes elements in l2 from l1, keeping track of which and how many
		// ones.
		List<T> removed = new LinkedList<T>();
		for (T e2 : l2) {
			if (l1.remove(e2)) {
				removed.add(e2);
			}
		}
		// for each element in the original l1 (that is, current l1 + removed),
		// remove it from l2.
		for (T e1 : l1) {
			l2.remove(e1);
		}
		for (T e1 : removed) {
			l2.remove(e1);
		}
	}

	/**
	 * Returns the index of the first element in a list satisfying a predicate,
	 * or -1 if none does.
	 */
	public static <T> int findIndexOfFirstElementSatisfyingPredicateOrMinus1(
			List<? extends T> list, Predicate<T> predicate) {
		Iterator<? extends T> iterator = list.iterator();
		for (int i = 0; i != list.size(); i++) {
			T element = iterator.next(); // we avoid using get(i) since it is
											// more expensive for LinkedList.
			if (predicate.apply(element)) {
				return i;
			}
		}
		return -1;
	}

	public static String camelCaseToSpacedString(String camel) {
		StringBuilder result = new StringBuilder();
		int i = 0;
		while (i < camel.length()) {
			char c = camel.charAt(i);
			if (Character.isUpperCase(c) && i != 0) {
				result.append(' ');
				while (i < camel.length()
						&& Character.isUpperCase(camel.charAt(i))) {
					result.append(Character.toLowerCase(c));
					i++;
				}
			} 
			else {
				result.append(c);
				i++;
			}
		}
		return result.toString();
	}

	/**
	 * Gets a collection and returns it back if there are no repeated elements,
	 * or an ArrayList with unique elements.
	 */
	public static <T> Collection<T> removeRepeatedNonDestructively(
			Collection<T> c) {
		LinkedHashSet<T> s = new LinkedHashSet<T>(c);
		if (s.size() == c.size()) {
			return c;
		}
		return new ArrayList<T>(s);
	}

	public static <T> List<T> removeNonDestructively(List<T> list,
			int excludedIndex) {
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

	/** Returns a new linked list containing the elements of list that do not satisfy a predicate. */
	public static <T> LinkedList<T> removeNonDestructively(List<T> list, Predicate<T> predicate) {
		LinkedList<T> result = new LinkedList<T>();
		for (T element : list) {
			if ( ! predicate.apply(element)) {
				result.add(element);
			}
		}
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
			} 
			else {
				result.add(someElement);
			}
		}
		return result;
	}

	/**
	 * Returns list of results of application of a function to pairs of elements
	 * with same indices on two given lists. This is based on a Haskell function
	 * of same name.
	 */
	public static <F1, F2, T> List<T> zipWith(
			BinaryFunction<F1, F2, T> function, List<F1> list1, List<F2> list2) {
		List<T> result = new LinkedList<T>();
		Iterator<F1> i1 = list1.iterator();
		Iterator<F2> i2 = list2.iterator();
		while (i1.hasNext()) {
			F1 t1 = i1.next();
			F2 t2 = i2.next();
			T application = function.apply(t1, t2);
			result.add(application);
		}
		return result;
	}

	/**
	 * Returns list of results of application of a function to triples of
	 * elements with same indices on three given lists. This is based on a
	 * Haskell function of same name.
	 */
	public static <F1, F2, F3, T> List<T> zip3With(
			TernaryFunction<F1, F2, F3, T> function, 
			List<F1> list1, List<F2> list2, List<F3> list3) {
		List<T> result = new LinkedList<T>();
		Iterator<F1> i1 = list1.iterator();
		Iterator<F2> i2 = list2.iterator();
		Iterator<F3> i3 = list3.iterator();
		while (i1.hasNext() && i2.hasNext() && i3.hasNext()) {
			F1 f1 = i1.next();
			F2 f2 = i2.next();
			F3 f3 = i3.next();
			T application = function.apply(f1, f2, f3);
			result.add(application);
		}
		return result;
	}

	/**
	 * Returns an iterator from second element on in a collection (assumes it
	 * contains at least one element).
	 */
	public static <T> Iterator<T> iteratorFromSecondElementOn(Collection<T> c) {
		Iterator<T> i = c.iterator();
		i.next();
		return i;
	}

	/**
	 * Receives an iterator and receives same after iterating once (assumes it
	 * contains a next element).
	 */
	public static <T> Iterator<T> fromSecondElementOn(Iterator<T> i) {
		i.next();
		return i;
	}

	/**
	 * Returns same list if none of its elements gets evaluated to a distinct
	 * object by a replacement function, or a new list equal to the original one
	 * but for having elements replaced by their replacements as provided by
	 * same replacement function.
	 */
	public static <T> List<T> replaceElementsNonDestructively(List<T> list,
			Function<T, T> replacementFunction) {
		List<T> replacementList = null;

		ListIterator<T> it = list.listIterator();
		while (it.hasNext()) {
			T element = it.next();
			T replacement = replacementFunction.apply(element);
			if (replacement != element) {
				replacementList = new ArrayList<T>(list);
				replacementList.set(it.previousIndex(), replacement);
				break;
			}
		}

		if (replacementList == null) {
			return list;
		}

		it = replacementList.listIterator(it.nextIndex());
		while (it.hasNext()) {
			T element = it.next();
			T replacement = replacementFunction.apply(element);
			if (replacement != element) {
				it.set(replacement);
			}
		}

		return replacementList;
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

	/**
	 * Returns a string representing the timestamp's date for the current
	 * locale.
	 */
	public static String dateString(long timestamp) {
		return DateFormat.getTimeInstance().format(new Date(timestamp));
	}

	/**
	 * Returns a string representing the current time's date for the current
	 * locale.
	 */
	public static String dateString() {
		return dateString(System.currentTimeMillis());
	}

	/**
	 * Returns the number of elements in an iterator's range.
	 */
	public static <T> int size(Iterator<T> it) {
		int count = 0;
		while (it.hasNext()) {
			it.next();
			count++;
		}
		return count;
	}

	/**
	 * Returns the number of elements in an iterator's range.
	 */
	public static <T> int count(Iterator<T> it) {
		int count = 0;
		while (it.hasNext()) {
			it.next();
			count++;
		}
		return count;
	}

	/**
	 * Indicates whether two collections have the exact same elements (comparing
	 * by identity, not equality).
	 */
	public static <T> boolean elementsAreTheSame(Collection<? extends T> c1,
			Collection<? extends T> c2) {
		if (c1.size() != c2.size()) {
			return false;
		}

		Iterator<? extends T> it1 = c1.iterator();
		Iterator<? extends T> it2 = c2.iterator();
		while (it1.hasNext()) {
			T e1 = it1.next();
			T e2 = it2.next();
			if (e1 != e2) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a string which is <code>n</code> repetitions of given string.
	 */
	public static String repeat(int n, String string) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i != n; i++) {
			buffer.append(string);
		}
		return buffer.toString();
	}

	/**
	 * Returns an iterator over a sub-range of a base iterator, from the
	 * first-th to the (last-1)-th element.
	 */
	public static <T> Iterator<T> subRangeIterator(Iterator<T> iterator,
			int first, int last) {
		Iterator<T> result = new SubRangeIterator<T>(iterator, first, last);
		return result;
	}

	public static <T> List<T> setNonDestructively(List<T> list, int index,
			T newElement) {
		List<T> result = new LinkedList<T>(list);
		result.set(index, newElement);
		return result;
	}

	/**
	 * Returns a given iterator after iterating over its first element.
	 */
	public static <T> Iterator<T> removeFirst(Iterator<T> iterator) {
		if (iterator.hasNext()) {
			iterator.next();
			return iterator;
		}
		Util.fatalError("Iterator without elements received by Util.removeFirst(Iterator)");
		return null;
	}

	/**
	 * Returns i-th element in iterator's range, or <null> if such element does
	 * not exist.
	 */
	public static <T> T getIthElementInRange(Iterator<T> iterator, int i) {
		while (iterator.hasNext()) {
			T element = iterator.next();
			if (i == 0) {
				return element;
			}
			i--;
		}
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
	 */
	public static int compareIntegersWithMinusOneMeaningInfinite(int limit1, int limit2) {
		if (limit1 == -1) {
			if (limit2 == -1) {
				return 0;
			}
			else {
				return +1;
			}
		}
		else {
			if (limit2 == -1) {
				return -1;
			}
			else {
				return limit1 - limit2;
			}
		}
	}
	
	/**
	 * Indicates whether an iterator's range contains a given element.
	 */
	public static <T> boolean contains(Iterator<T> iterator, T element) {
		while (iterator.hasNext()) {
			if (iterator.next().equals(element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Indicates whether an element appears in a list from a given position.
	 */
	public static <T> boolean listFromIContains(ArrayList<T> list, int i, T element) {
		ListIterator<T> iterator = list.listIterator(i);
		boolean result = contains(iterator, element);
		return result;
	}

	/**
	 * Indicates whether an element appears in a list in a position up to, but different from, i.
	 */
	public static <T> boolean listUpToIExclusiveContains(ArrayList<T> list, int i, T element) {
		Iterator<T> iterator = list.iterator();
		for (int j = 0; iterator.hasNext() && j != i; j++) {
			T element2 = iterator.next();
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


	/** Returns multiple of delta closest to a given number. */
	public static double round(double number, double delta) {
		if (delta == 0) {
			return number;
		}
	
		double numberOfDeltasInNumber = number / delta;
		long   roundedNumberOfDeltasInNumber = Math.round(numberOfDeltasInNumber);
		double result = delta * roundedNumberOfDeltasInNumber;
		return result;
	}

	/** Removes the elements that satisfy a predicate from a list. */
	public static <T> void removeElementsSatisfying(List<? extends T> list, Predicate<T> predicate) {
		ListIterator<? extends T> iterator = list.listIterator();
		while (iterator.hasNext()) {
			T element = iterator.next();
			if (predicate.apply(element)) {
				iterator.remove();
			}
		}
	}

	/** Removes the elements that satisfy a predicate from a set -- works for sets not containing null elements only. */
	public static <T> void removeElementsSatisfying(Set<? extends T> set, Predicate<T> predicate) {
		boolean removed;
		do {
			removed = false;
			T element = getFirstSatisfyingPredicateOrNull(set, predicate);
			if (element != null) {
				set.remove(element);
				removed = true;
			}
		} while (removed);
	}

	/**
	 * Returns the number of occurrences of a substring in a string.
	 */
	public static int numberOfOccurrencesOf(String substring, String string) {
		int result = 0;
		int index = -1;
		while ((index = string.indexOf(substring, index + 1)) != -1) {
			result++;
		}
		return result;
	}

	public static <T> Pair<T, T> pair(T first, T second) {
		return new Pair<T,T>(first, second);
	}

	/**
	 * Returns an object's toString() result, or string "null" if object is null.
	 */
	public static String toStringOrNull(Object object) {
		if (object == null) {
			return "null";
		}
		return object.toString();
	}

	/** Returns +1 if given int is greater than 0, 0 if it is 0, and -1 if it is less than 0. */
	public static int signal(int integer) {
		if (integer == 0) {
			return 0;
		}
		else if (integer > 0) {
			return +1;
		}
		return -1;
	}

	/**
	 * Creates a map from a list of keys and a list of values.
	 */
	public static <K, V> Map<K, V> mapFromListOfKeysAndListOfValues(List<K> keys, List<V> values) {
		if (keys.size() != values.size()) {
			throw new Error("mapFromListOfKeysAndListOfValues requires two lists of same size but got " + keys + " with " + keys.size() + " elements and " + values + " with " + values.size() + " elements.");
		}
		
		LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();
		Iterator<K>   keysIterator =   keys.iterator();
		Iterator<V> valuesIterator = values.iterator();
		while (keysIterator.hasNext()) {
			K key     = keysIterator.next();
			V value = valuesIterator.next();
			result.put(key, value);
		}
		return result;
	}

	/**
	 * Given map1 and map2, returns a new map such that map(K) = map2(map1(K))
	 */
	public static <K, V1, V2> Map<K, V2> composeMaps(Map<K, V1> map1, Map<V1, V2> map2) {
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
	 * Replaces a given instance in a list by a new object.
	 */
	public static <T> void replaceInstanceInList(T instanceToBeReplaced, T newElement, List<T> list) {
		ListIterator<T> iterator = list.listIterator();
		while (iterator.hasNext()) {
			T someInstanceInList = iterator.next();
			if (someInstanceInList == instanceToBeReplaced) {
				iterator.set(newElement);
				break;
			}
		}
	}

    /** Wait for some time, throwing an error in case of an exception. */
	public static void waitOrThrowError(long time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
			throw new Error("Unexpected exception:", e);
		}
	}

    /**
     * Returns the first object in an array that is an instance of a given class.
     */
    public static Object getObjectOfClass(Class clazz, Object[] args) {
	for (Object object : args) {
	    if (clazz.isInstance(object))
		return object;
	}
	return null;
    }

	/**
	 * Incrementally calculates component-wise averages, given previously calculated averages
	 * (out of n numbers) and a list of new numbers.
	 * The average list is filled with the appropriate number of zeros if it is empty.
	 * The result is stored in-place, destroying the previous average list. 
	 */
	static public List incrementalComputationOfComponentWiseAverage(List<Number> average, int n, List newItems) {
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
			double newItem        = ((Double) newItems.get(i)).doubleValue();
			double newAverage     = (currentAverage * n + newItem) / (n + 1); 
			average.set(i, new Double(newAverage));
		}
	
		return average;
	}

	/**
	 * A more general version of {@link #incrementalComputationOfComponentWiseAverage(List<Number>, int, List<Number>)}
	 * that operates on lists of lists of arbitrary depth, including depth 0, that is, on {@link Number}s.
	 * It is in-place and returns <code>average</code> if given objects are lists, or returns a new Number otherwise.
	 */
	@SuppressWarnings("unchecked")
	public static Object incrementalComponentWiseAverageArbitraryDepth(Object average, int n, Object newItems) {
		if (average instanceof Number) {
			return (((Number)average).doubleValue()*n + ((Number)newItems).doubleValue())/(n + 1);
		}
		ListIterator averageIterator = ((List<Number>)average).listIterator();
		ListIterator newItemsIt = ((List)newItems).listIterator();
		while (averageIterator.hasNext()) {
			Object averageElement = averageIterator.next();
			Object newItemsElement = newItemsIt.next();
			Object newAverageElement = incrementalComponentWiseAverageArbitraryDepth(averageElement, n, newItemsElement);
			if (newAverageElement != averageElement) {
				averageIterator.set(newAverageElement);
			}
		}
		return average;
	}

	/**
	 * Given an array <code>a</code>, returns a map from each string in it to the immediately following object.
	 * More precisely, returns a map mapping
	 * each String <code>s</code> in position <code>i</code> of <code>a</code>
	 * to the object in position <code>i+1</code> of <code>a</code>,
	 * ignoring the remaining elements.
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
	 * Returns a {@link NullaryFunction<Iterator<T>>}
	 * that returns a new iterator to the given <code>Collection<T></code>
	 * each time is it invoked. 
	 */
	static public <T> NullaryFunction<Iterator<T>> getIteratorNullaryFunction(final Collection<T> c) {
		return new NullaryFunction<Iterator<T>>() {
			public Iterator<T> apply() {
				return c.iterator();
			}
		};
	}

	/**
	 * Takes a list of lists, a dimension index (0 for rows, 1 for columns) and an index (either row or column index),
	 * and returns the corresponding slice (data[index,*] if dimension is 0, or data[*,index] if dimension is 1).
	 */
	public static <T> List<T> matrixSlice(List<List<T>> data, int dimension, int index) {
		if (dimension == 0) {
			return (List<T>) data.get(index);
		}
		List<T> result = new LinkedList<T>();
		for (Iterator<List<T>> rowIt = data.iterator(); rowIt.hasNext();) {
			List<T> row = rowIt.next();
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
	 * Randomly picks an element of given array using a given {@link Random} number generator.
	 */
	public static Object randomPick(Random random, Object[] items) {
		int index = random.nextInt(items.length);
		Object result = items[index];
		return result;
	}

	/**
	 * Randomly picks <code>n</code> elements (possibly repeated) of given array using
	 * a given {@link Random} number generator.
	 */
	public static ArrayList<Object> randomPick(int n, Random random, Object[] items) {
		ArrayList<Object> result = new ArrayList<Object>(n);
		for (int i = 0; i != n; i++) {
			result.add(randomPick(random, items));
		}
		return result;
	}

	/**
	 * Randomly picks an element of given list ({@link ArrayList}s will be most efficient)
     * using a given {@link Random} number generator.
	 * @param <T>
	 */
	public static <T> T randomPick(Random random, List<T> items) {
		int index = random.nextInt(items.size());
		T result = items.get(index);
		return result;
	}

	/**
	 * Randomly picks <code>n</code> elements (possibly repeated) of given list
	 * ({@link ArrayList}s will be most efficient) using a
	 * given {@link Random} number generator.
	 * @param <T>
	 */
	public static <T> ArrayList<T> randomPick(int n, Random random, List<T> items) {
		ArrayList<T> result = new ArrayList<T>(n);
		for (int i = 0; i != n; i++) {
			result.add(randomPick(random, items));
		}
		return result;
	}

	/** 
	 * Determines whether all elements in collection are {@link Object#equals(Object)},
	 * including <code>null</code>.
	 */
	public static <T> boolean allEqual(Collection<T> collection) {
		boolean result = true;
		Iterator<T> iterator = collection.iterator();
		if (iterator.hasNext()) {
			T previous = iterator.next();
			while (result && iterator.hasNext()) {
				T nextOne = iterator.next();
				if ( ! equals(nextOne, previous)) {
					result = false;
				}
			}
		}

		return result;
	}
}
