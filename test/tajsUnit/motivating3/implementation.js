/*
export module module {
    function foo(): number;
    function bar(): string;
}
*/

var side = 0;
module.exports = {
    foo: function () {
        if (side > 2) {
            return "foo";
        }
        return 123;
    },
    bar: function () {
        side++;
        return "string";
    }
};