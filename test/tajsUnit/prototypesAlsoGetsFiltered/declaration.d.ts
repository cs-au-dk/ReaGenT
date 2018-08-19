
interface Foo {
    foo: true;
}

export module module {
    function createFoo(): Foo;
    function useFoo(f: Foo): true;
}