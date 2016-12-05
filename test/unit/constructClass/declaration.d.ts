declare class Foo {
    stuff(): "foo";
}

declare namespace module {
    function foo(arg: typeof Foo): "foo";
}