/*
export module module {
    class K {
        constructor(v: number);
        value: number;
    }
    function id(x: K[]): true; // fails when feedback value is used.
}*/

module.exports = {
    K: function (v) {
        this.value = v;
        this.isConstructed = true;
    },
    id: function (xs) {
        var x = xs[2];
        if (!x) {
            return true;
        }
        if (x.isConstructed) {
            return "This is constructed!"; // <- supposed to happen!
        }
        return true;
    }
};