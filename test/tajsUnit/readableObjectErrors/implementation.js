/*
export module module {
    function foo(): { // <- always returns undef.
        foo: boolean,
            bar: number,
            baz: string
    };
}*/

module.exports = {
    foo: function () {
        var res;
        return res;
    }
};