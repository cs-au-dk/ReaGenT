export module module {
    class K {
        readonly foo: number;
        bar: number;
    }
    function readsFoo(k: K): true;
    function readsBar(k: K): true;
}