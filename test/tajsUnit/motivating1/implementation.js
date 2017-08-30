/*export module module {
    function foo(): string;
    function bar(): number;
    const self: typeof module;
}*/

(function () {
    var jQuery = {};
    jQuery.foo = function () {
        return "foo";
    };
    jQuery.bar = function () {
        return 123 + 4;
    };
    jQuery.self = jQuery;

    module.exports = jQuery;
})();
