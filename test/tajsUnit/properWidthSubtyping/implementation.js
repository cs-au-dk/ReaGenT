/*
export module module {
    function foo(x: {foo: true}): true;
}*/

module.exports = {
    foo: function (x) {
        if (!x.foo) {
            return "x.foo was not necessarily true";
        }
        if (x.wubbaLubbaDubDub().whatUp()) {
            return true;
        }
        throw new Error();
    }
};