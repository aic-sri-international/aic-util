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
package com.sri.ai.test.util.collect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.Assert;

import org.junit.Test;

import com.sri.ai.util.collect.CartesianProductEnumeration;

public class CartesianProductEnumerationTest {

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalCartesianProductEnumeration1() {
		// No Elements defined
		new CartesianProductEnumeration<String>(new ArrayList<List<String>>());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalCartesianProductEnumeration2() {

		List<List<String>> listOfListsOfElements = new ArrayList<List<String>>();
		List<String> elementList1 = new ArrayList<String>();
		elementList1.add("list_1_Element_1");
		elementList1.add("list_1_Element_2");
		// Forgot to put any elements in the second list
		List<String> elementList2 = new ArrayList<String>();
		
		listOfListsOfElements.add(elementList1);
		listOfListsOfElements.add(elementList2);
		new CartesianProductEnumeration<String>(listOfListsOfElements);
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testNoSuchElementException() {

		List<List<String>> listOfListsOfElements = new ArrayList<List<String>>();
		List<String> elementList1 = new ArrayList<String>();
		elementList1.add("list_1_Element_1");
		elementList1.add("list_1_Element_2");
		List<String> elementList2 = new ArrayList<String>();
		elementList2.add("list_2_Element_1");
		elementList2.add("list_2_Element_2");
		
		listOfListsOfElements.add(elementList1);
		listOfListsOfElements.add(elementList2);
		
		CartesianProductEnumeration<String> cpe = new CartesianProductEnumeration<String>(listOfListsOfElements);
		cpe.nextElement();
		cpe.nextElement();
		cpe.nextElement();
		cpe.nextElement();
		// This should cause the exception as will have exceeded the cross product
		cpe.nextElement();
	}
	
	@Test
	public void testSizeOfCrossProductEnumeration() {

		List<List<String>> listOfListsOfElements = new ArrayList<List<String>>();
		List<String> elementList1 = new ArrayList<String>();
		elementList1.add("list_1_Element_1");
		elementList1.add("list_1_Element_2");
		elementList1.add("list_1_Element_3");
		List<String> elementList2 = new ArrayList<String>();
		elementList2.add("list_2_Element_1");
		elementList2.add("list_2_Element_2");
		
		listOfListsOfElements.add(elementList1);
		listOfListsOfElements.add(elementList2);
		
		CartesianProductEnumeration<String> cpe = new CartesianProductEnumeration<String>(listOfListsOfElements);
		
		Assert.assertEquals(6, cpe.size().intValue());
	}
	
	@Test
	public void testEnumerateFastestFromRightToLeft() {
		List<List<String>> listOfListsOfElements = new ArrayList<List<String>>();
		List<String> elementList1 = new ArrayList<String>();
		elementList1.add("list_1_Element_1");
		elementList1.add("list_1_Element_2");
		List<String> elementList2 = new ArrayList<String>();
		elementList2.add("list_2_Element_1");
		elementList2.add("list_2_Element_2");
		
		listOfListsOfElements.add(elementList1);
		listOfListsOfElements.add(elementList2);
		
		CartesianProductEnumeration<String> cpe = new CartesianProductEnumeration<String>(listOfListsOfElements);
		
		List<String> row = cpe.nextElement();
		Assert.assertEquals(Arrays.asList("list_1_Element_1", "list_2_Element_1"), row);
		row = cpe.nextElement();
		Assert.assertEquals(Arrays.asList("list_1_Element_1", "list_2_Element_2"), row);
		row = cpe.nextElement();
		Assert.assertEquals(Arrays.asList("list_1_Element_2", "list_2_Element_1"), row);
		row = cpe.nextElement();
		Assert.assertEquals(Arrays.asList("list_1_Element_2", "list_2_Element_2"), row);
		
		Assert.assertFalse(cpe.hasMoreElements());
	}
	
	@Test
	public void testEnumerateFastestFromLeftToRight() {
		List<List<String>> listOfListsOfElements = new ArrayList<List<String>>();
		List<String> elementList1 = new ArrayList<String>();
		elementList1.add("list_1_Element_1");
		elementList1.add("list_1_Element_2");
		List<String> elementList2 = new ArrayList<String>();
		elementList2.add("list_2_Element_1");
		elementList2.add("list_2_Element_2");
		
		listOfListsOfElements.add(elementList1);
		listOfListsOfElements.add(elementList2);
		
		CartesianProductEnumeration<String> cpe = new CartesianProductEnumeration<String>(listOfListsOfElements, false);
		
		List<String> row = cpe.nextElement();
		Assert.assertEquals(Arrays.asList("list_1_Element_1", "list_2_Element_1"), row);
		row = cpe.nextElement();
		Assert.assertEquals(Arrays.asList("list_1_Element_2", "list_2_Element_1"), row);
		row = cpe.nextElement();
		Assert.assertEquals(Arrays.asList("list_1_Element_1", "list_2_Element_2"), row);
		row = cpe.nextElement();
		Assert.assertEquals(Arrays.asList("list_1_Element_2", "list_2_Element_2"), row);
		
		Assert.assertFalse(cpe.hasMoreElements());
	}
	
	@Test
	public void testEnumerateWithFixedValue() {
		List<List<String>> listOfListsOfElements = new ArrayList<List<String>>();
		List<String> elementList1 = new ArrayList<String>();
		elementList1.add("list_1_Element_1");
		elementList1.add("list_1_Element_2");
		List<String> elementList2 = new ArrayList<String>();
		// This is equivalent to having a fixed value
		elementList2.add("list_2_Element_1");
		List<String> elementList3 = new ArrayList<String>();
		elementList3.add("list_3_Element_1");
		elementList3.add("list_3_Element_2");
		
		listOfListsOfElements.add(elementList1);
		listOfListsOfElements.add(elementList2);
		listOfListsOfElements.add(elementList3);
		
		CartesianProductEnumeration<String> cpe = new CartesianProductEnumeration<String>(listOfListsOfElements);
		
		List<String> row = cpe.nextElement();
		Assert.assertEquals(Arrays.asList("list_1_Element_1", "list_2_Element_1", "list_3_Element_1"), row);
		row = cpe.nextElement();
		Assert.assertEquals(Arrays.asList("list_1_Element_1", "list_2_Element_1", "list_3_Element_2"), row);
		row = cpe.nextElement();
		Assert.assertEquals(Arrays.asList("list_1_Element_2", "list_2_Element_1", "list_3_Element_1"), row);
		row = cpe.nextElement();
		Assert.assertEquals(Arrays.asList("list_1_Element_2", "list_2_Element_1", "list_3_Element_2"), row);
		
		Assert.assertFalse(cpe.hasMoreElements());
	}
}
