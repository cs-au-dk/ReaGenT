
export module module {
    function foo(): { // <- always returns undef.
        foo: boolean,
        bar: number,
        baz: string
    };
}