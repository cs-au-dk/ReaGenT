package dk.webbies.tajscheck.benchmark.options;

/**
 * Created by erik1 on 31-07-2017.
 *
 * Options that only work for static testing (TAJSCheck)
 */
@SuppressWarnings("WeakerAccess")
public class StaticOptions {
    public final boolean limitSideEffects;


    public StaticOptions(StaticOptions.Builder builder) {
        this.limitSideEffects = builder.limitSideEffects;
    }

    public static final class Builder {
        private boolean limitSideEffects = false; // Make data-flow only happen between tests that are strictly dependent on each other.

        public Builder setLimitSideEffects(boolean limitSideEffects) {
            this.limitSideEffects = limitSideEffects;
            return this;
        }

        private final CheckOptions.Builder outerBuilder;

        public Builder(CheckOptions.Builder outerBuilder) {
            this.outerBuilder = outerBuilder;
        }

        public CheckOptions.Builder getOuterBuilder() {
            return outerBuilder;
        }

        public StaticOptions build() {
            return new StaticOptions(this);
        }

        public CheckOptions buildOuter() {
            return getOuterBuilder().build();
        }
    }
}
