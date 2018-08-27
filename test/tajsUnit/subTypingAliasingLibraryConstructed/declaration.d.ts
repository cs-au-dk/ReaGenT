interface Foo {
    foo: true;
}
interface Bar extends Foo {
    bar: true;
}


export module module {
    function getFoo(): Foo;
    function getBar(): Bar;
    function foo(f: Foo): true;
    function bar(b: Bar): true;
}