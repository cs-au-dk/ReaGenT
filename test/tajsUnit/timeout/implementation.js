/*
interface Bar {
    foo: true; // <- is actually false. And we want to find that.
}

export module module {
    function foo(): Bar;
    function polute(): true; // <- causes foo() to timeout.
}*/

var poluted = false;

module.exports = {
    foo: function () {
        if (poluted) {
            var a = {};
            var b = {};
            var anystr = Math.random() + "oihn" + Math.random();
            a[anystr] = b[123] = window[anystr];
            for (var k in a) {
                for (var l in b) {
                    b[k] = a[l];
                }
            }

            for (var k in a) {
                for (var l in b) {
                    a[l] = b[k];
                }
            }
        }
        return {
            foo: false // <- is wrong, is supposed to be.
        }
    },
    polute: function () {
        poluted = true;
        return true;
    }
};