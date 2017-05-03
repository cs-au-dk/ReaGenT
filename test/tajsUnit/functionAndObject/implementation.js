/*
export module module {
    var foo: {
        bar : boolean,
        foo: 123
    }
}*/


module.exports = {
    foo: (function () {
        var result = function () {

        };
        result.bar = true;
        result.foo = 123;
        return result;
    })()
};