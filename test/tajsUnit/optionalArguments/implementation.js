/*

export module module {
    function foo(c: (x?: number) => string): void;
}*/

module.exports = {
    foo: function (c) {
        c(123); // all of these
        c(); // should be
        c(undefined); // valid
    }
};