package dk.webbies.tajscheck.benchmark.options;

/**
 * Created by erik1 on 31-07-2017.
 *
 * Options that only work for dynamic testing (TSTest)
 */
@SuppressWarnings("WeakerAccess")
public class DynamicOptions {
    public final int checkDepthUseValue;
    public final int checkDepthReport;
    public final int checkDepthForUnions;
    public final int maxIterationsToRun;
    public final int maxTime;
    public final boolean failOnAny;
    public final boolean makeTSInferLike;
    public final boolean firstMatchSignaturePolicy;
    public final boolean monitorUnknownPropertyAccesses;
    public final boolean useAssertTypeFunctions;

    public DynamicOptions(Builder builder) {
        this.checkDepthForUnions = builder.checkDepthForUnions;
        this.checkDepthUseValue = builder.checkDepthUseValue;
        this.checkDepthReport = builder.checkDepthReport;
        this.maxIterationsToRun = builder.maxIterationsToRun;
        this.maxTime = builder.maxTime;
        this.failOnAny = builder.failOnAny;
        this.makeTSInferLike = builder.makeTSInferLike;
        this.firstMatchSignaturePolicy = builder.firstMatchSignaturePolicy;
        this.monitorUnknownPropertyAccesses = builder.monitorUnknownPropertyAccesses;
        this.useAssertTypeFunctions = builder.useAssertTypeFunctions;
    }

    public boolean makeSeparateReportAssertions() {
        return this.checkDepthReport == this.checkDepthUseValue;
    }

    public static final class Builder {
        private final CheckOptions.Builder outerBuilder;
        public int checkDepthUseValue = 0; // How deeply should objects be checked, when seeing if the value should be used.
        public int checkDepthReport = 2; // How deeply should objects be checked when seeing if an error should be reported. (The above will also report warnings).
        public int checkDepthForUnions = 1; // How deep should the checking be, when determining which
        public int maxIterationsToRun = -1; // The maximum number of iteration to run in the loop, before returning
        public int maxTime = 10 * 1000; // The maximum time to run (the driver tries to exist gracefully, but 10 seconds after the timeout, it is forcefully shutdown).
        public boolean failOnAny = true; // If "any" is returned (as in, something that has our "isAnyMarker"), it is a valid warning.
        public boolean makeTSInferLike = false; // Restrict the driver to only check properties, and call constructors (where all arguments are ignored). Kinda equal to the dynamic part of TSInfer.
        public boolean firstMatchSignaturePolicy = true; // If the first-match-signature policy of TypeScript should be enforced.
        public boolean monitorUnknownPropertyAccesses = false;
        public boolean useAssertTypeFunctions = true; // Whether or not to combine type-cheks into assertType functions, if not they are inlined (slightly bigger, easier to read).


        public Builder(CheckOptions.Builder outerBuilder) {
            this.outerBuilder = outerBuilder;
        }

        public CheckOptions.Builder getOuterBuilder() {
            return outerBuilder;
        }

        public Builder setCheckDepthReport(int checkDepthReport) {
            this.checkDepthReport = checkDepthReport;
            return this;
        }

        public Builder setCheckDepthUseValue(int checkDepthUseValue) {
            this.checkDepthUseValue = checkDepthUseValue;
            return this;
        }

        public Builder setCheckDepthForUnions(int checkDepthForUnions) {
            this.checkDepthForUnions = checkDepthForUnions;
            return this;
        }

        public DynamicOptions build() {
            return new DynamicOptions(this);
        }

        public CheckOptions buildOuter() {
            return getOuterBuilder().build();
        }
    }
}
