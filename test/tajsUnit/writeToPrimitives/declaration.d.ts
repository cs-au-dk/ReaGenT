interface Foo {
    isFoo: boolean;
}

export module module {
    function getFoo(): Foo;
    function testFoo(f: Foo): true;
}