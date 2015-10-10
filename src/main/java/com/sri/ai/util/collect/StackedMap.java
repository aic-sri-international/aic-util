package com.sri.ai.util.collect;

import java.util.Map;

/**
 * A Map based on another Map (the <i>base</i>), and storing only the differences between itself and the base.
 * When an entry is added to this map, it will mask another one in the base map.
 * <p>
 * This is useful when we want a "copy" of a map to which we will add entries, without actually having to copy all original entries.
 * <p>
 * Beware: changes to entries still residing in the base map <i>are</i> reflected in it!
 * <p>
 * IMPORTANT: as of October 2015, implementation {@link AbstractStackedMap} does not reflect changes made through
 * {@link Map#entrySet()}, {@link Map#keySet()}, and {@link Map#values()}.  
 */
public interface StackedMap<K, V> extends Map<K,V> {

	public abstract Map<K, V> getTop();

	public abstract void setTop(Map<K, V> top);

	public abstract Map<K, V> getBase();

	public abstract void setBase(Map<K, V> base);

}