interface Foo {
    (x: number) : number;
    (x: string) : number;
    (x: boolean) : number;
}

export module module {
    function foo(c: Foo) : string;
}