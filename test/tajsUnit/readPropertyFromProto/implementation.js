/*

export module module {
    function make(): {foo: true}
}*/

module.exports = {
    make: function (x) {
        function C() {

        }
        C.prototype.foo = true;
        return new C();
    }
};