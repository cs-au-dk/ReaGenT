interface MyCollection<T> {
    get(i: number): T | undefined;
    add(t: T): void;
    values: T[]
}


export module module {
    var foo: MyCollection<number>;
    var bar: MyCollection<string>;
    var baz: MyCollection<boolean>;
}