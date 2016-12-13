
export module module {
    function foo(): string | number; // <- Actually never returns a string.
}