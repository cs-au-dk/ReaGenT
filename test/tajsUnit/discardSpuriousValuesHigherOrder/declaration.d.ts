// execution order: module.foo(), module.foo().baz(), module.bar()

interface Test {
    foo(): {
        n: number; // <- read from global, modified by bar.
        baz(): string; // <- always returns a number. Should not show up in final result
    }
    bar(); // <- modifies m.
}

export module module {
    function gen(callback: (x: Test) => void) : void;

}