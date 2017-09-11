// export function foo(x: Date | Function): true;

module.exports = function (x) {
    if (typeof x === "function") {
        return true;
    }
    if (x instanceof Date) {
        return true;
    }
    return false;
};