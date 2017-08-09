/*interface Foo {
    (x: number) : number;
    (x: string) : number;
}

export module module {
    function foo(c: Foo) : string;
}*/

module.exports = {
    foo: function (c) {
        return c(true); // <- not a valid overload, should be detected as such
    }
};