/*
interface Numbers {
    1 : boolean,
    2: 123
}

export module module {
    function foo() : Numbers
    function bar() : Numbers
}*/

module.exports = {
    foo: function () {
        return {
            1: true,
            2: "123" // <- wrong
        };
    },
    bar: function () {
        return {
            1: true,
            2: 123 // <- right
        };
    }
};