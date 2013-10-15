package com.sri.ai.util.collect;

import java.util.Map;

/**
 * A Map "on top" of another Map that is used whenever a key is not found.
 */
public interface StackedMap<K, V> extends Map<K,V> {

	public abstract Map<K, V> getTop();

	public abstract void setTop(Map<K, V> top);

	public abstract Map<K, V> getBase();

	public abstract void setBase(Map<K, V> base);

}