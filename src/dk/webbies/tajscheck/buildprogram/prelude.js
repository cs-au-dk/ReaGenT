var assertionFailures = new Set();
var no_value = {};
function assert (cond, path, expected, actual) {
    if (!cond) {
        assertionFailures.add({
            path: path,
            expected: expected,
            actual: actual
        });
    }
    return cond
}
var print = console.log;
for (var key in console) {
    console[key] = function () {};
}