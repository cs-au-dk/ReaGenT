/*

export module module {
    function foo() : number | string;
}
*/

module.exports = {
    foo: function () {
        return Math.random() > 0.5 ? 123 : "123"; // <- both in the union.
    }
};