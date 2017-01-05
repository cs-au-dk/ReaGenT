package dk.webbies.tajscheck.benchmarks;

/**
 * Created by erik1 on 13-12-2016.
 */
public final class CheckOptions {
    public final int checkDepth;
    public final int checkDepthForUnions;
    public final boolean checkHeap;
    public final boolean splitUnions;
    private Builder builder;
    public final int iterationsToRun;
    public final boolean disableSizeOptimization;

    private CheckOptions(Builder builder) {
        this.checkDepth = builder.checkDepth;
        this.checkDepthForUnions = builder.checkDepthForUnions;
        this.checkHeap = builder.checkHeap;
        this.splitUnions = builder.splitUnions;
        this.iterationsToRun = builder.iterationsToRun;
        this.disableSizeOptimization = builder.disableSizeOptimization;
        this.builder = builder;
    }

    public Builder getBuilder() {
        return this.builder;
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

        public CheckOptions build() {
            return new CheckOptions(this);
        }

        public Builder setCheckDepth(int checkDepth) {
            this.checkDepth = checkDepth;
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
    }
}
