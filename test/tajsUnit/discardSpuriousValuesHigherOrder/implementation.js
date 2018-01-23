/*
// execution order: module.foo(), module.foo().baz(), module.bar()

interface Test {
    foo(): {
        n: number; // <- read from global, modified by bar.
        baz(): string; // <- always returns a number. Should not show up in final result
    }
    bar(); // <- modifies m.
}

export module module {
    function gen(callback: (x: Test) => void) : void;

}
*/


var n = 123;

var result = {
    foo: function () {
        return {
            n: n,
            baz: function () {
                return 123; // <- not a string
            }
        }
    },
    bar: function () {
        n = "123";
    }
};
module.exports = {
    gen: function (c) {
        c(result);
    }
};