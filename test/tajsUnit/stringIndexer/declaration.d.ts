
// all of this is correct
interface Foo {
    [n: string]: number | null; // or null because the __proto__ is null.
    bar : 123;
}

declare function foo() : Foo;