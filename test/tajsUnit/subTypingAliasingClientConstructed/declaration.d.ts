interface Foo {
    foo: true;
}
interface Bar extends Foo {
    bar: true;
}


export module module {
    function foo(f: Foo): false;
    function bar(b: Bar): true;
}