
export module module {
    function foo(): string | boolean | number; // <- Actually never returns a string.
}