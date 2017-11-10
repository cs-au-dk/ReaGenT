/*
export module module {
    function toRetract(str: string): void;

    function returnsBool(): boolean;
}*/


var bool = true;

module.exports = {
    toRetract: function (str) {
        bool = "NO";
        var a = {};
        var b = {};
        a[str + str] = b[str + str];
        for (var i in a) {
            for (var j in b) {
                b[i] = a[str + i + str];
                a[i] = b[str + i + str];
            }
        }
    },
    returnsBool: function () {
        return bool;
    }
};