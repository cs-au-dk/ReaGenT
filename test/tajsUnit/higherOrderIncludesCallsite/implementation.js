/*
export module module {
    function foo(c: (a: true) => void): void;
}*/

module.exports = {
    foo: function (c) {
        c(false); // <- error 1.
        c(false); // <- error 2.
    }
};