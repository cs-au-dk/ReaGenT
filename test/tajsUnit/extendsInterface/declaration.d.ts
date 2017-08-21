interface Bar extends Foo {
    baz: number;
}

interface Foo {
    bar: string,
    foo: boolean
}

export module module {
    function foo(f: Bar): true;
}