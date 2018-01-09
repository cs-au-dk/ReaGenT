type MyType = {
    bar: Uint8Array
};
export module module {
    function bar(x: MyType): false;
    function foo(x: MyType): number;
}