interface Foo {
    (a: number | string): number; // <- The string overload is never used.
}


export module module {
    function foo(callback: Foo): void;
}