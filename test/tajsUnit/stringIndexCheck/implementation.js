/*
interface Foo {
    [n: string]: string;
}

export module module {
    function nullPrototype(): Foo;
    function defaultPrototype(): Foo;
}*/

module.exports = {
    nullPrototype: function () {
        var res = Object.create(null);
        res.foo = "true";
        res.foo = "reioaub";
        res.ouneargon = undefined;
        return res;
    },
    defaultPrototype: function () {
        return {
            foo: "oan",
            beaiou: "iounga",
            aoiubear: undefined
        };
    }
};