/*

export module module {
    function id(x: {foo: true}): true;
}*/

module.exports = {
    id: function (x) {
        for (key in x) {
            return x[key];
        }
        return true;
    }
};