package dk.webbies.tajscheck.tajstester;

import dk.webbies.tajscheck.testcreator.test.Test;

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
}

