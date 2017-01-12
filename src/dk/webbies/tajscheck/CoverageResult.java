package dk.webbies.tajscheck;

import dk.webbies.tajscheck.util.Pair;
import dk.webbies.tajscheck.util.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by erik1 on 09-01-2017.
 */
public class CoverageResult {
    private final Map<SourceLocation, Integer> statements;
    private final Map<SourceLocation, Collection<Integer>> branches;
    private final Map<SourceLocation, Integer> functions;

    public CoverageResult(Map<SourceLocation, Integer> statements, Map<SourceLocation, Collection<Integer>> branches, Map<SourceLocation, Integer> functions) {
        this.statements = statements;
        this.branches = branches;
        this.functions = functions;
    }

    public double statementCoverage() {
        return (statements.values().stream().filter(n -> n > 0).count() / (statements.size() * 1.0));
    }

    public double functionCoverage() {
        return (functions.values().stream().filter(n -> n > 0).count() / (statements.size() * 1.0));
    }

    public double branchCoverage() {
        int count = 0;
        int total = 0;
        for (Collection<Integer> coverageList : branches.values()) {
            for (Integer integer : coverageList) {
                total++;
                if (integer > 0) {
                    count++;
                }
            }

        }

        return (1.0 * count) / (total);
    }


    @Override
    public String toString() {
        return "CoverageResult{" +
                "statements=" + Util.toFixed(statementCoverage(), 4) +
                ", branches=" + Util.toFixed(branchCoverage(), 4) +
                ", functions=" + Util.toFixed(functionCoverage(), 4) +
                '}';
    }

    static Map<String, CoverageResult> parse(String string) {
        try {
            JSONObject obj = new JSONObject(string);

            Map<String, CoverageResult> result = new HashMap<>();

            for (Map.Entry<String, JSONObject> entry : toMap(obj, JSONObject.class).entrySet()) {
                String str = entry.getKey();

                JSONObject subObj = entry.getValue();

                String name = str.substring(str.lastIndexOf("\\") + 1, str.length());
                result.put(name, parseResult(subObj));
            }

            return result;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static <B> Map<String, B> toMap(JSONObject obj, Class<B> valueClass) throws JSONException {
        Map<String, B> result = new HashMap<>();
        for (String key : Util.toTypedList(obj.keys(), String.class)) {
            result.put(key, valueClass.cast(obj.get(key)));
        }

        return result;
    }

    public Map<String, CoverageResult> split(int split, String jsName, String testFileName) {
        Map<SourceLocation, Integer> firstStatements = new HashMap<>();
        Map<SourceLocation, Collection<Integer>> firstBranches = new HashMap<>();
        Map<SourceLocation, Integer> firstFunctions = new HashMap<>();
        CoverageResult first = new CoverageResult(firstStatements, firstBranches, firstFunctions);
        Map<SourceLocation, Integer> secondStatements = new HashMap<>();
        Map<SourceLocation, Collection<Integer>> secondBranches = new HashMap<>();
        Map<SourceLocation, Integer> secondFunctions = new HashMap<>();
        CoverageResult second = new CoverageResult(secondStatements, secondBranches, secondFunctions);

        for (Map.Entry<SourceLocation, Integer> entry : this.statements.entrySet()) {
            if (entry.getKey().start.line < split) {
                firstStatements.put(entry.getKey(), entry.getValue());
            } else {
                secondStatements.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<SourceLocation, Collection<Integer>> entry : this.branches.entrySet()) {
            if (entry.getKey().start.line < split) {
                firstBranches.put(entry.getKey(), entry.getValue());
            } else {
                secondBranches.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<SourceLocation, Integer> entry : this.functions.entrySet()) {
            if (entry.getKey().start.line < split) {
                firstFunctions.put(entry.getKey(), entry.getValue());
            } else {
                secondFunctions.put(entry.getKey(), entry.getValue());
            }
        }


        Map<String, CoverageResult> resultMap = new HashMap<>();
        resultMap.put(jsName, first);
        resultMap.put(testFileName, second);
        return resultMap;
    }

    public Map<String, CoverageResult> split(Map<String, Pair<Integer, Integer>> splitRules) {
        HashMap<String, CoverageResult> result = new HashMap<>();
        for (Map.Entry<String, Pair<Integer, Integer>> ruleEntry : splitRules.entrySet()) {
            String name = ruleEntry.getKey();
            int start = ruleEntry.getValue().getLeft();
            int end = ruleEntry.getValue().getRight();

            Map<SourceLocation, Integer> statements = new HashMap<>();
            Map<SourceLocation, Collection<Integer>> branches = new HashMap<>();
            Map<SourceLocation, Integer> functions = new HashMap<>();

            for (Map.Entry<SourceLocation, Integer> entry : this.statements.entrySet()) {
                if (entry.getKey().start.line >= start && entry.getKey().start.line < end) {
                    statements.put(entry.getKey(), entry.getValue());
                }
            }

            for (Map.Entry<SourceLocation, Collection<Integer>> entry : this.branches.entrySet()) {
                if (entry.getKey().start.line >= start && entry.getKey().start.line < end) {
                    branches.put(entry.getKey(), entry.getValue());
                }
            }

            for (Map.Entry<SourceLocation, Integer> entry : this.functions.entrySet()) {
                if (entry.getKey().start.line >= start && entry.getKey().start.line < end) {
                    functions.put(entry.getKey(), entry.getValue());
                }
            }

            result.put(name, new CoverageResult(statements, branches, functions));
        }

        return result;
    }

    public static final class SourceLocation {
        public final SourcePosition start;
        public final SourcePosition end;

        public SourceLocation(SourcePosition start, SourcePosition end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return "SourceLocation{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

    public static final class SourcePosition {
        public final int line;
        public final int column;

        public SourcePosition(int line, int column) {
            this.line = line;
            this.column = column;
        }

        @Override
        public String toString() {
            return "SourcePosition{" +
                    "line=" + line +
                    ", column=" + column +
                    '}';
        }
    }

    private static CoverageResult parseResult(JSONObject obj) throws JSONException {
        Map<Integer, Collection<Integer>> branchesCovered = new HashMap<>();
        {
            JSONObject branchObject = obj.getJSONObject("b");
            for (Map.Entry<String, JSONArray> entry : toMap(branchObject, JSONArray.class).entrySet()) {
                JSONArray value = entry.getValue();
                ArrayList<Integer> thisBranchCoverage = new ArrayList<>();
                for (int i = 0; i < value.length(); i++) {
                    thisBranchCoverage.add(value.getInt(i));
                }

                branchesCovered.put(Integer.parseInt(entry.getKey()), thisBranchCoverage);
            }
        }

        Map<String, JSONObject> branchesObject = toMap(obj.getJSONObject("branchMap"), JSONObject.class);

        Map<SourceLocation, Collection<Integer>> branches = branchesCovered.entrySet().stream().map(Util.mapKey(key -> toSourceLocation(branchesObject.get(key.toString())))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        Map<Integer, Integer> statementsCovered = new HashMap<>();
        JSONObject statementObject = obj.getJSONObject("s");
        for (Map.Entry<String, Integer> entry : toMap(statementObject, Integer.class).entrySet()) {
            statementsCovered.put(Integer.parseInt(entry.getKey()), entry.getValue());
        }

        Map<String, JSONObject> statementObjects = toMap(obj.getJSONObject("statementMap"), JSONObject.class);

        Map<SourceLocation, Integer> statements = statementsCovered.entrySet().stream().map(Util.mapKey(key -> toSourceLocation(statementObjects.get(key.toString())))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        Map<Integer, Integer> functionsCovered = new HashMap<>();
        {
            JSONObject functionObject = obj.getJSONObject("f");
            for (Map.Entry<String, Integer> entry : toMap(functionObject, Integer.class).entrySet()) {
                functionsCovered.put(Integer.parseInt(entry.getKey()), entry.getValue());
            }
        }

        Map<String, JSONObject> functionsObject = toMap(obj.getJSONObject("fnMap"), JSONObject.class);

        Map<SourceLocation, Integer> functions = functionsCovered.entrySet().stream().map(Util.mapKey(key -> toSourceLocation(functionsObject.get(key.toString())))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        return new CoverageResult(statements, branches, functions);
    }

    private static SourceLocation toSourceLocation(JSONObject object) {
        try {
            if (object.has("loc")) {
                return toSourceLocation(object.getJSONObject("loc"));
            }
            if (object.has("line")) {
                JSONArray arr = object.getJSONArray("locations");
                List<SourceLocation> locations = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    locations.add(toSourceLocation(arr.getJSONObject(i)));
                }

                Comparator<SourcePosition> comparePositions = (a, b) -> {
                    if (a.line < b.line) {
                        return -1;
                    } else if (a.line > b.line) {
                        return 1;
                    }
                    if (a.column < b.column) {
                        return -1;
                    } else if (a.column > b.column) {
                        return 1;
                    }
                    return 0;
                };
                SourcePosition start = locations.stream().min(Comparator.comparing(a -> a.start, comparePositions)).get().start;

                SourcePosition end = locations.stream().min(Comparator.comparing(a -> a.end, comparePositions)).get().end;
                return new SourceLocation(start, end);
            } else {
                return new SourceLocation(toSourcePosition(object.getJSONObject("start")), toSourcePosition(object.getJSONObject("end")));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private static SourcePosition toSourcePosition(JSONObject obj) throws JSONException {
        return new SourcePosition(obj.getInt("line"), obj.getInt("column"));
    }
}
