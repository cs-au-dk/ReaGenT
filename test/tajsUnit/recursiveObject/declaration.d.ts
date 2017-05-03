interface Foo {
    bar : boolean
    foo: 123
    rec: Foo
}


export module module {
    var foo: Foo
}