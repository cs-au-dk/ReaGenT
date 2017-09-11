interface FooBar {
    value: 2,
    foo: {
        bar: FooBar
    }
}

export function foo(): FooBar;