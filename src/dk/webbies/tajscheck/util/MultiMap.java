package dk.webbies.tajscheck.util;

import java.util.Collection;
import java.util.Set;

/**
 * Created by erik1 on 19-10-2016.
 */
public interface MultiMap <K, T> {
    void put(K key, T value);

    Collection<T> get(K key);

    Collection<T> remove(K key);

    Set<K> keySet();
}
