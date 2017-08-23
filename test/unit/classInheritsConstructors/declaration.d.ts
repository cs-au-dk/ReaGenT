declare module module {
    class Foo {
        constructor(n: number);
    }
    class Bar extends Foo {
        // no constructor, in this case, the constructor is inherited from Foo.
    }
    class Baz extends Foo {
        constructor(s: string) // this overrides the one in Foo.
    }
    function foo(f: typeof Foo, b: typeof Baz): true;
}
