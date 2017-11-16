/*
export module module {
    function foo(c: (foo: number, bar: string) => void): void;
}*/

module.exports = {
    foo: function (c) {
        setTimeout(function () {
            c(); // <- called with no arguments, this should show up.
        })
    }
};