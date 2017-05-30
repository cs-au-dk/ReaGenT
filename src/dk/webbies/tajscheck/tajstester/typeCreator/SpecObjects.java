package dk.webbies.tajscheck.tajstester.typeCreator;

import dk.brics.tajs.analysis.HostAPIs;
import dk.brics.tajs.lattice.HostAPI;
import dk.brics.tajs.lattice.HostObject;

import java.util.LinkedList;
import java.util.List;

import static dk.brics.tajs.util.Collections.newList;

public class SpecObjects implements HostObject {

    // FIXME ensure proper linking: the actual use of this class is based on *where* the abstraction is needed, and *not* where it is defined!

    public static SpecObjects getTupleAbstraction(List<String> path) {
        return new FullPath(path);
    }

    public static SpecObjects getObjectAbstraction(List<String> path) {
        return new FullPath(newList(path));
    }

    @Override
    public HostAPI getAPI() {
        return HostAPIs.SPEC;
    }

    public static class FullPath extends SpecObjects {

        public final List<String> path;

        public FullPath(List<String> path) {
            this.path = path;
        }

        public String asText() {
            StringBuilder ret = new StringBuilder();

            for (String s : path) {
                ret.append("\"" + s + "\", ");
            }
            return ret.toString();
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
        
        public boolean arrayEquals(String[] path) {
        	//if (true) return true;
        	LinkedList<String> other = new LinkedList<>();
        	for (String s : path) {
        		other.add(s);
        	}
        	return this.equals(new FullPath(other));
        	
        }
    }

    public static class ProcessBindingObject extends SpecObjects {

        public final List<String> path;

        public ProcessBindingObject(List<String> path) {
            this.path = path;
        }

        @Override
        public String toString() {
            return "ProcessBindingObject{" +
                    "path=" + path +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ProcessBindingObject that = (ProcessBindingObject) o;

            return path != null ? path.equals(that.path) : that.path == null;
        }
        public boolean arrayEquals(String[] path) {
            //if (true) return true;
            LinkedList<String> other = new LinkedList<>();
            for (String s : path) {
                other.add(s);
            }
            return this.equals(new FullPath(other));

        }
        @Override
        public int hashCode() {
            return path != null ? path.hashCode() : 0;
        }
    }
}
