/*
declare module blablabla {
    declare class Document {
        foo: string
    }

    declare var foo: Document;
}

*/

module.exports = {
    Document: function () {
        this.foo = "1223";
    },
    foo: {
        foo: "string"
    }
}