declare module module {
    function foo(arg: Foo<void>): true;
}

interface Foo<T> {
    bar: Foo<T & string>;
    baz: T;
}

type test = Foo<void> & string;