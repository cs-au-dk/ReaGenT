/*

export module module {
    function foo(c: () => void): void;
    function fails(): true;
}*/

var bool = true;

module.exports = {
    foo: function (c) {
        bool = false;
        c();
        bool = true;
    },
    fails: function () {
        return bool;
    }
};