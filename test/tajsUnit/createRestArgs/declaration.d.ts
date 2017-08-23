
// two of exactly the same, but I pass wrong arguments only the second one.
interface Foo {
    (x: boolean, ...xs: number[]): number;
}
interface Bar {
    (x: boolean, ...xs: number[]): number;
}

export module module {
    function foo(f: Foo): true;
    function bar(b: Bar): true;
}