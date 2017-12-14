// should be constructed early.
interface Callback {
    (): number;
}

// should never be constructed.
interface Foo {
    test: boolean;
}

export module module {
    function takesFoo(f: Foo): Callback;
    function takesSomeFoo(f: Foo): Callback;
    function takesCallback(c: Callback): Foo;
    function takesAFoo(f: Foo): Callback;
    function takesAFooHereAreMoreWords(f: Foo): Callback;
}