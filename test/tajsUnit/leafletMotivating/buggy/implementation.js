(function (window, document, undefined) {
    var L = {version: "1.0.3"};

    function expose() {
        var oldL = window.L;
        L.noConflict = function () {
            window.L = oldL;
            return this
        };
        window.L = L
    }

    if (typeof module === "object" && typeof module.exports === "object") module.exports = L; else if (typeof define === "function" && define.amd) define(L);
    if (typeof window !== "undefined") expose();
    L.Util = {
        formatNum: function (num, digits) {
            var pow = Math.pow(10, digits || 5);
            return Math.round(num * pow) / pow
        },
        isArray: Array.isArray || function (obj) {
            return Object.prototype.toString.call(obj) === "[object Array]"
        }
    };
    L.Point = function (x, y, round) {
        this.x = round ? Math.round(x) : x;
        this.y = round ? Math.round(y) : y
    };
    L.Point.prototype = {
        clone: function () {
            return new L.Point(this.x, this.y)
        }, add: function (point) {
            return this.clone()._add(L.point(point))
        }, _add: function (point) {
            this.x += point.x;
            this.y += point.y;
            return this
        }, subtract: function (point) {
            return this.clone()._subtract(L.point(point))
        }, _subtract: function (point) {
            this.x -=
                point.x;
            this.y -= point.y;
            return this
        }, divideBy: function (num) {
            return this.clone()._divideBy(num)
        }, _divideBy: function (num) {
            this.x /= num;
            this.y /= num;
            return this
        }, multiplyBy: function (num) {
            return this.clone()._multiplyBy(num)
        }, _multiplyBy: function (num) {
            this.x *= num;
            this.y *= num;
            return this
        }, scaleBy: function (point) {
            return new L.Point(this.x * point.x, this.y * point.y)
        }, unscaleBy: function (point) {
            return new L.Point(this.x / point.x, this.y / point.y)
        }, round: function () {
            return this.clone()._round()
        }, _round: function () {
            this.x =
                Math.round(this.x);
            this.y = Math.round(this.y);
            return this
        }, floor: function () {
            return this.clone()._floor()
        }, _floor: function () {
            this.x = Math.floor(this.x);
            this.y = Math.floor(this.y);
            return this
        }, ceil: function () {
            return this.clone()._ceil()
        }, _ceil: function () {
            this.x = Math.ceil(this.x);
            this.y = Math.ceil(this.y);
            return this
        }, distanceTo: function (point) {
            point = L.point(point);
            var x = point.x - this.x, y = point.y - this.y;
            return Math.sqrt(x * x + y * y)
        }, equals: function (point) {
            point = L.point(point);
            return point.x === this.x && point.y ===
                this.y
        }, contains: function (point) {
            point = L.point(point);
            return Math.abs(point.x) <= Math.abs(this.x) && Math.abs(point.y) <= Math.abs(this.y)
        }, toString: function () {
            return "Point(" + L.Util.formatNum(this.x) + ", " + L.Util.formatNum(this.y) + ")"
        }
    };
    L.point = function (x, y, round) {
        if (x instanceof L.Point) return x;
        if (L.Util.isArray(x)) return new L.Point(x[0], x[1]);
        if (x === undefined || x === null) return x;
        if (typeof x === "object" && "x" in x && "y" in x) return new L.Point(x.x, x.y);
        return new L.Point(x, y, round)
    };
    L.LineUtil = {
        simplify: function (points, tolerance) {
            if (!tolerance ||
                !points.length) return points.slice();
            var sqTolerance = tolerance * tolerance;
            points = this._reducePoints(points, sqTolerance);
            points = this._simplifyDP(points, sqTolerance);
            return points
        }, pointToSegmentDistance: function (p, p1, p2) {
            return Math.sqrt(this._sqClosestPointOnSegment(p, p1, p2, true))
        }, closestPointOnSegment: function (p, p1, p2) {
            return this._sqClosestPointOnSegment(p, p1, p2, false)
        }, _simplifyDP: function (points, sqTolerance) {
            var len = points.length, ArrayConstructor = typeof Uint8Array !== undefined + "" ? Uint8Array : Array,
                markers =
                    new ArrayConstructor(len);
            markers[0] = markers[len - 1] = 1;
            this._simplifyDPStep(points, markers, sqTolerance, 0, len - 1);
            var i, newPoints = [];
            for (i = 0; i < len; i++) if (markers[i]) newPoints.push(points[i]);
            return newPoints
        }, _simplifyDPStep: function (points, markers, sqTolerance, first, last) {
            var maxSqDist = 0, index, i, sqDist;
            for (i = first + 1; i <= last - 1; i++) {
                sqDist = this._sqClosestPointOnSegment(points[i], points[first], points[last], true);
                if (sqDist > maxSqDist) {
                    index = i;
                    maxSqDist = sqDist
                }
            }
            if (maxSqDist > sqTolerance) {
                markers[index] = 1;
                this._simplifyDPStep(points,
                    markers, sqTolerance, first, index);
                this._simplifyDPStep(points, markers, sqTolerance, index, last)
            }
        }, _reducePoints: function (points, sqTolerance) {
            var reducedPoints = [points[0]];
            for (var i = 1, prev = 0, len = points.length; i < len; i++) if (this._sqDist(points[i], points[prev]) > sqTolerance) {
                reducedPoints.push(points[i]);
                prev = i
            }
            if (prev < len - 1) reducedPoints.push(points[len - 1]);
            return reducedPoints
        }, clipSegment: function (a, b, bounds, useLastCode, round) {
            var codeA = useLastCode ? this._lastCode : this._getBitCode(a, bounds), codeB = this._getBitCode(b,
                bounds), codeOut, p, newCode;
            this._lastCode = codeB;
            while (true) {
                if (!(codeA | codeB)) return [a, b];
                if (codeA & codeB) return false;
                codeOut = codeA || codeB;
                p = this._getEdgeIntersection(a, b, codeOut, bounds, round);
                newCode = this._getBitCode(p, bounds);
                if (codeOut === codeA) {
                    a = p;
                    codeA = newCode
                } else {
                    b = p;
                    codeB = newCode
                }
            }
        }, _getEdgeIntersection: function (a, b, code, bounds, round) {
            var dx = b.x - a.x, dy = b.y - a.y, min = bounds.min, max = bounds.max, x, y;
            if (code & 8) {
                x = a.x + dx * (max.y - a.y) / dy;
                y = max.y
            } else if (code & 4) {
                x = a.x + dx * (min.y - a.y) / dy;
                y = min.y
            } else if (code &
                2) {
                x = max.x;
                y = a.y + dy * (max.x - a.x) / dx
            } else if (code & 1) {
                x = min.x;
                y = a.y + dy * (min.x - a.x) / dx
            }
            return new L.Point(x, y, round)
        }, _getBitCode: function (p, bounds) {
            var code = 0;
            if (p.x < bounds.min.x) code |= 1; else if (p.x > bounds.max.x) code |= 2;
            if (p.y < bounds.min.y) code |= 4; else if (p.y > bounds.max.y) code |= 8;
            return code
        }, _sqDist: function (p1, p2) {
            var dx = p2.x - p1.x, dy = p2.y - p1.y;
            return dx * dx + dy * dy
        }, _sqClosestPointOnSegment: function (p, p1, p2, sqDist) {
            var x = p1.x, y = p1.y, dx = p2.x - x, dy = p2.y - y, dot = dx * dx + dy * dy, t;
            if (dot > 0) {
                t = ((p.x - x) * dx + (p.y - y) *
                    dy) / dot;
                if (t > 1) {
                    x = p2.x;
                    y = p2.y
                } else if (t > 0) {
                    x += dx * t;
                    y += dy * t
                }
            }
            dx = p.x - x;
            dy = p.y - y;

            return sqDist ? dx * dx + dy * dy : new L.Point(x, y);
        }
    };
})(window, document);