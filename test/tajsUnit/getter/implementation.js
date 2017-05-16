/*
export module module {
    function foo() : {
        bar: number
    };
}*/

module.exports = {
    foo: function () {
        var foo = {};
        Object.defineProperty(foo, 'bar', {
            get: function() { return 0xdeadbeef; }
        });
        return foo;
    }
};