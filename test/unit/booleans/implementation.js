/*
interface CustomBoolean {
    quz: true;
}

export module module {
    var foo: Boolean;
    var bar: boolean;
    var baz: CustomBoolean;
}*/


module.exports = {
    foo: new Boolean(true),
    bar: true,
    baz: (function () {
        var res = new Boolean(true);
        res.quz = true;
        return res;
    })();
};