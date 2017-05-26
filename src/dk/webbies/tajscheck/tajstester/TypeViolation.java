package dk.webbies.tajscheck.tajstester;

import dk.webbies.tajscheck.testcreator.test.Test;

public class TypeViolation {
    final public String message;
    final public Test test;
    TypeViolation(String message, Test t){
        this.message = message;
        this.test = t;
    }

    @Override
    public String toString() {
        return message + " in test " + test;
    }
}

