/*
export module module {
    module fails {
        function foo(): number; // <- also causes bar to fail, because it writes to a variable.
    }
    module succeeds {
        function bar(): string; // <- only fails if foo() executes.
    }
}*/

var str = "this is a string";
module.exports = {
    fails: {
        foo: function () {
            str = 312; // <- not a string.
            return "321";
        }
    },
    succeeds: {
        bar: function () {
            return str;
        }
    }
};