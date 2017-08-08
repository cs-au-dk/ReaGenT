/*
export module module {
    function foo(x: boolean, ...xs: number[]): true;
}*/


module.exports = {
    foo: function (x, a, b, c, d, e) {
        for (var i = 1; i < arguments.length; i++) {
            if (typeof arguments[i] !== number) {
                return false;
            }
        }
        return typeof x === "boolean";
    }
};