package dk.webbies.tajscheck;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 23-11-2016.
 */
public class OutputParser {
    public static final class ParseResult {
        public final String path;
        public String expected;
        public String typeof;
        public String toString;
        public String JSON;

        public ParseResult(String path, String expected, String typeof, String toString, String JSON) {
            this.path = path;
            this.expected = expected;
            this.typeof = typeof;
            this.toString = toString;
            this.JSON = JSON;
        }
    }

    public static List<ParseResult> parseDriverResult(String output) {
        List<String> split = Arrays.stream(output.split("\n")).filter(line -> !line.trim().isEmpty()).collect(Collectors.toList());
        List<ParseResult> result = new ArrayList<>();

        assert split.get(0).startsWith("Initial random: ");

        if (split.size() == 1) {
            return new ArrayList<>();
        }

        List<String> singleResultCollector = new ArrayList<>();

        singleResultCollector.add(split.get(1));
        for (int i = 2; i < split.size(); i++) {
            String line = split.get(i);

            if (!line.startsWith(" ")) {
                result.add(parseSingleResult(singleResultCollector));
                singleResultCollector.clear();
            }
            singleResultCollector.add(line);

        }

        result.add(parseSingleResult(singleResultCollector));

        assert result.stream().allMatch(Objects::nonNull);

        return result;
    }

    private static ParseResult parseSingleResult(List<String> lines) {
        assert lines.size() == 5 || lines.size() == 4;

        assert lines.get(0).lastIndexOf(':') == lines.get(0).length() - 1;
        String path = lines.get(0).substring(0, lines.get(0).length() - 1);

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

        return new ParseResult(path, expected, typeof, toString, JSON);
    }
}
