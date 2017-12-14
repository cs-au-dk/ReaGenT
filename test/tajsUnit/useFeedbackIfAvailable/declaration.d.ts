interface Foo {
    foo: true;
}

interface Bar {
    bar: true;
}

export module module {
    function produceFoo(): Foo; // succeeds
    function produceBar(): Bar; // throws an exception.
    function useFoo(foo: Foo): true; // uses feedback
    function useBar(bar: Bar): true; // uses constructed
}