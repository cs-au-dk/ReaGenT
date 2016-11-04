package dk.webbies.tajscheck.util;

import java.util.*;

/**
 * Created by erik1 on 19-10-2016.
 */
public class ArrayListMultimap<K, T> implements MultiMap<K, T> {
    private Map<K, List<T>> map = new HashMap<>();

    @Override
    public void put(K key, T value) {
        if (map.containsKey(key)) {
            map.get(key).add(value);
        } else {
            map.put(key, new ArrayList<>(Collections.singletonList(value)));
        }
    }

    @Override
    public Collection<T> get(K key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public Collection<T> remove(K key) {
        List<T> result = map.remove(key);
        if (result != null) {
            return result;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }
}
