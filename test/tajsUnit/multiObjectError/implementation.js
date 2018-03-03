/*

export module module {
    var foo : true;
    var bar : true;
    function sideEffect(): void;
}*/

var exports = {
    foo: true,
    bar: true,
    sideEffect: function () {
        exports.foo = false;
        exports.bar = false;
    }
};
module.exports = exports;