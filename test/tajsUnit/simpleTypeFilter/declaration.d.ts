
interface Foo {
    foo: true;
}

export module module {
    function makeFoo(): Foo; // <- this returns Foo | undefined. but that is suppresed.
    function useFoo(f: Foo): true; // <- this will fail if recieving "Foo | undefined", which it will do if the above isn't filtered.
}