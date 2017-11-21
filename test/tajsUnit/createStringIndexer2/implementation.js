/*interface Foo {
    [index: string]: number
}

export module module {
    function foo(a: Foo, index: string): number;
}*/

module.exports = {
    foo: function (obj, index) {
        return obj[index];
    }
};