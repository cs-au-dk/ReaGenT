/*interface Foo {
    value: boolean;
}

export module module {
    function foo(): Foo;
    function test(x: Foo): false;
}*/

var previous;

module.exports = {
    foo: function () {
        previous = {
            value: true
        };
        return previous;
    },
    test: function (x) {
        return x === previous; // if not careful, this will always return false.
    }
};