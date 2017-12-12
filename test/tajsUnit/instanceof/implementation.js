/*

export module module {
    function func(x: Function) : true;
    function obj(x: object) : true;
    function arr(x: string[]) : true;
}*/


module.exports = {
    func: function (x) {
        return x instanceof Function;
    },
    obj: function (x) {
        return x instanceof Object;
    },
    arr: function (x) {
        return x instanceof Array;
    }
};