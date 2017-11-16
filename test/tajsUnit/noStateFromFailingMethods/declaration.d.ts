
export module module {
    module fails {
        function foo(): number; // <- also causes bar to fail, because it writes to a variable.
    }
    module succeeds {
        function bar(): string; // <- only fails if foo() executes.
    }
}