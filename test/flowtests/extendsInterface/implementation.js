/*
declare module "extendsInterface" {
    interface Base {
        foo: string;
    }
    interface Extended extends Base {
        bar: number;
    }

    declare var myVar: Extended;
}

*/

module.exports = {
    myVar: {
        foo: "string",
        bar: 123
    }
}