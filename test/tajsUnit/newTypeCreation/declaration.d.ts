
type NumToString = (n: number) => string; // has a hidden "isLib" property.

// I can get NumToString, but I cannot get a clean Foo.
// This also has "bar" property that is: true.
interface Foo {
    foo: NumToString;
    dirty: number; // <- is always number|string.
}

export module module {
    function getFoo(): Foo;
    function useFoo(f: Foo): true; // this must test that the NumToString is library-constructed! And that the "bar" property is present.
}