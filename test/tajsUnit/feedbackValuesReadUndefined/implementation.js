var L = {};

L.Point = function (x) {
    this.x = x;
};
L.Point.prototype = {
    multiplyBy: function () {

        var res = {
            x: 321,
            multiplyBy: function () {
                return res;
            }
        };
        return res;
        // return new L.Point(x); // <- Doing this instead results in the same bug being present. This method just needs to return something of type Point.
    }
};
L.closestPointOnSegment = function (p) {
    var x = p.x;
    return new L.Point(x);
    // return new L.Point(p.x); // <- if you use this return instead, it works.
};

module.exports = L;