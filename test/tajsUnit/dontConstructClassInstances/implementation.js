/*

export module module {
    class F {
        constructor(value: number);
        value: number;
    }
    function getHiddenValue(f: F): true;
    function getHiddenValueFromNested(f: F | {f: F}): true;
}*/

module.exports = {
    F: function (value) {
        this.value = value;
        this.test = true; // <- private.
    },
    getHiddenValue: function (f) {
        return f.test;
    },
    getHiddenValueFromNested: function (f) {
        var obj = f;
        var nested = f.f;
        if (f.f) {
            return f.f.test;
        } else {
            return f.test;
        }
    }
};