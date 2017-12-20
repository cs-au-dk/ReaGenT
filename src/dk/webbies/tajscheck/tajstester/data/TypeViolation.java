package dk.webbies.tajscheck.tajstester.data;

public class TypeViolation {
    final public String message;
    final public String path;
    final public boolean definite;

    public TypeViolation(String message, String path){
        this.message = message;
        this.path = path;
        this.definite = true;
    }

    private TypeViolation(String message, String path, boolean definite) {
        this.message = message;
        this.path = path;
        this.definite = definite;
    }

    public TypeViolation asMaybeViolation() {
        return new TypeViolation(this.message, this.path, false);
    }

    @Override
    public String toString() {
        String prefix = definite ? "Definite: " : "Maybe: ";
        return prefix + message + " in test " + path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeViolation that = (TypeViolation) o;

        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        return path != null ? path.equals(that.path) : that.path == null;
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }
}

