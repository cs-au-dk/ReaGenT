interface Foo {
    // priv: () => number;
}

export module module {
    function create(): Foo;
    function consume(f: Foo): number;
}