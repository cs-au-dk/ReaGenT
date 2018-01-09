/*
type MyType = {
    bar: Date
};
export module module {
    function bar(x: MyType): false;
    function foo(x: MyType): number;
}*/

module.exports = {
    foo: function (x) {
        return x.bar.getTime();
    },
    bar: function (x) {
        return x.bar.getTime();
    }
};