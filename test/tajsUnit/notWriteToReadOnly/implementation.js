/*
export module module {
    class K {
        readonly foo: number;
        bar: number;
    }
    function readsFoo(k: K): true;
    function readsBar(k: K): true;
}*/

module.exports = {
    K: function () {
        this.foo = 2;
        this.bar = 3;
    },
    readsFoo: function (k) {
        return k.foo === 2;
    },
    readsBar: function (k) {
        return k.bar === 3; // <- this fails, the client can write to bar.
    }
};