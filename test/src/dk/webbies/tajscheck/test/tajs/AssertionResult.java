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
        DEFINITELY_FALSE("always false"),
        DEFINITELY_TRUE("always true"),
        SOMETIMES_TRUE("sometimes true (otherwise unknown)"),
        SOMETIMES_FALSE("sometimes false (otherwise unknown)"),
        SOMETIMES_TRUE_SOMETIMES_FALSE("sometimes true, sometimes false"),
        ALWAYS_UNKNOWN("always unknown");

        final String pretty;

        BooleanResult(String pretty) {
            this.pretty = pretty;
        }

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
