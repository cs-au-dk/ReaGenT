/*


export module module {
    function tuple(x: [number, number]): true;
    function object(x: {foo: true}): true;
    function array(x: string[]): true;
}*/

module.exports = {
    tuple: function (x) {
        return x instanceof Array;
    },
    object: function (x) {
        return x instanceof Object;
    },
    array: function (x) {
        return x instanceof Array;
    }
};