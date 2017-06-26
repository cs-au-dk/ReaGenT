interface Foo {
    new (): number; // <- The code tried to return a number, but the semantics of JavaScript make that impossible.
}

declare var construct: Foo;