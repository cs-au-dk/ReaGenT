/*
declare module foo {
    declare export class MyKlass<T> {
        // constructor(t: T);
        value: T;
        isFalse: true;
    }

    declare export var bar: () => MyKlass<string>;
}


*/
module.exports = {
    MyKlass: function MyKlass(value) {
        this.value = value;
        this.isFalse = false; // <- This is an error.
    },
    bar: function () {
        return {
            value: 123, // <- is wrong,
            isFalse: true // <- no mistake here.
        }
    }
}