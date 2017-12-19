// export function foo(x: Date | {bar: true} | (() => boolean)): true;

module.exports = function (x) {
    if (typeof x === "function") {
        return true;
    }
    if (x.bar) {
        return x.bar;
    }
    // if (x instanceof Object) {
    //     return true
    // }
    return x;
};