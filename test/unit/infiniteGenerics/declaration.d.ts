
export module module {
    interface Foo<T> {
        bar: Foo<T[]>;
        value: T
    }
    var foo: Foo<boolean>;
}