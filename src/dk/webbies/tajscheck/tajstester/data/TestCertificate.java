package dk.webbies.tajscheck.tajstester.data;

import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.Value;
import dk.webbies.tajscheck.testcreator.test.Test;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dk.webbies.tajscheck.util.Util.prettyValue;

public class TestCertificate {
    final String message;
    final Value[] usedValues;
    final Test test;
    final State ownerState;

    public TestCertificate(Test test, String message, Value[] usedValues, State ownerState) {
        this.test = test;
        this.message = message;
        this.usedValues = usedValues;
        this.ownerState = ownerState;
    }

    @Override
    public String toString() {
        String patternString = "\\[[0-9]+\\]";
        Pattern pattern = Pattern.compile(patternString);

        Matcher matcher = pattern.matcher(message);
        int total = 0;
        while (matcher.find())
            total++;

        String m = message;
        for(int i = 0; i < total; i++) {
            m = m.replace("[" + i + "]", prettyValue(usedValues[i], ownerState));
        }
        return test.getClass().getSimpleName() + "(" + test.getPath() +"):" + m;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestCertificate that = (TestCertificate) o;

        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(usedValues, that.usedValues)) return false;
        if (test != null ? !test.equals(that.test) : that.test != null) return false;
        return ownerState != null ? ownerState.equals(that.ownerState) : that.ownerState == null;
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(usedValues);
        result = 31 * result + (test != null ? test.hashCode() : 0);
        result = 31 * result + (ownerState != null ? ownerState.hashCode() : 0);
        return result;
    }
}
