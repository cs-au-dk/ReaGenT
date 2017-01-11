package dk.webbies.tajscheck.util;


import dk.au.cs.casa.typescript.types.*;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by erik1 on 01-09-2015.
 */
public class Util {
    private static final boolean alwaysRecreate = false;
    public static String runNodeScript(String args, int timeout) throws IOException, TimeoutException {
        return runNodeScript(args, null, timeout);
    }

    public static String runNodeScript(String args, File dir) throws IOException {
        try {
            return runNodeScript(args, dir, -1);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static String runScript(String args, int timeout) throws IOException, TimeoutException {
        return runScript(args, null, timeout);
    }

    public static String runScript(String args, File dir, int timeout) throws IOException, TimeoutException {
        if (args.endsWith("\"")) args = args.replace("\"", "");
        Process process = Runtime.getRuntime().exec(args, null, dir);

        CountDownLatch latch = new CountDownLatch(2);
        StreamGobbler inputGobbler = new StreamGobbler(process.getInputStream(), latch);
        StreamGobbler errGobbler = new StreamGobbler(process.getErrorStream(), latch);

        if (timeout > 0) {
            try {
                waitForProcess(process, timeout);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (!errGobbler.getResult().isEmpty()) {
            System.err.println("Error running node script: " + errGobbler.getResult());
//            throw new RuntimeException("Got an error running a node script: " + errGobbler.getResult());
        }

        return inputGobbler.getResult();
    }

    public static String runNodeScript(String args, File dir, int timeout) throws IOException, TimeoutException {
        return runScript("node " + args, dir, timeout);
    }

    private static int waitForProcess(Process process, int timeout)
            throws IOException, InterruptedException, TimeoutException {
        Worker worker = new Worker(process);
        worker.start();
        try {
            worker.join(timeout);
            if (worker.exit != null)
                return worker.exit;
            else
                throw new TimeoutException();
        } catch(InterruptedException ex) {
            worker.interrupt();
            Thread.currentThread().interrupt();
            throw ex;
        } finally {
            process.destroy();
        }
    }

    public static <T> List<T> toTypedList(Iterator keys, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        while (keys.hasNext()) {
            result.add(clazz.cast(keys.next()));
        }
        return result;
    }

    public static <S, T, R> Function<Map.Entry<S,T>,Map.Entry<R, T>> mapKey(Function<S, R> mapper) {
        return (entry) -> new AbstractMap.SimpleEntry<>(mapper.apply(entry.getKey()), entry.getValue());
    }

    public static <S, T, R> Function<Map.Entry<S,T>,Map.Entry<S, R>> mapValue(Function<T, R> mapper) {
        return (entry) -> new AbstractMap.SimpleEntry<>(entry.getKey(), mapper.apply(entry.getValue()));
    }

    private static class Worker extends Thread {
        private final Process process;
        private Integer exit;
        private Worker(Process process) {
            this.process = process;
        }
        public void run() {
            try {
                exit = process.waitFor();
            } catch (InterruptedException ignore) {
                // ignored.
            }
        }
    }

    public static String removeSuffix(String str, String suffix) {
        assert str.endsWith(suffix);
        return str.substring(0, str.length() - suffix.length());
    }

    public static String removePrefix(String str, String prefix) {
        assert str.startsWith(prefix);
        return str.substring(prefix.length(), str.length());
    }

    public static String listToString(List<String> list) {
        if (list.isEmpty()) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            builder.append(", ").append(list.get(i));
        }
        builder.append("]");
        return builder.toString();
    }

    public static <T> int compareStringEntry(Map.Entry<String, T> o1, Map.Entry<String, T> o2) {
        return o1.getKey().compareTo(o2.getKey());
    }

    public static <T> Collection<T> reduceCollection(Collection<T> acc, Collection<T> elems) {
        acc.addAll(elems);
        return acc;
    }

    public static String toPrettyNumber(double d) {
        if (d % 1 == 0 && d < Long.MAX_VALUE) {
            return Long.toString((long) d);
        } else {
            return Double.toString(d);
        }
    }

    public static <T> Stream<Pair<T, Integer>> withIndex(Collection<T> list) {
        return zip(list, IntStream.range(0, list.size()).boxed());
    }

    public static <T, R> Stream<R> withIndex(Collection<T> list, BiFunction<? super T, ? super Integer, ? extends R> zipper) {
        return zip(list.stream(), IntStream.range(0, list.size()).boxed(), zipper);
    }

    public static <T, R> Stream<R> withIndex(Stream<T> list, BiFunction<? super T, ? super Integer, ? extends R> zipper) {
        return withIndex(list.collect(Collectors.toList()), zipper);
    }

    public static <T> Stream<Pair<T, Integer>> withIndex(Stream<T> list) {
        return withIndex(list.collect(Collectors.toList()));
    }

    public static class StreamGobbler extends Thread {
        BufferedInputStream is;
        private CountDownLatch latch;
        private String result;

        public StreamGobbler(InputStream is, CountDownLatch latch) {
            this.is = new BufferedInputStream(is);
            this.latch = latch;
            this.start();
        }

        public String getResult() {
            return result;
        }

        @Override
        public void run() {
            try {
                result = IOUtils.toString(is);
                is.close();
                latch.countDown();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public static String getCachedOrRunNode(String cachePath, List<File> checkAgainst, String nodeArgs) throws IOException {
        return getCachedOrRun(cachePath, checkAgainst, () -> {
            try {
                return Util.runNodeScript(nodeArgs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static String runNodeScript(String nodeArgs) throws IOException {
        try {
            return runNodeScript(nodeArgs, -1);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCachedOrRun(String cachePath, List<File> checkAgainst, Supplier<String> run) throws IOException {
        cachePath = cachePath.replaceAll("/", "");
        cachePath = cachePath.replaceAll(":", "");
         cachePath = cachePath.replaceAll("\\\\", "");

        List<Boolean> exists = checkAgainst.stream().map(File::exists).collect(Collectors.toList());

        if (!checkAgainst.stream().allMatch(File::exists)) {
            throw new RuntimeException("I cannot check against something that doesn't exist.");
        }

        if (!new File("cache/").exists()) {
            //noinspection ResultOfMethodCallIgnored
            new File("cache/").mkdir();
        }

        File cache = new File("cache/" + cachePath);

        boolean recreate = false;
        if (!cache.exists()) {
            recreate = true;
        } else {
            long jsnapLastModified = getLastModified(cache);
            for (File fileToCheckAgainst : checkAgainst) {
                long jsLastModified = getLastModified(fileToCheckAgainst);
                if (jsnapLastModified < jsLastModified) {
                    recreate = true;
                    break;
                }
            }

        }

        //noinspection PointlessBooleanExpression
        if (recreate || alwaysRecreate) {
            System.out.println("Creating " + cache.getPath() + " from scratch.");
            String jsnap = run.get();
            BufferedWriter writer = new BufferedWriter(new FileWriter(cache));
            writer.write(jsnap);
            writer.close();
            return jsnap;
        } else {
            FileReader reader = new FileReader(cache);
            String result = IOUtils.toString(reader);
            reader.close();
            return result;
        }
    }

    // http://stackoverflow.com/questions/12249155/how-to-get-the-last-modified-date-and-time-of-a-directory-in-java#answer-12249411
    private static long getLastModified(File file) {
        if (file == null) {
            return 0;
        }
        if (!file.exists()) {
            return 0;
        }
        if (!file.isDirectory()) {
            return file.lastModified();
        } else {
            File[] files = file.listFiles();
            assert files != null;
            if (files.length == 0) return file.lastModified();
            Arrays.sort(files, (o1, o2) -> {
                return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified()); //latest 1st
            });

            return files[0].lastModified();
        }
    }


    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void runAll(Runnable... runs) throws Throwable {
        CountDownLatch latch = new CountDownLatch(runs.length);
        final Throwable[] exception = {null};
        for (Runnable run : runs) {
            threadPool.submit(() -> {
                try {
                    run.run();
                } catch (Throwable e) {
                    exception[0] = e;
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (exception[0] != null) {
            throw exception[0];
        }
    }

    public static <T> Predicate<T> not(Predicate<T> p) { return o -> !p.test(o); }

    // I would really like to force ColT and ColS to be the same subtype of Collection. But I don't think Java generics can handle that.
    public static <T, S, ColT extends Collection<T>, ColS extends Collection<S>> ColS cast(Class<S> clazz, ColT list) {
        if (list == null) {
            return null;
        }
        for (T t : list) {
            if (!clazz.isInstance(t)) {
                throw new ClassCastException("Cannot cast : " + t + " to class " + clazz.getName());
            }
        }
        //noinspection unchecked
        return (ColS) list;
    }

    public static <T, S> List<S> filter(Class<S> clazz, List<T> list) {
        return list.stream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
    }

    public static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    public static void writeFile(String filename, String data) throws IOException {
        writeFile(filename, data, 0);
    }

    private static void writeFile(String filename, String data, int tries) throws IOException{
        try {
            BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(new File(filename)));
            IOUtils.write(data, fileOut);
            fileOut.close();
        } catch (IOException e) {
            if (tries > 10) {
                throw e;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                throw new RuntimeException(e);
            }

            //noinspection ResultOfMethodCallIgnored
            new File(filename).delete();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                throw new RuntimeException(e);
            }

            writeFile(filename, data, tries + 1);
        }
    }

    // http://stackoverflow.com/questions/17640754/zipping-streams-using-jdk8-with-lambda-java-util-stream-streams-zip#answer-23529010
    public static <A, B, C> Stream<C> zip(Stream<? extends A> a,
                                          Stream<? extends B> b,
                                          BiFunction<? super A, ? super B, ? extends C> zipper) {
        Objects.requireNonNull(zipper);
        @SuppressWarnings("unchecked")
        Spliterator<A> aSpliterator = (Spliterator<A>) Objects.requireNonNull(a).spliterator();
        @SuppressWarnings("unchecked")
        Spliterator<B> bSpliterator = (Spliterator<B>) Objects.requireNonNull(b).spliterator();

        // Zipping looses DISTINCT and SORTED characteristics
        long zipSize = ((aSpliterator.characteristics() & bSpliterator.characteristics() &
                ~(Spliterator.DISTINCT | Spliterator.SORTED) & Spliterator.SIZED) != 0)
                ? Math.min(aSpliterator.getExactSizeIfKnown(), bSpliterator.getExactSizeIfKnown())
                : -1;

        Iterator<A> aIterator = Spliterators.iterator(aSpliterator);
        Iterator<B> bIterator = Spliterators.iterator(bSpliterator);
        Iterator<C> cIterator = new Iterator<C>() {
            @Override
            public boolean hasNext() {
                return aIterator.hasNext() && bIterator.hasNext();
            }

            @Override
            public C next() {
                return zipper.apply(aIterator.next(), bIterator.next());
            }
        };

        Spliterator<C> split = Spliterators.spliterator(cIterator, zipSize, aSpliterator.characteristics() & bSpliterator.characteristics() & ~(Spliterator.DISTINCT | Spliterator.SORTED));
        return (a.isParallel() || b.isParallel())
                ? StreamSupport.stream(split, true)
                : StreamSupport.stream(split, false);
    }

    public static <A, B> List<Pair<A, B>> zip(Collection<? extends A> a, Collection<? extends B> b) {
        Stream<Pair<A, B>> zip = zip(a.stream(), b.stream());
        return zip.collect(Collectors.toList());
    }

    public static <A, B> Stream<Pair<A, B>> zip(Collection<? extends A> a, Stream<? extends B> b) {
        return zip(a.stream(), b);
    }

    public static <A, B> Stream<Pair<A, B>> zip(Stream<? extends A> a, Collection<? extends B> b) {
        return zip(a, b.stream());
    }

    public static <A, B> Stream<Pair<A, B>> zip(Stream<? extends A> a, Stream<? extends B> b) {
        //noinspection Convert2MethodRef
        return zip(a, b, (one,two) -> new Pair<>(one, two));
    }

    public static <E, S extends List<E>, T extends Collection<E>> S reduceList(S acc, T elem) {
        acc.addAll(elem);
        return acc;
    }

    public static <E, S extends Set<E>, T extends Collection<E>> S reduceSet(S acc, T set) {
        acc.addAll(set);
        return acc;
    }

    public static <S, T, T1 extends T, S1 extends S> T getWithDefault(Map<S, T> map, S1 key, T1 defaultValue) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            map.put(key, defaultValue);
            return defaultValue;
        }
    }

    public static <T> void runOnCommon(Set<T> one, Set<T> two, Consumer<T> callback) {
        Set<T> smallSet;
        Set<T> bigSet;
        if (one.size() <= two.size()) {
            smallSet = one;
            bigSet = two;
        } else {
            smallSet = two;
            bigSet = one;
        }

        smallSet.stream().filter(bigSet::contains).forEach(callback::accept);
    }

    public static <T> List<T> intersection(Set<T> one, Set<T> two) {
        ArrayList<T> result = new ArrayList<>();
        runOnCommon(one, two, result::add);
        return result;
    }

    public static <T> List<T> intersection(Collection<T> one, Collection<T> two) {
        return intersection(new HashSet<T>(one), new HashSet<T>(two));
    }

    public static <T> T evaluate(Supplier<T> supplier) {
        return supplier.get();
    }

    public static Predicate<String> isInteger = Pattern.compile("^\\d+$").asPredicate();
    public static boolean isInteger(String str) {
        return isInteger.test(str);
    }

    public static String toFixed(double number, int decimals) {
        return toFixed(number, decimals, '.');
    }

    public static String toFixed(double number, int decimals, char separator) {
        if (Double.isInfinite(number)) {
            return number > 0 ? "Infinite" : "-Infinite";
        } else if (Double.isNaN(number)) {
            return "NaN";
        }
        BigDecimal numberBigDecimal = new BigDecimal(number);
        numberBigDecimal = numberBigDecimal.setScale(decimals, BigDecimal.ROUND_HALF_UP);
        return numberBigDecimal.toString().replace('.', separator);
    }

    public static int lines(String file) throws IOException {
        String contents = Util.readFile(file);
        return contents.split("\n").length;
    }

    @SafeVarargs
    public static <T> Set<T> concatSet(Collection<T>... collections) {
        HashSet<T> result = new HashSet<>();
        for (Collection<T> collection : collections) {
            if (collection == null) {
                continue;
            }
            result.addAll(collection);
        }
        return result;
    }

    @SafeVarargs
    public static <T> List<T> concat(Collection<? extends T>... collections) {
        ArrayList<T> result = new ArrayList<>();
        for (Collection<? extends T> collection : collections) {
            if (collection == null) {
                continue;
            }
            result.addAll(collection);
        }
        return result;
    }

    public static <T> Set<T> createSet(T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }

    public static <T> T gen(Supplier<T> method) {
        return method.get();
    }

    public static <T> int indexOf(T[] list, Predicate<T> test) {
        return indexOf(Arrays.asList(list), test);
    }

    private static <T> int indexOf(List<T> list, Predicate<T> test) {
        return withIndex(list).filter(pair -> test.test(pair.getLeft())).map(Pair::getRight).findFirst().get();
    }
}
