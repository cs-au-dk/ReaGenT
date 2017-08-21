interface Foo<T> {
    value: T;
}

export module module {
    function foo(str: Foo<string>, num: Foo<number>): true;
}