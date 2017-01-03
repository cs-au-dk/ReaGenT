package dk.webbies.tajscheck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
                    "    Here i expected: " + expected + ", but instead i got: \n" +
                    "        typeof: " + typeof + "\n" +
                    "        toString: " + toString + "\n" +
                    "        JSON: " + JSON;
        }
    }

    public static final class RunResult {
        public final List<TypeError> typeErrors;
        public final List<String> errors;

        public RunResult(List<TypeError> typeErrors, List<String> errors) {
            this.typeErrors = typeErrors;
            this.errors = errors;
        }
    }

    public static RunResult parseDriverResult(String output) {
        List<String> split = Arrays.stream(output.split("\n")).filter(line -> !line.trim().isEmpty()).collect(Collectors.toList());
        int errorsIndex = split.indexOf("---- ERRORS ----");
        List<String> errors = new ArrayList<>();
        if (errorsIndex != -1) {
            errors = split.subList(errorsIndex + 1, split.size());
            split = split.subList(0, errorsIndex);
        }
        List<TypeError> typeErrors = new ArrayList<>();

        assert split.get(0).startsWith("Initial random: ");

        if (split.size() == 1) {
            return new RunResult(new ArrayList<>(), errors);
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

        return new RunResult(typeErrors, errors);
    }

    private static TypeError parseSingleResult(List<String> lines) {
        assert lines.size() == 5 || lines.size() == 4;

        String header = lines.get(0);
        assert header.substring(header.lastIndexOf('('), header.length()).startsWith("(iteration: ");
        header = header.substring(0, header.lastIndexOf('(') - 1);
        assert header.lastIndexOf(':') == header.length() - 1;
        String path = header.substring(0, header.length() - 1);

        String expectFinish = ", but instead i got: ";
        assert lines.get(1).endsWith(expectFinish);
        String expectStart = "    Here i expected: ";
        assert lines.get(1).startsWith(expectStart);

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
}
