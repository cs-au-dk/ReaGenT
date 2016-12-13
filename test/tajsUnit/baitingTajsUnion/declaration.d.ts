
// Here it try to bait TAJS, create a union-type, that never fails, but constructed such that it is difficult to see.

// This is always returned
interface Foo1 {
    foo: true;
    bar: {
        baz: true
    };
}

// This is never returned
interface Foo2 {
    foo: false, // <- This is the bait
    bar: {
        baz: false
    };
}

export module module {
    function foo() : Foo1 | Foo2;
}