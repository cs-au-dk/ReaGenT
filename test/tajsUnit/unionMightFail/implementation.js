/*

export module module {
    function foo() : number | string;
}
*/

module.exports = {
    foo: function () {
        if (Math.random() > 2) {
            return true; // <- Not in the union.
        } else {
            return Math.random() > 0.5 ? 123 : "123"; // <- both in the union.
        }
    }
};