interface Foo {
    forEach(eachFn: number): void;
}
interface Bar {
    forEach(search: string): void;
}

declare var Sugar: Foo & Bar;