interface Foo {
    [n: string]: string;
}

export module module {
    function noError(): Foo;
    function hasError(): Foo;
}