/*

export module module {
    function foo(): true;
}*/

module.exports = {
    foo: function () {
        var str;
        if (Math.random() > 0.5) {
            str = "2 == 3";
        } else {
            str = "3 == 2";
        }
        eval(str);
        return true;
    }
};