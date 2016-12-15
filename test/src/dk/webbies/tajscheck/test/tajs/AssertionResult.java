package dk.webbies.tajscheck.test.tajs;

import dk.brics.tajs.lattice.Value;

import java.util.Collection;

/**
 * Created by erik1 on 15-12-2016.
 */
public class AssertionResult {
    BooleanResult result;
    String expected;
    Value actual;

    public enum BooleanResult {
        DEFINITELY_FALSE,
        DEFINITELY_TRUE,
        SOMETIMES_TRUE,
        SOMETIMES_FALSE,
        SOMETIMES_TRUE_SOMETIMES_FALSE,
        ALWAYS_UNKNOWN;

        static BooleanResult parse(Collection<Value> values) {
            boolean hasBeenFalse = false;
            boolean hasBeenTrue = false;
            boolean hasBeenUnknown = false;
            for (Value value : values) {
                if (value.isMaybeTrueButNotFalse()) {
                    hasBeenTrue = true;
                } else if (value.isMaybeFalseButNotTrue()) {
                    hasBeenFalse = true;
                } else {
                    assert value.isMaybeAnyBool();
                    hasBeenUnknown = true;
                }
            }
            if (!hasBeenFalse && !hasBeenTrue) {
                return ALWAYS_UNKNOWN;
            }
            if (hasBeenTrue && hasBeenFalse) {
                return SOMETIMES_TRUE_SOMETIMES_FALSE;
            }
            if (!hasBeenUnknown && hasBeenTrue) {
                return DEFINITELY_TRUE;
            }
            //noinspection ConstantConditions
            if (!hasBeenUnknown && hasBeenFalse) {
                return DEFINITELY_FALSE;
            }
            if (hasBeenTrue) {
                return SOMETIMES_TRUE;
            }
            //noinspection ConstantConditions
            if (hasBeenFalse) {
                return SOMETIMES_FALSE;
            }
            throw new RuntimeException();
        }

        boolean isSometimesTrue() {
            return this == SOMETIMES_TRUE || this == DEFINITELY_TRUE || this == SOMETIMES_TRUE_SOMETIMES_FALSE;
        }

        boolean isSometimesFalse() {
            return this == SOMETIMES_FALSE || this == DEFINITELY_FALSE || this == SOMETIMES_TRUE_SOMETIMES_FALSE;
        }
    }
}
