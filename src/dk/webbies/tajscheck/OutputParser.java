package dk.webbies.tajscheck;

import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 23-11-2016.
 */
public class OutputParser {
    public static final class TypeError {
        public final String path;
        public String expected;
        public String typeof;
        public String toString;
        public String JSON;

        public TypeError(String path, String expected, String typeof, String toString, String JSON) {
            this.path = path;
            this.expected = expected;
            this.typeof = typeof;
            this.toString = toString;
            this.JSON = JSON;
        }

        @Override
        public String toString() {
            return path + "\n" +
                    "    Here I expected: " + expected + ", but instead I got: \n" +
                    "        typeof: " + typeof + "\n" +
                    "        toString: " + toString + "\n" +
                    "        JSON: " + JSON;
        }

        public String getPath() {
            return path;
        }
    }

    public static final class RunResult {
        public final List<TypeError> typeErrors;
        public final List<String> errors;
        private final Integer totalTests;
        private final Set<Integer> testsCalled;

        public RunResult(List<TypeError> typeErrors, List<String> errors, Integer totalTests, Set<Integer> testsCalled) {
            this.typeErrors = typeErrors;
            this.errors = errors;
            this.totalTests = totalTests;
            this.testsCalled = testsCalled;
        }

        public List<TypeError> getTypeErrors() {
            return typeErrors;
        }

        public List<String> getErrors() {
            return errors;
        }

        public Integer getTotalTests() {
            return totalTests;
        }

        public Set<Integer> getTestsCalled() {
            return testsCalled;
        }
    }

    public static RunResult parseDriverResult(String output) {
        if (output.isEmpty()) {
            return new RunResult(new ArrayList<>(), new ArrayList<>(), -1, Collections.emptySet());
        }
        List<String> split = Arrays.stream(output.split("\n")).filter(line -> !line.trim().isEmpty()).collect(Collectors.toList());

        Pair<List<String>, Pair<Integer, Set<Integer>>> testStats = extractTestStats(split);
        split = testStats.getLeft();


        int errorsIndex = split.indexOf("---- ERRORS ----");
        List<String> errors = new ArrayList<>();
        if (errorsIndex != -1) {
            errors = split.subList(errorsIndex + 1, split.size());
            split = split.subList(0, errorsIndex);
        }
        List<TypeError> typeErrors = new ArrayList<>();

        assert split.get(0).startsWith("Initial random: ");

        if (split.size() == 1) {
            return new RunResult(new ArrayList<>(), errors, testStats.getRight().getLeft(), testStats.getRight().getRight());
        }

        List<String> singleResultCollector = new ArrayList<>();

        singleResultCollector.add(split.get(1));
        for (int i = 2; i < split.size(); i++) {
            String line = split.get(i);

            if (!line.startsWith(" ")) {
                typeErrors.add(parseSingleResult(singleResultCollector));
                singleResultCollector.clear();
            }
            singleResultCollector.add(line);

        }

        typeErrors.add(parseSingleResult(singleResultCollector));

        assert typeErrors.stream().allMatch(Objects::nonNull);

        return new RunResult(typeErrors, errors, testStats.getRight().getLeft(), testStats.getRight().getRight());
    }

    // Test called:
    // total number of tests:
    private static Pair<List<String>, Pair<Integer, Set<Integer>>> extractTestStats(List<String> split) {
        int totalTests = -1;
        Set<Integer> testsCalled = new HashSet<>();
        List<String> filtered = new ArrayList<>();

        for (String str : split) {
            if (str.startsWith("Test called: ")) {
                int testCalled = Integer.parseInt(Util.removePrefix(str, "Test called: ").trim());
                testsCalled.add(testCalled);
            } else if (str.startsWith("total number of tests: ")) {
                assert totalTests == -1;
                totalTests = Integer.parseInt(Util.removePrefix(str, "total number of tests: ").trim());
            } else {
                filtered.add(str);
            }
        }

        return new Pair<>(filtered, new Pair<>(totalTests, testsCalled));
    }

    private static TypeError parseSingleResult(List<String> lines) {
        if (!(lines.size() == 5 || lines.size() == 4)) {
            System.out.println();
        }
        assert lines.size() == 5 || lines.size() == 4;

        String header = lines.get(0);
        assert header.substring(header.lastIndexOf('('), header.length()).startsWith("(iteration: ");
        if (header.lastIndexOf('(') == -1) {
            throw new RuntimeException();
        }
        header = header.substring(0, header.lastIndexOf('(') - 1);
        assert header.lastIndexOf(':') == header.length() - 1;
        String path = header.substring(0, header.length() - 1);

        String expectStart = "    Here I expected: ";
        assert lines.get(1).startsWith(expectStart);
        String expectFinish = ", but instead I got: ";
        assert lines.get(1).endsWith(expectFinish);

        String expected = lines.get(1).substring(expectStart.length(), lines.get(1).length() - expectFinish.length());

        String typeofPrefix = "        typeof: ";
        assert lines.get(2).startsWith(typeofPrefix);
        String typeof = lines.get(2).substring(typeofPrefix.length(), lines.get(2).length());

        String toStringPrefix = "        toString: ";
        assert lines.get(3).startsWith(toStringPrefix);
        String toString = lines.get(3).substring(toStringPrefix.length(), lines.get(3).length());

        String JSON = null;
        if (lines.size() >= 5) {
            String jsonPrefix = "        JSON: ";
            assert lines.get(4).startsWith(jsonPrefix);
            JSON = lines.get(4).substring(jsonPrefix.length(), lines.get(4).length());
        }

        return new TypeError(path, expected, typeof, toString, JSON);
    }

    public static RunResult combine(List<RunResult> results) {
        results = results.stream().filter(Objects::nonNull).collect(Collectors.toList());

        List<String> errors = results.stream().map(res -> res == null ? new RunResult(Collections.emptyList(), Collections.emptyList(), -1, Collections.emptySet()) : res).map(RunResult::getErrors).reduce(new ArrayList<>(), Util::reduceList).stream().distinct().collect(Collectors.toList());

        List<TypeError> typeErrors = results.stream().map(RunResult::getTypeErrors).reduce(new ArrayList<>(), Util::reduceList).stream().distinct().collect(Collectors.toList());

        int totalTests = results.stream().map(RunResult::getTotalTests).reduce(-1, Math::max);

        Set<Integer> testsCalled = results.stream().map(RunResult::getTestsCalled).reduce(new HashSet<>(), Util::reduceSet);

        return new RunResult(typeErrors, errors, totalTests, testsCalled);
    }
}
