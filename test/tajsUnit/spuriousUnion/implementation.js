
module.exports = {
    foo: function () {
        // Never returns a string.
        if (Math.random() > 0.5) {
            return Math.random(); // number
        } else {
            return Math.random() > 0.5; // bool
        }
    }
};