package dk.webbies.tajscheck.util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 19-10-2016.
 */
public class ArrayListMultiMap<K, T> implements MultiMap<K, T> {
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

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public Map<K, Collection<T>> toMap() {
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
