(function (factory) {
    if (typeof define === "function" && define.amd) {}
    else {
        window["Sortable"] = factory();
    }
})(function () {
    function Sortable(el, options) {
        el['Sortable' + (new Date).getTime()] = this;
    }
    Sortable.prototype =  {
        toArray: function () {}
    };
    Sortable.utils = {
        css: function (el, prop, val) {
            el[prop] = "foo";
        }
    };
    return Sortable;
});