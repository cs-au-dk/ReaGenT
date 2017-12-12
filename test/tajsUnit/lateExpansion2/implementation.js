/*
export module module {
    function foo(): {foo: true};
    function bar(): {bar: true};
}*/

module.exports = {
    foo: function () {
        return {
            foo: true
        }
    },
    bar: function () {
        return {
            bar: false
        }
    }
};