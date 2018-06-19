/*
declare module foo {
    declare export var Base : {
        prop: true;
    }

    declare export var foo: () => typeof Base.prop;
}

*/

module.exports = {
    Base: {
        prop: true
    },
    foo: function() {
        return false; // <- ERROR.
    }
}