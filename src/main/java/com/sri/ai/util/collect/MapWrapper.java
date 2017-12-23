package com.sri.ai.util.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A Map that wraps a given map. Useful as a basis for classes that need to modify certain behaviors of a given map.
 */
public class MapWrapper<K, V> implements Map<K,V> {

	private Map<K,V> map;
	
	public MapWrapper(Map<K,V> map) {
		this.map = map;
	}
	
	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(Object arg0) {
		return map.containsKey(arg0);
	}

	@Override
	public boolean containsValue(Object arg0) {
		return map.containsValue(arg0);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	@Override
	public V get(Object arg0) {
		return map.get(arg0);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public V remove(Object arg0) {
		return map.remove(arg0);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public V put(K key, V value) {
		return map.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		map.putAll(m);
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}
	
	@Override
	public boolean equals(Object another) {
		return map.equals(another);
	}
	
	@Override
	public int hashCode() {
		return map.hashCode();
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
}