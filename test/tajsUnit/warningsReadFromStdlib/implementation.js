" foo ".substring(); // <- This is OK. It happens in the initialization.

[].push(); // <- This is OK.

module.exports = function foo() {
    // These could be poluted.
    Object.prototype.toString();
    Math.random();
    return " foo ".trim();
};