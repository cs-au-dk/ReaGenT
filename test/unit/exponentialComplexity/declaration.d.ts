
// Results in 658 tests.
// 595 if i disable optimizations (yes, less)
declare module module {
    var foo: L1<string, boolean>;
    var bar: L1<number, symbol>;
}
interface L1<S, T> {
    foo: [[T, L2<T[], this>], T & L1<T, S>];
    bar: [[S, L2<S[], this>], S & L1<S, T>];
}
interface L2<S, T> extends L1<[S, T], [T[], [S[]]]> {
    baz: [[T, [T, this]], T & L1<T, S>];
    quz: [[S, [S, this]], S & L1<S, T>];
}

