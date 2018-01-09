/*

export module module {
    function obj(x: Object) : true;
    function date(x: Date) : true;
    function func(x: Function) : true;
    function regexp(x: RegExp) : true;
    function stringCons(x: StringConstructor) : true;
    function image(x: HTMLImageElement) : true;
    function UInt8Array(x: Uint8Array) : true;

}*/


module.exports = {
    obj: function (x) {
        return x instanceof Object;
    },
    date: function (x) {
        return x instanceof Date;
    },
    func: function (x) {
        return x instanceof Function;
    },
    regexp: function (x) {
        return x instanceof RegExp;
    },
    stringCons: function (x) {
        return x === String;
    },
    image: function (x) {
        return x instanceof HTMLImageElement;
    },
    UInt8Array: function (x) {
        return x instanceof Uint8Array;
    }
};