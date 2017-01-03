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

    private CheckOptions(Builder builder) {
        this.checkDepth = builder.checkDepth;
        this.checkDepthForUnions = builder.checkDepthForUnions;
        this.checkHeap = builder.checkHeap;
        this.splitUnions = builder.splitUnions;
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
        public boolean checkHeap = false;
        public boolean splitUnions = true;

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
    }
}
