/*interface Foo {
    (x: number) : number;
    (x: string) : number;
    (x: boolean) : number;
}

export module module {
    function foo(c: Foo) : string;
}*/

module.exports = {
    foo: function (c) {
        return c(123) + c("string") + c(true); // <- this is wrong, and that should be detected.
    }
};