package dk.webbies.tajscheck.benchmark;

import dk.webbies.tajscheck.util.Util;

/**
 * Created by erik1 on 13-12-2016.
 */
public final class CheckOptions {
    private Builder builder;
    public final int checkDepthUseValue;
    public final int checkDepthReport;
    public final int checkDepthForUnions;
    public final boolean checkHeap;
    public final boolean splitUnions;
    public final int maxIterationsToRun;
    public final boolean disableSizeOptimization;
    public final boolean combineAllUnboundGenerics;
    public final boolean disableGenerics;
    public final boolean constructClassInstances; // TODO: Test.
    public final boolean constructClassTypes; // TODO: Test.
    public final boolean constructAllTypes; // TODO: Test.
    public final int maxTime;
    public final boolean failOnAny;
    public final boolean makeTSInferLike;
    public final boolean combineNullAndUndefined;
    public final boolean writePrimitives;
    public final boolean writeAll;
    public final boolean firstMatchSignaturePolicy;
    public final boolean useAssertTypeFunctions;


    private CheckOptions(Builder builder) {
        this.checkDepthUseValue = builder.checkDepthUseValue;
        this.checkDepthForUnions = builder.checkDepthForUnions;
        this.checkHeap = builder.checkHeap;
        this.splitUnions = builder.splitUnions;
        this.maxIterationsToRun = builder.maxIterationsToRun;
        this.maxTime = builder.maxTime;
        this.disableSizeOptimization = builder.disableSizeOptimization;
        this.combineAllUnboundGenerics = builder.combineAllUnboundGenerics;
        this.disableGenerics = builder.disableGenerics;
        this.failOnAny = builder.failOnAny;
        this.makeTSInferLike = builder.makeTSInferLike;
        this.combineNullAndUndefined = builder.combineNullAndUndefined;
        this.writePrimitives = builder.writePrimitives;
        this.writeAll = builder.writeAll;
        this.checkDepthReport = builder.checkDepthReport;
        this.firstMatchSignaturePolicy = builder.firstMatchSignaturePolicy;
        this.useAssertTypeFunctions = builder.useAssertTypeFunctions;
        this.constructClassInstances = builder.constructClassInstances;
        this.constructClassTypes = builder.constructClassTypes;
        this.constructAllTypes = builder.constructAllTypes;
        this.builder = builder;

    }

    public Builder getBuilder() {
        return new Builder(this.builder);
    }

    public static CheckOptions defaultOptions() {
        return new Builder().build();
    }

    public static CheckOptions errorFindingOptions(CheckOptions options) {
        return options.getBuilder()
                .setMaxTime(30 * 1000)
                .setCombineNullAndUndefined(true)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("SameParameterValue")
    public static final class Builder {
        private int checkDepthUseValue = 0; // How deeply should objects be checked, when seeing if the value should be used.
        private int checkDepthReport = 2; // How deeply should objects be checked when seeing if an error should be reported. (The above will also report warnings).
        private int checkDepthForUnions = 1; // How deep should the checking be, when determining which
        private boolean checkHeap = false; // Test the loaded module, including all its properties, recursively (no function calls).
        private boolean splitUnions = true; // Split function-signatures, such that no function-signature has a union-type as parameter, they are instead distinct signatures (explodes size of some larger benchmarks, but can be useful for more precise warnings).
        private int maxIterationsToRun = -1; // The maximum number of iteration to run in the loop, before returning
        private int maxTime = 10 * 1000; // The maximum time to run (the driver tries to exist gracefully, but 10 seconds after the timeout, it is forcefully shutdown).
        private boolean disableSizeOptimization = false; // Disable optimizations for generics (don't do this)
        private boolean combineAllUnboundGenerics = true; // Instead of having distinct values for each unbound generic, combine them all into 1. (If disabled, there is a small unsoundness if a generic method extends another generic method).
        private boolean disableGenerics = false; // Disable all generics, the TypeContext becomes empty.
        private boolean constructClassInstances = false; // Construct random values of class instance types.
        private boolean constructClassTypes = false; // Construct ramdom values of class types.
        private boolean constructAllTypes; // No filters on construction
        private boolean failOnAny = true; // If "any" is returned (as in, something that has our "isAnyMarker"), it is a valid warning.
        private boolean makeTSInferLike = false; // Restrict the driver to only check properties, and call constructors (where all arguments are ignored). Kinda equal to the dynamic part of TSInfer.
        private boolean combineNullAndUndefined = false; // null and undefined (last one is also called void) are different types. But errors related to this are anoying, and doesn't matter. Setting this to true makes it not an error.

        private boolean writePrimitives = false; // We write to properties that have primitive values.
        private boolean writeAll = false; // Only has effect if above is true. Every single property is potentially written to. Is VERY stupid, will likely overwrite the library before testing it.
        private boolean firstMatchSignaturePolicy = true; // If the first-match-signature policy of TypeScript should be enforced.
        private boolean useAssertTypeFunctions = true; // Wether or not to combine type-cheks into assertType functions, if not they are inlined (slightly bigger, easier to read).


        private Builder() {}

        private Builder(Builder builder) {
            Util.copyAllFields(this, builder);
        }

        public CheckOptions build() {
            return new CheckOptions(this);
        }

        public Builder setCombineNullAndUndefined(boolean combineNullAndUndefined) {
            this.combineNullAndUndefined = combineNullAndUndefined;
            return this;
        }

        public Builder setFirstMatchSignaturePolicy(boolean firstMatchSignaturePolicy) {
            this.firstMatchSignaturePolicy = firstMatchSignaturePolicy;
            return this;
        }

        public Builder setUseAssertTypeFunctions(boolean useAssertTypeFunctions) {
            this.useAssertTypeFunctions = useAssertTypeFunctions;
            return this;
        }

        public Builder setConstructAllTypes(boolean constructAllTypes) {
            this.constructAllTypes = constructAllTypes;
            return this;
        }

        public Builder setMakeTSInferLike(boolean makeTSInferLike) {
            this.makeTSInferLike = makeTSInferLike;
            return this;
        }

        public Builder setCheckDepthReport(int checkDepthReport) {
            this.checkDepthReport = checkDepthReport;
            return this;
        }

        public Builder setWritePrimitives(boolean writePrimitives) {
            this.writePrimitives = writePrimitives;
            return this;
        }

        public Builder setWriteAll(boolean writeAll) {
            this.writePrimitives |= writeAll;
            this.writeAll = writeAll;
            return this;
        }

        public Builder setMaxTime(int maxTime) {
            this.maxTime = maxTime;
            return this;
        }

        public Builder setFailOnAny(boolean failOnAny) {
            this.failOnAny = failOnAny;
            return this;
        }

        public Builder setCheckDepthUseValue(int checkDepthUseValue) {
            this.checkDepthUseValue = checkDepthUseValue;
            return this;
        }

        public Builder setConstructClassInstances(boolean constructClassInstances) {
            this.constructClassInstances = constructClassInstances;
            return this;
        }

        public Builder setConstructClassTypes(boolean constructClassTypes) {
            this.constructClassTypes = constructClassTypes;
            return this;
        }

        public Builder setCombineAllUnboundGenerics(boolean combineAllUnboundGenerics) {
            this.combineAllUnboundGenerics = combineAllUnboundGenerics;
            return this;
        }

        public Builder setCheckDepthForUnions(int checkDepthForUnions) {
            this.checkDepthForUnions = checkDepthForUnions;
            return this;
        }

        public Builder setCheckHeap(boolean checkHeap) {
            this.checkHeap = checkHeap;
            return this;
        }

        public Builder setSplitUnions(boolean splitUnions) {
            this.splitUnions = splitUnions;
            return this;
        }

        public Builder setMaxIterationsToRun(int maxIterationsToRun) {
            this.maxIterationsToRun = maxIterationsToRun;
            return this;
        }

        public Builder setDisableSizeOptimization(boolean disableSizeOptimization) {
            this.disableSizeOptimization = disableSizeOptimization;
            return this;
        }

        public Builder setDisableGenerics(boolean disableGenerics) {
            this.disableGenerics = disableGenerics;
            return this;
        }
    }
}
