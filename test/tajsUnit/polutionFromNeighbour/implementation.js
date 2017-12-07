var L = {};

L.polute = function () {
    L.commonMethod(false);
};
L.testMethod = function () {
    return L.commonMethod(true)
};
L.commonMethod = function (x) {
    return x ? "string" : 2;
};

module.exports = L;