/*

export module module {
    const foo: true;
    const bar: true;
}*/

var exports = {
    foo: true
};
if (Math.random() > 0.5) {
    throw new Error();
}
exports.bar = true;
module.exports = exports;