declare module foo {
    declare export var Base : {
        prop: true;
    }

    declare export var foo: () => typeof Base.prop;
}
