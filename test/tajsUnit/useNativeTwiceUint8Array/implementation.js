/*
type MyType = {
    bar: Uint8Array
};
export module module {
    function bar(x: MyType): false;
    function foo(x: MyType): number;
}*/

module.exports = {
    foo: function (x) {
        return x.bar[2];
    },
    bar: function (x) {
        return x.bar[3];
    }
};