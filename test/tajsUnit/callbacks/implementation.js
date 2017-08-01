/*
export module module {
    function foo(c: () => number) : string;
}*/


module.exports = {
    foo: function (c) {
        return c(); // <- this is wrong, and that should be detected.
    }
};