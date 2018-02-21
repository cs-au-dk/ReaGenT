/*

export module module {
    function foo(): boolean;
}*/

module.exports = {
    foo: function () {
        if (Math.random() > 0.5) {
            return true;
        } else if (Math.random() > 0.5) {
            return undefined;
        } else {
            return null;
        }
    }
};