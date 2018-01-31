/*
interface MyArray extends Array<string> {
    myMarker: true;
}

export module module {
    function instance(x: MyArray): true;
    function asString(x: MyArray): string;
    function readMarker(x: MyArray): true;
}
*/

module.exports = {
    instance: function (x) {
        return x instanceof Array;
    },
    asString: function (x) {
        var a = Array.prototype.toString.call(x);
        var b = x.toString();
        return a + b;
    },
    readMarker: function (x) {
        return x.myMarker;
    }
};