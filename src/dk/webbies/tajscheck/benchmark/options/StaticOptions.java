package dk.webbies.tajscheck.benchmark.options;

/**
 * Created by erik1 on 31-07-2017.
 *
 * Options that only work for static testing (TAJSCheck)
 */
public class StaticOptions {

    public StaticOptions(StaticOptions.Builder builder) {
    }

    public static final class Builder {
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
