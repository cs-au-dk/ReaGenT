/*
export module module {
    function foo(x: number[]) : true;
}*/


module.exports = {
    foo: function (x) {
        var foo = x instanceof Array;
        return true;
    }
};