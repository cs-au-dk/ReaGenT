interface Foo {
    foo: true; // nothing special. We are test object equality.
}

export module module {
    function createFoo() : Foo;
    function equalFoo(f: Foo): true;
}