package dk.webbies.tajscheck.tajstester.typeCreator;

import dk.brics.tajs.analysis.HostAPIs;
import dk.brics.tajs.lattice.HostAPI;
import dk.brics.tajs.lattice.HostObject;
import dk.webbies.tajscheck.TypeWithContext;

import java.util.List;

import static dk.brics.tajs.util.Collections.newList;

public class SpecObjects implements HostObject {

    // FIXME ensure proper linking: the actual use of this class is based on *where* the abstraction is needed, and *not* where it is defined!
    public static SpecObjects getObjectAbstraction(List<String> path, TypeWithContext type) {
        return new TypedObject(newList(path), type);
    }

    @Override
    public HostAPI getAPI() {
        return HostAPIs.SPEC;
    }

    public static class TypedObject extends SpecObjects {

        public final List<String> path;
        private TypeWithContext type;

        public TypedObject(List<String> path, TypeWithContext type) {
            this.type = new TypeWithContext(type.getType(), type.getTypeContext().optimizeTypeParameters(type.getType()));
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
            return "TypedObject{" +
                    "path=" + path +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TypedObject that = (TypedObject) o;

            return type != null ? type.equals(that.type) : that.type == null;
        }

        @Override
        public int hashCode() {
            return type != null ? type.hashCode() : 0;
        }
    }
}
