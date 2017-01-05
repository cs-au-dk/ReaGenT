/*
export module module {
    interface Foo {
        foo: this;
    }
    interface Bar extends Foo {
        bar: this;
    }

    function baz(): Bar;
}*/

module.exports = {
    baz: function () {
        function Foo() {
            this.foo = function () {
                return new Foo(); // <- Wrong.
            }
        }

        function Bar() {
            Foo.call(this);
            this.bar = function () {
                return this;
            }
        }

        return bar;
    }
};