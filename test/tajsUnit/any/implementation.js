/*
export module module {
    function aliasing(x: any): true;
    function coerceToBool(x: any): true;
    function notAlwaysCrash(x: any): true;
    function nestedProps(x: any): true;
    function asFunction(x: any): true;
    function asConstructor(x: any): true;
    function throws(x: any): true;
}*/

module.exports = {
    aliasing: function (x) {
        x.foo = "bar";
        return x.foo === "bar";
    },
    coerceToBool: function (x) {
        return !!x;
    },
    notAlwaysCrash: function (x) {
        var bar = x.foo;
        return true;
    },
    nestedProps: function (x) {
        var foo = x.foo.bar;
        return true;
    },
    asFunction: function (x) {
        x()(1)(2)(123, 23, 4, 5);
        return true;
    },
    asConstructor: function (x) {
        new (new x(x(), new x()));
        return true;
    },
    throws: function (x) {
        try {
            x();
        } catch (e) {
            try {
                var blab = x.foo;
            } catch (e) {
                return false;
            }
        }
        return true;
    }
};