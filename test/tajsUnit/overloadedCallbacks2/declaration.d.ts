interface Foo {
    (x: number) : number;
    (x: string) : number;
}

export module module {
    function foo(c: Foo) : string;
}