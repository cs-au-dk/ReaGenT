declare var loop: L1<true>;
interface L1<T> {
    bar: L2<T & false>;
}
interface L2<T> {
    foo: L1<T & true>;
    get: T;
}