package dk.webbies.tajscheck.benchmark.options;

import dk.webbies.tajscheck.util.Util;

@SuppressWarnings("WeakerAccess")
public final class CheckOptions {
    private Builder builder;
    public final boolean splitUnions; // Can make things blow up if first-match-policy is also used.
    public final boolean disableSizeOptimization;
    public final boolean combineAllUnboundGenerics;
    public final boolean disableGenerics;
    public final boolean constructClassInstances;
    public final boolean constructClassTypes;
    public final boolean constructAllTypes;
    public final boolean constructOnlyPrimitives;
    public final boolean combineNullAndUndefined;
    public final boolean writePrimitives;
    public final boolean writeAll;
    public final boolean onlyInitialize;
    public final boolean useTracified;

    public final DynamicOptions dynamicOptions;
    public final StaticOptions staticOptions;

    private CheckOptions(Builder builder) {
        this.dynamicOptions = builder.dynamicOptions.build();
        this.staticOptions = builder.staticOptions.build();
        this.splitUnions = builder.splitUnions;
        this.disableSizeOptimization = builder.disableSizeOptimization;
        this.combineAllUnboundGenerics = builder.combineAllUnboundGenerics;
        this.disableGenerics = builder.disableGenerics;
        this.combineNullAndUndefined = builder.combineNullAndUndefined;
        this.writePrimitives = builder.writePrimitives;
        this.writeAll = builder.writeAll;
        this.constructClassInstances = builder.constructClassInstances;
        this.constructClassTypes = builder.constructClassTypes;
        this.constructAllTypes = builder.constructAllTypes;
        this.constructOnlyPrimitives = builder.constructOnlyPrimitives;
        this.onlyInitialize = builder.onlyInitialize;
        this.useTracified = builder.useTracified;
        this.builder = builder;
    }

    public Builder getBuilder() {
        return new Builder(this.builder);
    }

    public static CheckOptions defaultOptions() {
        return new Builder().build();
    }

    public static CheckOptions.Builder errorFindingOptions(CheckOptions.Builder options) {
        return options
                .setMaxTime(30 * 1000)
                .setCombineNullAndUndefined(true);
    }

    public static CheckOptions.Builder monitorUnknownPropertyAccesses(CheckOptions.Builder options) {
        return options
                .setMonitorUnkownPropertyAccesses(true);
    }

    public static CheckOptions.Builder errorFindingOptions(CheckOptions options) {
        return errorFindingOptions(options.getBuilder());
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("SameParameterValue")
    public static final class Builder {
        public boolean splitUnions = true; // Split function-signatures, such that no function-signature has a union-type as parameter, they are instead distinct signatures (explodes size of some larger benchmarks, but can be useful for more precise warnings).
        public boolean disableSizeOptimization = false; // Disable optimizations for generics (don't do this)
        public boolean combineAllUnboundGenerics = true; // Instead of having distinct values for each unbound generic, combine them all into 1. (If disabled, there is a small unsoundness if a generic method extends another generic method).
        public boolean disableGenerics = false; // Disable all generics, the TypeContext becomes empty.
        public boolean constructClassInstances = false; // Construct random values of class instance types.
        public boolean constructClassTypes = false; // Construct ramdom values of class types.
        public boolean constructAllTypes = false; // No filters on construction (overrides construct class type options)
        public boolean constructOnlyPrimitives = false; // Basically emulate RANDOOP. Overrides all other construct type options.
        public boolean combineNullAndUndefined = false; // null and undefined (last one is also called void) are different types. But errors related to this are anoying, and doesn't matter. Setting this to true makes it not an error.

        public boolean writePrimitives = false; // We write to properties that have primitive values.
        public boolean writeAll = false; // Only has effect if above is true. Every single property is potentially written to. Is VERY stupid, will likely overwrite the library before testing it.
        public boolean onlyInitialize = false; // If true, all tests except the initializing test is removed.

        public boolean useTracified = false;

        public DynamicOptions.Builder dynamicOptions = new DynamicOptions.Builder(this);
        public StaticOptions.Builder staticOptions = new StaticOptions.Builder(this);

        private Builder() {}

        private Builder(Builder builder) {
            Util.copyPrimitives(this, builder);
            Util.copyPrimitives(this.dynamicOptions, builder.dynamicOptions);
            Util.copyPrimitives(this.staticOptions, builder.staticOptions);
        }

        public CheckOptions build() {
            return new CheckOptions(this);
        }

        public Builder setUseTracified(boolean useTracified) {
            this.useTracified = useTracified;
            return this;
        }

        public Builder setCombineNullAndUndefined(boolean combineNullAndUndefined) {
            this.combineNullAndUndefined = combineNullAndUndefined;
            return this;
        }

        public Builder setFirstMatchSignaturePolicy(boolean firstMatchSignaturePolicy) {
            this.dynamicOptions.firstMatchSignaturePolicy = firstMatchSignaturePolicy;
            return this;
        }

        public Builder setUseAssertTypeFunctions(boolean useAssertTypeFunctions) {
            this.dynamicOptions.useAssertTypeFunctions = useAssertTypeFunctions;
            return this;
        }

        public Builder setOnlyInitialize(boolean onlyInitialize) {
            this.onlyInitialize = onlyInitialize;
            return this;
        }

        public Builder setConstructOnlyPrimitives(boolean constructOnlyPrimitives) {
            this.constructOnlyPrimitives = constructOnlyPrimitives;
            return this;
        }

        public Builder setConstructAllTypes(boolean constructAllTypes) {
            this.constructAllTypes = constructAllTypes;
            return this;
        }

        public Builder setMakeTSInferLike(boolean makeTSInferLike) {
            this.dynamicOptions.makeTSInferLike = makeTSInferLike;
            return this;
        }

        public Builder setCheckDepthReport(int checkDepthReport) {
            this.dynamicOptions.setCheckDepthReport(checkDepthReport);
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
            this.dynamicOptions.maxTime = maxTime;
            return this;
        }

        public Builder setFailOnAny(boolean failOnAny) {
            this.dynamicOptions.failOnAny = failOnAny;
            return this;
        }

        public Builder setCheckDepthUseValue(int checkDepthUseValue) {
            this.dynamicOptions.setCheckDepthUseValue(checkDepthUseValue);
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
            this.dynamicOptions.setCheckDepthForUnions(checkDepthForUnions);
            return this;
        }

        public Builder setSplitUnions(boolean splitUnions) {
            this.splitUnions = splitUnions;
            return this;
        }

        public Builder setMaxIterationsToRun(int maxIterationsToRun) {
            this.dynamicOptions.maxIterationsToRun = maxIterationsToRun;
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

        public Builder setMonitorUnkownPropertyAccesses(boolean monitorUnknownPropertyAccesses) {
            this.dynamicOptions.monitorUnknownPropertyAccesses = monitorUnknownPropertyAccesses;
            return this;
        }

    }
}
