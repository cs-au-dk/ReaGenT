/*interface Foo {
    [index: string]: number
}

export module module {
    function foo(a: Foo): number | undefined;
}*/

module.exports = {
    foo: function (obj) {
        return obj[Math.random() + "foo" + Math.random()]; // TODO: This only works if i actually use an "any string".
    }
};