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
    public final int iterationsToRun;
    public final boolean disableSizeOptimization;
    public final boolean combineAllUnconstrainedGenerics;
    public final boolean disableGenerics;
    public final boolean constructAllTypes;

    private CheckOptions(Builder builder) {
        this.checkDepth = builder.checkDepth;
        this.checkDepthForUnions = builder.checkDepthForUnions;
        this.checkHeap = builder.checkHeap;
        this.splitUnions = builder.splitUnions;
        this.iterationsToRun = builder.iterationsToRun;
        this.disableSizeOptimization = builder.disableSizeOptimization;
        this.combineAllUnconstrainedGenerics = builder.combineAllUnconstrainedGenerics;
        this.disableGenerics = builder.disableGenerics;
        this.constructAllTypes = builder.constructAllTypes;
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
        private int checkDepth = 0;
        private int checkDepthForUnions = 1;
        private boolean checkHeap = false;
        private boolean splitUnions = true;
        private int iterationsToRun = 10000;
        private boolean disableSizeOptimization = false;
        private boolean combineAllUnconstrainedGenerics = true;
        private boolean disableGenerics = false;
        private boolean constructAllTypes = false;

        private Builder() {}

        private Builder(Builder builder) {
            Util.copyAllFields(this, builder);
        }

        public CheckOptions build() {
            return new CheckOptions(this);
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

        public Builder setIterationsToRun(int iterationsToRun) {
            this.iterationsToRun = iterationsToRun;
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
