/*
export module module {
    function id(a: 1 | "1"): number
    function id(a: 2 | "2"): string
}*/

module.exports = {
    id: function (a) {
        return a;
    }
};