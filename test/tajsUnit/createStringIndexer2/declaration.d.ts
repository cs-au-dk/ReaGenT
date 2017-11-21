interface Foo {
    [index: string]: number
}

export module module {
    function foo(a: Foo, index: string): number | undefined;
}