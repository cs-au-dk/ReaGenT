package dk.webbies.tajscheck.benchmark;

import dk.webbies.tajscheck.util.Util;

/**
 * Created by erik1 on 13-12-2016.
 */
public final class CheckOptions {
    private Builder builder;
    public final int checkDepth;
    public final int checkDepthForUnions;
    public final boolean checkHeap;
    public final boolean splitUnions;
    public final int maxIterationsToRun;
    public final boolean disableSizeOptimization;
    public final boolean combineAllUnconstrainedGenerics;
    public final boolean disableGenerics;
    public final boolean constructAllTypes;
    public final int maxTime;
    public final boolean failOnAny;
    public final boolean makeTSInferLike;
    public final boolean combineNullAndUndefined;

    private CheckOptions(Builder builder) {
        this.checkDepth = builder.checkDepth;
        this.checkDepthForUnions = builder.checkDepthForUnions;
        this.checkHeap = builder.checkHeap;
        this.splitUnions = builder.splitUnions;
        this.maxIterationsToRun = builder.maxIterationsToRun;
        this.maxTime = builder.maxTime;
        this.disableSizeOptimization = builder.disableSizeOptimization;
        this.combineAllUnconstrainedGenerics = builder.combineAllUnconstrainedGenerics;
        this.disableGenerics = builder.disableGenerics;
        this.constructAllTypes = builder.constructAllTypes;
        this.failOnAny = builder.failOnAny;
        this.makeTSInferLike = builder.makeTSInferLike;
        this.combineNullAndUndefined = builder.combineNullAndUndefined;
        this.builder = builder;
    }

    public Builder getBuilder() {
        return new Builder(this.builder);
    }

    public static CheckOptions defaultOptions() {
        return new Builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("SameParameterValue")
    public static final class Builder {
        private int checkDepth = 0; // How deeply should objects be checked when checking (remember, sub-properties are checked in later tests)
        private int checkDepthForUnions = 1; // How deep should the checking be, when determining which
        private boolean checkHeap = false; // Test the loaded module, including all its properties, recursively (no function calls).
        private boolean splitUnions = true; // Split function-signatures, such that no function-signature has a union-type as parameter, they are instead distinct signatures (explodes size of some larger benchmarks, but can be useful for more precise warnings).
        private int maxIterationsToRun = 10000; // The maximum number of iteration to run in the loop, before returning
        private int maxTime = 60 * 1000; // The maximum time to run (the driver tries to exist gracefully, but 10 seconds after the timeout, it is forcefully shutdown).
        private boolean disableSizeOptimization = false; // Disable optimizations for generics (don't do this)
        private boolean combineAllUnconstrainedGenerics = true; // Instead of having distinct values for each unconstrained generic, combine them all into 1. (If disabled, there is a small unsoundness if a generic method extends another generic method).
        private boolean disableGenerics = false; // Disable all generics, the TypeContext becomes empty.
        private boolean constructAllTypes = false; // As in, also construct classes, class-instances, and the module itself we are trying to test (all of them only if there is a method taking it as parameter).
        private boolean failOnAny = true; // If "any" is returned (as in, something that has our "isAnyMarker"), it is a valid warning.
        private boolean makeTSInferLike = false; // Restrict the driver to only check properties, and call constructors (where all arguments are ignored). Kinda equal to the dynamic part of TSInfer.
        private boolean combineNullAndUndefined = false; // null and undefined (last one is also called void) are different types. But errors related to this are anoying, and doesn't matter. Setting this to true makes it not an error.

        private Builder() {}

        private Builder(Builder builder) {
            Util.copyAllFields(this, builder);
        }

        public CheckOptions build() {
            return new CheckOptions(this);
        }

        public void setCombineNullAndUndefined(boolean combineNullAndUndefined) {
            this.combineNullAndUndefined = combineNullAndUndefined;
        }

        public Builder setMakeTSInferLike(boolean makeTSInferLike) {
            this.makeTSInferLike = makeTSInferLike;
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

        public Builder setCheckDepth(int checkDepth) {
            this.checkDepth = checkDepth;
            return this;
        }

        public Builder setConstructAllTypes(boolean constructAllTypes) {
            this.constructAllTypes = constructAllTypes;
            return this;
        }

        public Builder setCombineAllUnconstrainedGenerics(boolean combineAllUnconstrainedGenerics) {
            this.combineAllUnconstrainedGenerics = combineAllUnconstrainedGenerics;
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
