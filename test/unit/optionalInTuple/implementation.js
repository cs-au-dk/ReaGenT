/*

export module module {
    function foo(): [number, number, number?];
}
*/

module.exports = {
    foo: function () {
        if (Math.random() > 0.5) {
            return [1, 2, 3];
        } else {
            return [1, 2];
        }
    }
};