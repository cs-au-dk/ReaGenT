// export function sum(...xs: string[]) : number;

module.exports = function () {
    var sum = 0;
    for (var i = 0; i < arguments.length; i++) {
        var part = arguments[i];
        part && (sum += part);
    }
    return sum;
};
