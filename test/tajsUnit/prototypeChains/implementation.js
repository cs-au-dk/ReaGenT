/*
export module module {
    var foo: {
        bar : boolean,
        foo: 123
    }
}*/


function Foo() {
    // this.bar = true;
    // this.foo = 123;
}

Foo.prototype.bar = true;
Foo.prototype.foo = 123;

var foo = {
    bar: true,
    foo: 123
}

module.exports = {
    foo: new Foo()
};