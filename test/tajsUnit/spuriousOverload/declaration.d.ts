interface Foo {
    (a: number): number;
    (a: string): string; // <- never used.
}


export module module {
    function foo(callback: Foo): void;
}