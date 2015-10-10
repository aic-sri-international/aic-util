package com.sri.ai.util.collect;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A {@link Map} implementation that directs methods to another map,
 * which is either a map received at construction, or a copy of it
 * to a new {@link HashMap} made upon the first writing (or potentially writing} operation.
 */
public class CopyOnWriteMap<K, V> implements Map<K, V> {

	private Map<K, V> base;
	private boolean ownsBase;
	
	public CopyOnWriteMap(Map<K, V> base) {
		this.base = base;
		this.ownsBase = false;
	}
	
	private void copy() {
		if ( ! ownsBase) {
			base = new HashMap<K,V>(base);
			ownsBase = true;
		}
	}

	@Override
	public void clear() {
		base = new HashMap<K,V>();
	}

	@Override
	public boolean containsKey(Object arg0) {
		return base.containsKey(arg0);
	}

	@Override
	public boolean containsValue(Object arg0) {
		return base.containsValue(arg0);
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		copy();
		return base.entrySet();
	}

	@Override
	public V get(Object arg0) {
		return base.get(arg0);
	}

	@Override
	public boolean isEmpty() {
		return base.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		copy();
		return base.keySet();
	}

	@Override
	public V put(K arg0, V arg1) {
		copy();
		return base.put(arg0, arg1);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {
		copy();
		base.putAll(arg0);
	}

	@Override
	public V remove(Object arg0) {
		V result = null;
		Map<K,V> newBase = new HashMap<K,V>();
		for (Map.Entry<K,V> entry : base.entrySet()) {
			if (entry.getKey().equals(arg0)) {
				result = entry.getValue();
			}
			else {
				newBase.put(entry.getKey(), entry.getValue());
			}
		}
		base = newBase;
		return result;
	}

	@Override
	public int size() {
		return base.size();
	}

	@Override
	public Collection<V> values() {
		copy();
		return base.values();
	}
}