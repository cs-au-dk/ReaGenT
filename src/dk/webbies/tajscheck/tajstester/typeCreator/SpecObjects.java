package dk.webbies.tajscheck.tajstester.typeCreator;

import dk.brics.tajs.analysis.HostAPIs;
import dk.brics.tajs.lattice.HostAPI;
import dk.brics.tajs.lattice.HostObject;
import dk.webbies.tajscheck.TypeWithContext;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static dk.brics.tajs.util.Collections.newList;

public class SpecObjects implements HostObject {

    // FIXME ensure proper linking: the actual use of this class is based on *where* the abstraction is needed, and *not* where it is defined!

    public static SpecObjects getTupleAbstraction(List<String> path, TypeWithContext type) {
        return new FullPath(path, type);
    }

    public static SpecObjects getObjectAbstraction(List<String> path, TypeWithContext type) {
        return new FullPath(newList(path), type);
    }

    @Override
    public HostAPI getAPI() {
        return HostAPIs.SPEC;
    }

    public static class FullPath extends SpecObjects {

        public final List<String> path;
        private TypeWithContext type;

        public FullPath(List<String> path, TypeWithContext type) {
            this.type = type;
            this.path = path;
        }

        public TypeWithContext getType() {
            return type;
        }

        public String asText() {
            return String.join(", ", path);
        }

        @Override
        public String toString() {
            return "FullPath{" +
                    "path=" + path +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final FullPath that = (FullPath) o;

            if (path != null ? !path.equals(that.path) : that.path != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return path != null ? path.hashCode() : 0;
        }
    }
}
