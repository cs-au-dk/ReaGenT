/*
export module module {
    function foo(): true;
    var pass: true;
}*/


var obj = {
    foo: function () {
        obj.pass = false;
        return true;
    },
    pass: true
};
module.exports = obj;