package dk.webbies.tajscheck.tajstester.typeCreator;

import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.util.AnalysisException;

import java.util.*;

import static dk.brics.tajs.util.Collections.*;

public class CanonicalHostObjectLabelPaths {

    private final Map<List<String>, ObjectLabel> paths;

    public CanonicalHostObjectLabelPaths(Collection<ObjectLabel> hostLabels) {
        this.paths = newMap();
        fill(paths, hostLabels);
    }

    private void fill(Map<List<String>, ObjectLabel> paths, Collection<ObjectLabel> labels) {
        for (final ObjectLabel label : labels) {
            // XXX this only works because TAJS by convention makes canonical toString values of the HostObject enums (most of them at least)!
            // e.g. "Object.prototype.propertyIsEnumerable" -> ["Object", "prototype", "propertyIsEnumerable"]
            if (label.getHostObject() == null) {
                continue;
            }
            String pathString = label.getHostObject().toString();
            if (pathString.startsWith("TAJS")) {
                continue;
            }
            List<String> hostObjectPath = newList(Arrays.asList(pathString.split("\\.")));
            if (paths.containsKey(hostObjectPath)) {
                throw new AnalysisException(String.format("Can not add %s at path '%s', it is already used by %s", label, hostObjectPath, paths.get(hostObjectPath)));
            }
            paths.put(hostObjectPath, label);
        }
    }

    public boolean has(Stack<String> path) {
        return paths.containsKey(makeKey(path));
    }

    private List<String> makeKey(Stack<String> path) {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Can not use empty path");
        }
        List<String> key = newList(path);
        // Special case #1: TAJS paths are implicitly rooted at the global object
        if (path.size() > 1 && path.get(0).equals("<the global object>")) {
            key = key.subList(1, key.size());
        }
        return key;
    }

    public ObjectLabel get(Stack<String> path) {
        return paths.get(makeKey(path));
    }

    public Set<List<String>> getPaths() {
        return newSet(paths.keySet());
    }
}
