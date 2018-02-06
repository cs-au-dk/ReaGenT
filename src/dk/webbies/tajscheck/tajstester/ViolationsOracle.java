package dk.webbies.tajscheck.tajstester;

import com.google.gson.Gson;
import dk.webbies.tajscheck.benchmark.Benchmark;
import dk.webbies.tajscheck.tajstester.data.TypeViolation;
import dk.webbies.tajscheck.util.Util;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ViolationsOracle {

    private final TypeViolations suppressed = TypeViolations.empty();
    private final TypeViolations toSuppress;

    private ViolationsOracle(TypeViolations toSuppress) {
        this.toSuppress = toSuppress == null ? TypeViolations.empty() : toSuppress;
    }

    public boolean canEmit(TypeViolation v) {
        if (v.path.equals(suppresedViolationMarker.path)) {
            return false;
        }
        TypeViolationSignature signature = getSignature(v);
        if(!toSuppress.contains(signature)) {
            return true;
        } else {
            suppressed.add(signature);
            return false;
        }
    }

    public static final TypeViolation suppresedViolationMarker = TypeViolation.definite(">Is fake", "Should never be reported");

    public boolean isTight() {
        return suppressed.containsAll(toSuppress);
    }

    public Set<TypeViolationSignature> getUnnecessarySuppressions() {
        Set<TypeViolationSignature> to = new HashSet<>(toSuppress.getSuppressions());
        to.removeAll(suppressed.getSuppressions());
        return to;
    }

    public static ViolationsOracle empty() {
        return new ViolationsOracle(TypeViolations.empty());
    }

    public static ViolationsOracle fromJson(Benchmark benchmark) {
        Gson g = new Gson();
        try {
            String inputContent = Util.readFile(benchmark.violationsOracleFile);
            TypeViolations tvs = g.fromJson(inputContent, TypeViolations.class);
            if (tvs != null && !tvs.getSuppressions().isEmpty()) {
                System.out.println("Loaded " + tvs.getSuppressions().size() + " warning suppressions");
            }
            return new ViolationsOracle(tvs);
        }
        catch(IOException e) {
            return new ViolationsOracle(TypeViolations.empty());
        }
    }

    private TypeViolationSignature getSignature(TypeViolation v) {
        return new TypeViolationSignature(v.path);
    }

    private static class TypeViolations {
        private Set<TypeViolationSignature> suppressions = new HashSet<>();

        private TypeViolations() {}

        public TypeViolations add(TypeViolationSignature s) {
            this.suppressions.add(s);
            return this;
        }

        public TypeViolations addPath(String p) {
            this.suppressions.add(new TypeViolationSignature(p));
            return this;
        }

        public static TypeViolations empty() { return new TypeViolations(); }

        public boolean contains(TypeViolationSignature s) { return suppressions.contains(s); }

        public boolean containsAll(TypeViolations c) { return suppressions.containsAll(c.suppressions); }

        public Collection<TypeViolationSignature> getSuppressions() { return suppressions; };
    }

    private static class TypeViolationSignature {
        String path;
        String comment;

        public TypeViolationSignature(String path) {
            this.path = path;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TypeViolationSignature that = (TypeViolationSignature) o;
            return Objects.equals(path, that.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path);
        }

        @Override
        public String toString() {
            return path + "(" + comment + ")";
        }
    }
}
