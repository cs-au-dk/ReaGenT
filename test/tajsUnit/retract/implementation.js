/*
export module module {
    function toRetract(str: string): void;

    function returnsBool(): boolean;
}*/


var bool = true;

module.exports = {
    toRetract: function (str) {
        bool = "NO";
        var host1 = parseInt;
        var host2 = parseFloat;
        var user1 = function () {};
        var foo;
        if (Math.random() > 0.5) {
            foo = host1;
        }
        if (Math.random() > 0.5) {
            foo = host2;
        }
        if (Math.random() > 0.5) {
            foo = user1;
        }
        console.log(foo.bar);
    },
    returnsBool: function () {
        return bool;
    }
};