package dk.webbies.tajscheck.tajstester;

public class TypeViolation {
    final public String message;
    final public String path;
    TypeViolation(String message, String path){
        this.message = message;
        this.path = path;
    }

    @Override
    public String toString() {
        return message + " in test " + path;
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

