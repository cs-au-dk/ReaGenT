/*
export module module {
    class Foo {
        addTemplate: Function;
    }
    class Bar { }
}*/

module.exports = {
    Foo: function () {
        this.addTemplate = function () {};
    },
    Bar: function () {
        this.addTemplate = 123;
    }
};