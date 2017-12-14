/*
interface Foo {
    isFoo: boolean;
}

export module module {
    function getFoo(): Foo;
    function testFoo(f: Foo): true;
}*/

module.exports = {
    getFoo: function () {
        return {
            isFoo: true,
            test: true
        }
    },
    testFoo: function (f) {
        if (f.test) {
            return f.isFoo;
        }
        return true;
    }
};