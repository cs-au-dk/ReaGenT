/*

export module module {
    function foo(): void;
}*/

module.exports = {
    foo: function () {
        return "foo bar baz".split(" ");
    }
};