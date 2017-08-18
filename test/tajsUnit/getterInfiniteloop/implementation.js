/*
export module module {
    function foo() : {
        bar: number
    };
}*/

var obj = {};
module.exports = obj;

Object.defineProperty(obj, 'foo', {
    get: function() { return 321; }
});