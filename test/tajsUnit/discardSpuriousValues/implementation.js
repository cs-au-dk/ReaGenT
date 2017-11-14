// execution order: module.foo(), module.foo().baz(), module.bar()

/*export module module {
    function foo(): {
        n: number; // <- read from global, modified by bar.
        baz(): string; // <- always returns a number. Should not show up in final result
    }
    function bar(); // <- modifies m.
}*/


var n = 123;

module.exports = {
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