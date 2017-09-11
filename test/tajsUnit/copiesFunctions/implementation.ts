interface Foo {
    foo: number;
    bar: string;
    baz: boolean;
}

function copy(x: Foo) : Foo {
    const result = {} as any;
    for (let key in x) {
        result[key] = x[key];
    }
    return result;
}
