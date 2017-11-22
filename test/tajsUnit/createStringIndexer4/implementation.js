/*interface Foo {
    [index: string]: number
}

export module module {
    function foo(a: Foo): number | undefined;
}*/

module.exports = {
    foo: function (obj) {
        return obj["foo" + Math.random()];
    }
};