/*
export module module {
    function foo(): {
        foo(): number
        bar(): string
    }
}*/


module.exports = {
    foo: function () {
        return {
            foo: function () {
                return "123"; // <- Wrong
            },
            bar: function () {
                return "123"; // <- Right
            }
        }
    }
};