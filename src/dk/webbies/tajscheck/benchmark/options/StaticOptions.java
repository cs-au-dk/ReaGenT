package dk.webbies.tajscheck.benchmark.options;

import dk.webbies.tajscheck.util.Util;

/**
 * Created by erik1 on 31-07-2017.
 *
 * Options that only work for static testing (TAJSCheck)
 */
@SuppressWarnings("WeakerAccess")
public class StaticOptions implements OptionsI {
    public final boolean limitSideEffects;
    public final boolean killGetters;
    public final boolean createSingletonObjects;
    public final boolean betterAnyString;
    private final Builder builder;


    public StaticOptions(StaticOptions.Builder builder) {
        this.limitSideEffects = builder.limitSideEffects;
        this.killGetters = builder.killGetters;
        this.createSingletonObjects = builder.createSingletonObjects;
        this.betterAnyString = builder.betterAnyString;
        this.builder = builder;
    }

    @Override
    public OptionsI.Builder getBuilder() {
        return new Builder(builder);
    }

    public static final class Builder implements OptionsI.Builder {
        private boolean limitSideEffects = false; // Make data-flow only happen between tests that are strictly dependent on each other.
        private boolean killGetters = false;
        private boolean createSingletonObjects = false;
        public boolean betterAnyString = true; // if true all string types are *not* well-known strings in Object or Function prototypes
        private final CheckOptions.Builder outerBuilder;

        public Builder(CheckOptions.Builder outerBuilder) {
            this.outerBuilder = outerBuilder;
        }

        public Builder(Builder builder) {
            this.outerBuilder = builder.outerBuilder;
            Util.copyPrimitives(this, builder);
        }

        public Builder setLimitSideEffects(boolean limitSideEffects) {
            this.limitSideEffects = limitSideEffects;
            return this;
        }

        public Builder setCreateSingletonObjects(boolean createSingletonObjects) {
            this.createSingletonObjects = createSingletonObjects;
            return this;
        }

        public Builder setKillGetters(boolean killGetters) {
            this.killGetters = killGetters;
            return this;
        }

        public StaticOptions buildInner() {
            return new StaticOptions(this);
        }

        @Override
        public CheckOptions build() {
            return outerBuilder.build();
        }
    }
}
