package dk.webbies.tajscheck.util;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by erik1 on 23-01-2017.
 */
public class MinimizeArray {
    public static <T> T[] minimizeArray(Function<T[], Boolean> test, T[] array) throws IOException {
        for (int sz = array.length >>> 1; sz > 0; sz >>>= 1) {
            System.out.println("  chunk size " + sz);
            int nchunks = (int) Math.floor(array.length / sz);
            for (int i = nchunks - 1; i >= 0; --i) {
                // try removing chunk i
                System.out.println("    chunk #" + i + " size(" + sz + ") arr(" + array.length + ")");
                int lo = i * sz;
                int hi = i == nchunks - 1 ? array.length : (i + 1) * sz;

                // avoid creating empty array if nonempty is set
                if (lo > 0 || hi < array.length) {
                    T[] orgArray = array.clone();
                    array = deleteFromArray(array, lo, hi - lo);

                    if (!test.apply(array)) {
                        array = orgArray;
                    }
                }
            }
        }
        return array;
    }

    private static <T> T[] deleteFromArray(T[] array, int from, int length) {
        List<T> result = new ArrayList<>();

        for (int i = 0; i < Math.min(from, array.length); i++) {
            result.add(array[i]);
        }
        for (int i = from + length; i < array.length; i++) {
            result.add(array[i]);
        }

        return result.toArray((T[]) Array.newInstance(array.getClass().getComponentType(), 0));
    }

}
