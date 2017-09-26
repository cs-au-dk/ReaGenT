
export module module {
    function foo(x: Function|Function[]) : true; // <- this signature is split in two, which is actually important for this test.
}