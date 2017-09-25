/*interface Foo {
    value: string;
}

export module module {
    function foo(x: Foo): string;
}*/

module.exports = {
    foo: function (x) {
        return x.value;
    }
};