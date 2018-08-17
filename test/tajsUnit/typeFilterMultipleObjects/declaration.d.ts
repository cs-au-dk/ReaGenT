
interface Foo {
    dirty: number; // <- is number|string in one object, number|bool in anoter. (the number is always 123 though).
}

export module module {
    function getFoo(): Foo;
    function useFoo(f: Foo): true; // this must test that strong updates can be made on f, and that dirty === 123.
}