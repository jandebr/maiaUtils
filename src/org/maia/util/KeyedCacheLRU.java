package org.maia.util;

import java.util.List;
import java.util.Vector;

public class KeyedCacheLRU<K, V> {

	private int capacity;

	private List<K> keysLRU;

	private List<V> valuesLRU;

	public KeyedCacheLRU(int capacity) {
		this.capacity = Math.max(capacity, 1);
		this.keysLRU = new Vector<K>(capacity);
		this.valuesLRU = new Vector<V>(capacity);
	}

	public int size() {
		return getKeysLRU().size();
	}

	public synchronized void clear() {
		getKeysLRU().clear();
		getValuesLRU().clear();
	}

	public synchronized void storeInCache(K key, V value) {
		if (!containsKey(key)) {
			if (size() == getCapacity()) {
				removeFromCache(getLeastRecentlyUsedKey());
			}
			getKeysLRU().add(key);
			getValuesLRU().add(value);
		}
	}

	public synchronized boolean containsKey(K key) {
		return getKeysLRU().contains(key);
	}

	public synchronized V fetchFromCache(K key) {
		V value = null;
		int index = getKeysLRU().indexOf(key);
		if (index >= 0) {
			value = getValuesLRU().get(index);
			if (index < size() - 1) {
				// move to front
				getKeysLRU().add(getKeysLRU().remove(index));
				getValuesLRU().add(getValuesLRU().remove(index));
			}
		}
		return value;
	}

	public synchronized void removeFromCache(K key) {
		int index = getKeysLRU().indexOf(key);
		if (index >= 0) {
			getKeysLRU().remove(index);
			V value = getValuesLRU().remove(index);
			evicted(key, value);
		}
	}

	protected void evicted(K key, V value) {
		// subclasses may override this method to perform any post-eviction operations
	}

	private K getLeastRecentlyUsedKey() {
		return getKeysLRU().isEmpty() ? null : getKeysLRU().get(0);
	}

	public int getCapacity() {
		return capacity;
	}

	private List<K> getKeysLRU() {
		return keysLRU;
	}

	private List<V> getValuesLRU() {
		return valuesLRU;
	}

}