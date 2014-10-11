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
 * Neither the name of the aic-expresso nor the names of its
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
package com.sri.ai.util.collect;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.Beta;

/**
 * A partial implementation of {@link StackedMap}.
 * 
 * IMPORTANT: this currently does not fulfill the Map contract perfectly because keySet() and entrySet()
 * are NOT backed by the map.
 *
 * @author braz
 * 
 * @param <K>
 * 			the type of the keys.
 * @param <V>
 *          the type of the values.
 */
@Beta
public abstract class AbstractStackedMap<K, V> implements StackedMap<K, V> {
	/** Top map. */
	protected Map<K, V> top = null;

	/** Base map. */
	protected Map<K, V> base = null;

	abstract Map<K, V> makeMap();

	abstract Set<K> makeKSet();

	public AbstractStackedMap() {
		top = makeMap();
	}

	/**
	 * Constructor receiving a base map.
	 * 
	 * @param base
	 *            the base Map that this Map stacks itself on top of.
	 */
	public AbstractStackedMap(Map<K, V> base) {
		this();
		this.base = base;
	}

	/**
	 * Constructor receiving top and base maps
	 * 
	 * @param top
	 *            the top Map that this Map has on its stack.
	 * @param base
	 *            the base Map that this Map stacks itself on top of.
	 */
	public AbstractStackedMap(Map<K, V> top, Map<K, V> base) {
		this.top  = top;
		this.base = base;
	}

	/* (non-Javadoc)
	 * @see com.sri.ai.util.collect.StackedMap#getTop()
	 */
	@Override
	public Map<K, V> getTop() {
		return top;
	}

	/* (non-Javadoc)
	 * @see com.sri.ai.util.collect.StackedMap#setTop(java.util.Map)
	 */
	@Override
	public void setTop(Map<K, V> top) {
		this.top = top;
	}

	/* (non-Javadoc)
	 * @see com.sri.ai.util.collect.StackedMap#getBase()
	 */
	@Override
	public Map<K, V> getBase() {
		return base;
	}

	/* (non-Javadoc)
	 * @see com.sri.ai.util.collect.StackedMap#setBase(java.util.Map)
	 */
	@Override
	public void setBase(Map<K, V> base) {
		this.base = base;
	}

	@Override
	public void clear() {
		top.clear();
		if (base != null) {
			base.clear();
		}
	}

	@Override
	public V put(K key, V value) {
		return top.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		top.putAll(map);
	}

	@Override
	public V get(Object key) {
		V result = top.get(key);
		if (result == null) {
			if ( ! top.containsKey(key) && base != null) {
				result = base.get(key);
			}
		}
		return result;
	}

	@Override
	public boolean containsKey(Object key) {
		boolean result = top.containsKey(key) || (base != null && base.containsKey(key));
		return result;
	}

	@Override
	public boolean containsValue(Object value) {
		boolean result = top.containsValue(value) || (base != null && base.containsValue(value));
		return result;
	}

	@Override
	public boolean isEmpty() {
		boolean result = top.isEmpty() && (base == null || base.isEmpty());
		return result;
	}

	@Override
	public V remove(Object key) {
		if (top.containsKey(key)) {
			return top.remove(key);
		}
		if (base != null) {
			return base.remove(key);
		}
		return null;
	}

	@Override
	public int size() {
		return keySet().size();
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		Map<K, V> result = makeMap();
		result.putAll(top);
		if (base != null) {
			for (Map.Entry<K, V> entry : base.entrySet()) {
				if ( ! top.containsKey(entry.getKey())) {
					result.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return result.entrySet();
	}

	@Override
	public Set<K> keySet() {
		Set<K> result = makeKSet();
		result.addAll(top.keySet());
		if (base != null) {
			result.addAll(base.keySet());
		}
		return result;
	}

	/**
	 * Very slow: copies the whole map into a regular map and returns values().
	 */
	 @Override
	 public Collection<V> values() {
		Map<K, V> map = makeMap();
		map.putAll(this);
		Collection<V> result = map.values();
		return result;
	 }

	 @Override
	 public boolean equals(Object other) {
		 if ( ! Map.class.isInstance(other)) {
			 return false;
		 }
		 @SuppressWarnings("unchecked")
		 Map<K,V> otherMap = (Map<K, V>) other;
		 boolean result = entrySet().equals(otherMap.entrySet());
		 return result;
	 }

	 @Override
	 public int hashCode() {
		 int result = 0;
		 for (Map.Entry<K, V> entry : entrySet()) {
			 result += entry.hashCode();
		 }
		 return result;
	 }

	 @Override
	public String toString() {
		 Map<K, V> auxiliary = new LinkedHashMap<K, V>(this);
		 return auxiliary.toString();
	 }
}
