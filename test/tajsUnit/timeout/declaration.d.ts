interface Bar {
    foo: true; // <- is actually false. And we want to find that.
}

export module module {
    function foo(): Bar;
    function polute(): true; // <- causes foo() to timeout.
}