/*
export module module {
    function foo() : {
        bar : boolean,
            foo: 123
    }
}*/

module.exports = {
    foo: function () {
        if (Math.random() > 2) { // <- just to bait the static analysis.
            return false;
        } else {
            return {
                bar: true,
                foo: 123
            };
        }
    }
};