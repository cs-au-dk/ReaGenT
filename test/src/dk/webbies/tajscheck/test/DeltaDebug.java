package dk.webbies.tajscheck.test;

import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 10-01-2017.
 */
public class DeltaDebug {
    public static void debug(String filePath, BooleanSupplier test) throws IOException {

        if (!test.getAsBoolean()) {
            throw new RuntimeException("Doesn't satisfy condition initially");
        }

        String[] array = Util.readFile(filePath).split(Pattern.quote("\n"));
        array = Arrays.stream(array).map(str -> str.replace("\r", "")).collect(Collectors.toList()).toArray(new String[]{});

        // Kinda copy-pasted from here: https://github.com/wala/jsdelta/blob/master/src/delta_single.js

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
                    String[] orgArray = array.clone();
                    array = deleteFromArray(array, lo, hi - lo);
                    write(filePath, array);

                    if (!test.getAsBoolean()) {
                        // didn't work, need to put it back
                        array = orgArray;
                        write(filePath, array);
                    }
                }
            }
        }

        write(filePath, array);

        System.out.println("Delta debugging complete. ");
    }

    private static String[] deleteFromArray(String[] array, int from, int length) {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < Math.min(from, array.length); i++) {
            result.add(array[i]);
        }
        for (int i = from + length; i < array.length; i++) {
            result.add(array[i]);
        }

        return result.toArray(new String[]{});
    }

    private static void write(String filePath, String[] file) throws IOException {
        Util.writeFile(filePath, String.join("\n", Arrays.asList(file)));
    }


    public static void main(String[] args) throws IOException {
        String file = "test/benchmarks/peerjs/peerjs.js";
        debug(file, () -> {
            try {
                TestParsing.testFile(file);
            } catch (IOException e) {
                throw new RuntimeException();
            } catch (AssertionError error) {
                return true;
            } catch (NullPointerException e) {
                return false;
            }
            return false;
        });
    }
}
