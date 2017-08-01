/*
export module module {
    function foo() : {
        bar: number
    };
}*/

module.exports = function () {
    var obj = {};

    Object.defineProperty(obj, 'bar', {
        get: function() { return 321; }
    });

    return obj;
};
