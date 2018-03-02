/*

export module module {
    var bool : true;
    function f(c: () => void): void;
}
*/

var exports = {
    bool: true,
    f: function (c) {
        exports.bool = false;
        c();
        exports.bool = true;
    }
};
module.exports = exports;