/*
interface Foo {
    [n: string]: string;
}

export module module {
    function noError(): Foo;
    function hasError(): Foo;
}*/

module.exports = {
    noError: function () {
        var res = Object.create(null);
        res.foo = "true";
        res.foo = "reioaub";
        res.ouneargon = undefined;
        return res;
    },
    hasError: function () {
        return {
            foo: "oan",
            opiwefiu: true // <- wrong.
        };
    }
};