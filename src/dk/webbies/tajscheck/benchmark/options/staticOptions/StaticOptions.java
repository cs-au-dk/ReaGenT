package dk.webbies.tajscheck.benchmark.options.staticOptions;

import dk.au.cs.casa.typescript.types.Type;
import dk.webbies.tajscheck.TypeWithContext;
import dk.webbies.tajscheck.benchmark.options.CheckOptions;
import dk.webbies.tajscheck.benchmark.options.OptionsI;
import dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy.ExpandImmediatelyPolicy;
import dk.webbies.tajscheck.benchmark.options.staticOptions.expansionPolicy.ExpansionPolicy;
import dk.webbies.tajscheck.tajstester.TajsTypeTester;
import dk.webbies.tajscheck.util.Util;

import java.util.function.BiFunction;
import java.util.function.Function;

import static dk.webbies.tajscheck.benchmark.options.staticOptions.StaticOptions.ArgumentValuesStrategy.*;
import static dk.webbies.tajscheck.tajstester.typeCreator.SpecInstantiator.*;

/**
 * Created by erik1 on 31-07-2017.
 *
 * Options that only work for static testing (TAJSCheck)
 */
@SuppressWarnings("WeakerAccess")
public class StaticOptions implements OptionsI {
    public final boolean createSingletonObjects;
    public final boolean betterAnyString;
    public final boolean betterAnyStringWithoutGlobal;
    public final RetractionPolicy retractionPolicy;
    public final ExpansionPolicy expansionPolicy;
    public final boolean propagateStateFromFailingTest;
    public final boolean properWidthSubtyping;
    public final BiFunction<TypeWithContext, TajsTypeTester, ArgumentValuesStrategy> argumentValuesStrategy;
    public final boolean checkAllPropertiesAfterFunctionCall;
    public final boolean useInspector;
    public final boolean useValuesWithMismatches;
    public final boolean ignoreMaybeUndefined;
    public final boolean callbacksAreMGC;
    public final boolean ignoreTypeDecs;
    public final boolean widthSubtpyingIncludesAllObjects;
    public final InstantiationFilter instantiationFilter;

    public enum ArgumentValuesStrategy {
        MIX_FEEDBACK_AND_CONSTRUCTED,
        ONLY_CONSTRUCTED, // <- except if BenchmarkInfo::shouldConstruct states that the type cannot be constructed, then a feedback-value is used.
        FEEDBACK_IF_POSSIBLE,
        FORCE_FEEDBACK,
    }


    private final Builder builder;

    public StaticOptions(StaticOptions.Builder builder) {
        this.createSingletonObjects = builder.createSingletonObjects;
        this.betterAnyString = builder.betterAnyString;
        this.betterAnyStringWithoutGlobal = builder.betterAnyStringWithoutGlobal;
        this.retractionPolicy = builder.retractionPolicy;
        this.expansionPolicy = builder.expansionPolicy;
        this.propagateStateFromFailingTest = builder.propagateStateFromFailingTest;
        this.argumentValuesStrategy = builder.argumentValuesStrategy;
        this.properWidthSubtyping = builder.properWidthSubtyping;
        this.checkAllPropertiesAfterFunctionCall = builder.checkAllPropertiesAfterFunctionCall;
        this.useInspector = builder.useInspector;
        this.useValuesWithMismatches = builder.useValuesWithMismatches;
        this.ignoreMaybeUndefined = builder.ignoreMaybeUndefined;
        this.callbacksAreMGC = builder.callbacksAreMGC;
        this.ignoreTypeDecs = builder.ignoreTypeDecs;
        this.instantiationFilter = builder.instantiationFilter;
        this.widthSubtpyingIncludesAllObjects = builder.widthSubtpyingIncludesAllObjects;
        this.builder = builder;

        if (useValuesWithMismatches && !propagateStateFromFailingTest) {
            throw new RuntimeException("This set of options does not make sense");
        }
    }

    @Override
    public OptionsI.Builder getBuilder() {
        return new Builder(builder);
    }

    public static final class Builder implements OptionsI.Builder {
        private boolean createSingletonObjects = false;
        private boolean betterAnyString = true; // if true all string types are *not* well-known strings in Object or Function prototypes
        private boolean betterAnyStringWithoutGlobal = true;
        private RetractionPolicy retractionPolicy = new NoRetractPolicy();
        private ExpansionPolicy expansionPolicy = new ExpandImmediatelyPolicy();
        private boolean propagateStateFromFailingTest = false;
        private BiFunction<TypeWithContext, TajsTypeTester, ArgumentValuesStrategy> argumentValuesStrategy = (type, tester) -> MIX_FEEDBACK_AND_CONSTRUCTED;
        private boolean properWidthSubtyping = false;
        private boolean checkAllPropertiesAfterFunctionCall = false; // then we run through all the PropertyReadTests after a function-call, to see if it had any harmful side-effects.
        private boolean useInspector = false;
        private boolean useValuesWithMismatches = false;
        private boolean ignoreMaybeUndefined = false;
        private boolean callbacksAreMGC = true;
        private boolean widthSubtpyingIncludesAllObjects = false;
        private boolean ignoreTypeDecs = false; // if true, ALL methods will be called with the "any" type.
        private InstantiationFilter instantiationFilter = (t, v, c, i) -> v;

        private final CheckOptions.Builder outerBuilder;

        public Builder(CheckOptions.Builder outerBuilder) {
            this.outerBuilder = outerBuilder;
        }

        public Builder(Builder builder) {
            this.outerBuilder = builder.outerBuilder;
            Util.copyPrimitives(this, builder);
        }

        public CheckOptions.Builder getOuterBuilder() {
            return outerBuilder;
        }

        public Builder setIgnoreMaybeUndefined(boolean ignoreMaybeUndefined) {
            this.ignoreMaybeUndefined = ignoreMaybeUndefined;
            return this;
        }

        public Builder setInstantiationFilter(InstantiationFilter instantiationFilter) {
            this.instantiationFilter = instantiationFilter;
            return this;
        }

        public Builder setCallbacksAreMGC(boolean callbacksAreMGC) {
            this.callbacksAreMGC = callbacksAreMGC;
            return this;
        }

        public Builder setWidthSubtpyingIncludesAllObjects(boolean widthSubtpyingIncludesAllObjects) {
            this.widthSubtpyingIncludesAllObjects = widthSubtpyingIncludesAllObjects;
            return this;
        }

        public Builder setCheckAllPropertiesAfterFunctionCall(boolean checkAllPropertiesAfterFunctionCall) {
            this.checkAllPropertiesAfterFunctionCall = checkAllPropertiesAfterFunctionCall;
            return this;
        }

        public Builder setUseValuesWithMismatches(boolean useValuesWithMismatches) {
            this.useValuesWithMismatches = useValuesWithMismatches;
            return this;
        }

        public Builder setIgnoreTypeDecs(boolean ignoreTypeDecs) {
            this.ignoreTypeDecs = ignoreTypeDecs;
            return this;
        }

        public Builder setBetterAnyString(boolean betterAnyString) {
            this.betterAnyString = betterAnyString;
            return this;
        }

        public Builder setArgumentValuesStrategy(ArgumentValuesStrategy argumentValuesStrategy) {
            this.argumentValuesStrategy = (type, tester) -> argumentValuesStrategy;
            return this;
        }

        public Builder setArgumentValuesStrategy(BiFunction<TypeWithContext, TajsTypeTester, ArgumentValuesStrategy> argumentValuesStrategy) {
            this.argumentValuesStrategy = argumentValuesStrategy;
            return this;
        }

        public Builder setExpansionPolicy(ExpansionPolicy expansionPolicy) {
            this.expansionPolicy = expansionPolicy;
            return this;
        }

        public Builder setPropagateStateFromFailingTest(boolean propagateStateFromFailingTest) {
            this.propagateStateFromFailingTest = propagateStateFromFailingTest;
            return this;
        }

        public Builder setCreateSingletonObjects(boolean createSingletonObjects) {
            this.createSingletonObjects = createSingletonObjects;
            return this;
        }

        public Builder setUseInspector(boolean useInspector) {
            this.useInspector = useInspector;
            return this;
        }

        public Builder setRetractionPolicy(RetractionPolicy retractionPolicy) {
            this.retractionPolicy = retractionPolicy;
            return this;
        }

        public Builder setProperWidthSubtyping(boolean properWidthSubtyping) {
            this.properWidthSubtyping = properWidthSubtyping;
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
