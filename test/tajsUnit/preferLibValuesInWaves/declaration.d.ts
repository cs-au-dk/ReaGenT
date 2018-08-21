
interface Foo { // <- library constructed
    foo: true;
    getBaz(): Baz;
    useBaz(b: Baz): true;
}
interface Baz { // <- library constructed.
    baz: true;
    // lib: true;
}

export module module {
    function createBar(f: {
        bar: true // <- client constructed.
    }): Foo;
}