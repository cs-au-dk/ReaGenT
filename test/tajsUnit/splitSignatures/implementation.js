
module.exports = {
    foo: function (callback) {
        // None of these are callbacks using strings.
        callback(123);
        callback(321);
    }
};