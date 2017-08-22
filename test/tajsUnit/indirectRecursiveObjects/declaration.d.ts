interface Foo {
    isFoo: true;
    foo: string,
    Bar: Bar
}

interface Bar {
    isBar: true;
    bar: string,
    Foo: Foo;
}

export module module {
    function foo(f: Foo): true;
}