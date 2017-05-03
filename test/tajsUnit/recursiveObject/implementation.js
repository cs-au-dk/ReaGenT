/*
interface Foo {
    bar : boolean
    foo: 123
    rec: Foo
}


export module module {
    var foo: Foo
}*/

var foo = {
    bar: true,
    foo: 123
};
foo.rec = foo;
module.exports = {
    foo: foo
};