/*
declare module "yada" {
    declare export interface Base<T> {
        value: T;
    }

    declare var str: Base<string>;
    declare var num: Base<number>;
}

*/

module.exports = {
    str: {
        value: "string"
    },
    num: {
        value: 123
    }
}