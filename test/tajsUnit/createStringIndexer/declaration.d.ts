interface Foo {
    [index: string]: number | string;
    str: string;
    num: number

}

export module module {
    function foo(a: Foo, index: string): true;
}