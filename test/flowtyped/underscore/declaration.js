// @flow
/* eslint-disable */

// type definitions for (some of) underscore

type FnIteratee<T> = (t: T, index: number, array: Array<T>) => boolean;

declare module "underscore" {
    declare type UnaryFn<A, R> = (a: A) => R;
    declare type Compose =
        & (<A, B, C, D, E, F, G>(
        fg: UnaryFn<F, G>,
        ef: UnaryFn<E, F>,
        de: UnaryFn<D, E>,
        cd: UnaryFn<C, D>,
        bc: UnaryFn<B, C>,
        ab: UnaryFn<A, B>,
        ...rest: Array<void>
    ) => UnaryFn<A, G>)
        & (<A, B, C, D, E, F>(
        ef: UnaryFn<E, F>,
        de: UnaryFn<D, E>,
        cd: UnaryFn<C, D>,
        bc: UnaryFn<B, C>,
        ab: UnaryFn<A, B>,
        ...rest: Array<void>
    ) => UnaryFn<A, F>)
        & (<A, B, C, D, E>(
        de: UnaryFn<D, E>,
        cd: UnaryFn<C, D>,
        bc: UnaryFn<B, C>,
        ab: UnaryFn<A, B>,
        ...rest: Array<void>
    ) => UnaryFn<A, E>)
        & (<A, B, C, D>(cd: UnaryFn<C, D>, bc: UnaryFn<B, C>, ab: UnaryFn<A, B>, ...rest: Array<void>) => UnaryFn<A, D>)
        & (<A, B, C>(bc: UnaryFn<B, C>, ab: UnaryFn<A, B>, ...rest: Array<void>) => UnaryFn<A, C>)
        & (<A, B>(ab: UnaryFn<A, B>, ...rest: Array<void>) => UnaryFn<A, B>)

    declare function $underscore$Extend<A: {}>(a: A, ...rest: Array<void>): A;
    declare function $underscore$Extend<A: {}, B: {}>(a: A, b: B, ...rest: Array<void>): A & B;
    declare function $underscore$Extend<A: {}, B: {}, C: {}>(a: A, b: B, c: C, ...rest: Array<void>): A & B & C;
    declare function $underscore$Extend<A: {}, B: {}, C: {}, D: {}>(a: A, b: B, c: C, d: D, ...rest: Array<void>): A & B & C & D;
    declare function $underscore$Extend<A: {}, B: {}, C: {}, D: {}, E: {}>(a: A, b: B, c: C, d: D, e: E, ...rest: Array<void>): A & B & C & D & E;

    declare function $underscore$ExtendParameterized<A: {}>(...rest: Array<void>): A;
    declare function $underscore$ExtendParameterized<A: {}, B: {}>(b: B, ...rest: Array<void>): A & B;
    declare function $underscore$ExtendParameterized<A: {}, B: {}, C: {}>(b: B, c: C, ...rest: Array<void>): A & B & C;
    declare function $underscore$ExtendParameterized<A: {}, B: {}, C: {}, D: {}>(b: B, c: C, d: D, ...rest: Array<void>): A & B & C & D;
    declare function $underscore$ExtendParameterized<A: {}, B: {}, C: {}, D: {}, E: {}>(b: B, c: C, d: D, e: E, ...rest: Array<void>): A & B & C & D & E;


    declare var compose: Compose;

    // Handle underscore chainables things.
    declare class UnderscoreWrappedList<T> {
        // Chain
        chain(): UnderscoreChainedList<T>;

        // Handle Collections functions
        each(iteratee: (element: T, index?: number, list?: Array<T>) => void): void;
    }


    // Handle underscore chainables things.
    declare class UnderscoreChainedList<T> {
        // End a Chain
        value(): Array<T>;

    }

    declare class UnderscoreChainedValue<T> {
        value(): T;
    }

    declare class UnderscoreWrappedValue<T> {
        chain(): UnderscoreChainedValue<T>;

        escape(): string;
        // TODO: Probably move this to UnderscoreWrappedNumber or something
        range(): Array<number>;
        isEmpty(): boolean;
    }

    // Handle regular things.
    declare class UnderscoreList {
        // Handle chaining
        chain<T>(a: Array<T>): UnderscoreChainedList<T>;
        chain<T>(v: T): UnderscoreChainedValue<T>;

    }

    declare class UnderscoreFunctions {
        bind(func: Function, object: Object, ...arguments: Array<any>): Function;
    }

    declare class UnderscoreObject {
        keys<K, V>(object: {[keys: K]: V}): Array<K>;
    }

    declare class UnderscoreChainedObject<WrappedObj: {}> {
        value(): WrappedObj;
        keys(): UnderscoreChainedList<$Keys<WrappedObj>>;
    }

    declare class UnderscoreWrappedObject<WrappedObj: {}> {
        chain(): UnderscoreChainedObject<WrappedObj>;

        map<R>(fn: (v: any, k: $Keys<WrappedObj>) => R): Array<R>;
    }

    declare class UnderscoreUtility {
        noConflict(): Underscore;
    }

    declare class UnderscoreWrappedList<T> {
        chain(): UnderscoreChainedList<T>;


    }

    // Have to use a type with $call instead of function type because otherwise this will cause us to lose type
    // information. see: https://github.com/facebook/flow/issues/3781
    declare type WrappedExports = {
        $call:
        // A type that can be an object or an array (usually 'any') should have both return types.
            & (<AnyType: {} & []>(arg: AnyType) => UnderscoreWrappedObject<AnyType> & UnderscoreWrappedList<AnyType>)
            // It's important that UnderscoreWrappedObject, UnderscoreWrappedList takes precedence over UnderscoreWrappedValue
            & (<WrappedObj: {}>(arg: WrappedObj) => UnderscoreWrappedObject<WrappedObj>)
            & (<T>(arg: Array<T>) => UnderscoreWrappedList<T>)
            & (<T>(arg: [T]) => UnderscoreWrappedList<T>)
            & (<T>(arg: T) => UnderscoreWrappedValue<T>)
    }

    declare type Underscore =
        & UnderscoreList
        & UnderscoreFunctions
        & UnderscoreObject
        & UnderscoreUtility
        & WrappedExports;

    declare module.exports: Underscore;
}
