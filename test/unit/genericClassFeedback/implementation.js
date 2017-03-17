/*
declare module module {
    class Foo<T> {
        constructor(t: T);
    value: T;
}
    function returnsFalse<T>(foo: Foo<T>): true;
}*/

function Foo(t) {
    this.value = t;
}

module.exports = {
    Foo: Foo,
    returnsFalse: function () {
        return false;
    }
};