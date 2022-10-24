package cc.invictusgames.ilib.timebased;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TimeBasedMap<K, V> implements Map<K, V> {

    private final long expireTime;
    private final Map<K, V> store;
    private final Map<K, Long> timeStamps;

    public TimeBasedMap(long time, TimeUnit timeUnit) {
        this(time, timeUnit, new HashMap<>(), new HashMap<>());
    }

    public TimeBasedMap(long time, TimeUnit timeUnit, Map<K, V> store, Map<K, Long> timeStamps) {
        this.expireTime = timeUnit.toMillis(time);
        if (!store.isEmpty())
            throw new IllegalStateException("Store map must be empty");

        this.store = store;

        if (!timeStamps.isEmpty())
            throw new IllegalStateException("Time stamps map must be empty");

        this.timeStamps = timeStamps;
    }

    @Override
    public int size() {
        validateEntries();
        return store.size();
    }

    @Override
    public boolean isEmpty() {
        validateEntries();
        return store.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        validateEntries();
        return store.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        validateEntries();
        return store.containsValue(o);
    }

    @Override
    public V get(Object o) {
        validateEntries();
        return store.get(o);
    }

    @Override
    public V put(K k, V v) {
        timeStamps.put(k, System.currentTimeMillis());
        return store.put(k, v);
    }

    @Override
    public V remove(Object o) {
        timeStamps.remove(o);
        return store.remove(o);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (K k : map.keySet())
            timeStamps.put(k, System.currentTimeMillis());
        store.putAll(map);
    }

    @Override
    public void clear() {
        timeStamps.clear();
        store.clear();
    }

    @Override
    public Set<K> keySet() {
        validateEntries();
        return store.keySet();
    }

    @Override
    public Collection<V> values() {
        validateEntries();
        return store.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        validateEntries();
        return store.entrySet();
    }

    public long getExpiry(K k) {
        return timeStamps.containsKey(k) ? timeStamps.get(k) + expireTime : -1;
    }

    private void validateEntries() {
        store.entrySet().removeIf(entry -> {
            timeStamps.putIfAbsent(entry.getKey(), System.currentTimeMillis());
            long expiresAt = getExpiry(entry.getKey());
            boolean expired = System.currentTimeMillis() > expiresAt;

            if (expired)
                timeStamps.remove(entry.getKey());

            return expired;
        });
    }
}
