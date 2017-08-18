// all here is correct
interface Foo {
    new (): {
        bar: true
    }
}

declare function foo(f: Foo): true;