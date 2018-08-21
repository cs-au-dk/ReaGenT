/*

export module module {
    function readFoo(x: {foo: true}): true;
}
*/

module.exports = {
    readFoo: function (f) {
        return f.foo;
    }
};