/*
export module module {
    function foo(x: any): boolean | number | string;
}*/

module.exports = {
    foo: function (x) {
        // since x is any, any of the below should be possible.
        if (x == true) {
            return true;
        }
        if (x === 123) {
            return 123;
        }
        if (x == "foobar") {
            return "foobar";
        }
        throw new Error("Never"); // <- just to not pollute the result.
    }
};