!function (e) {
    if ("object" == typeof exports && "undefined" != typeof module) module.exports = e(); else if ("function" == typeof define && define.amd) define([], e); else {
        var n;
        n = "undefined" != typeof window ? window : "undefined" != typeof global ? global : "undefined" != typeof self ? self : this, n.uuid = e()
    }
}(function () {
    return function e(n, r, o) {
        function t(u, f) {
            if (!r[u]) {
                if (!n[u]) {
                    var a = "function" == typeof require && require;
                    if (!f && a) return a(u, !0);
                    if (i) return i(u, !0);
                    var s = new Error("Cannot find module '" + u + "'");
                    throw s.code = "MODULE_NOT_FOUND", s
                }
                var d = r[u] = {exports: {}};
                n[u][0].call(d.exports, function (e) {
                    var r = n[u][1][e];
                    return t(r ? r : e)
                }, d, d.exports, e, n, r, o)
            }
            return r[u].exports
        }

        for (var i = "function" == typeof require && require, u = 0; u < o.length; u++) t(o[u]);
        return t
    }({
        1: [function (e, n, r) {
            var o = e("./v1"), t = e("./v4"), i = t;
            i.v1 = o, i.v4 = t, n.exports = i
        }, {"./v1": 4, "./v4": 5}], 2: [function (e, n, r) {
            function o(e, n) {
                var r = n || 0, o = t;
                return [o[e[r++]], o[e[r++]], o[e[r++]], o[e[r++]], "-", o[e[r++]], o[e[r++]], "-", o[e[r++]], o[e[r++]], "-", o[e[r++]], o[e[r++]], "-", o[e[r++]], o[e[r++]], o[e[r++]], o[e[r++]], o[e[r++]], o[e[r++]]].join("")
            }

            for (var t = [], i = 0; i < 256; ++i) t[i] = (i + 256).toString(16).substr(1);
            n.exports = o
        }, {}], 3: [function (e, n, r) {
            var o = "undefined" != typeof crypto && crypto.getRandomValues && crypto.getRandomValues.bind(crypto) || "undefined" != typeof msCrypto && "function" == typeof window.msCrypto.getRandomValues && msCrypto.getRandomValues.bind(msCrypto);
            if (o) {
                var t = new Uint8Array(16);
                n.exports = function () {
                    return o(t), t
                }
            } else {
                var i = new Array(16);
                n.exports = function () {
                    for (var e, n = 0; n < 16; n++) 0 === (3 & n) && (e = 4294967296 * Math.random()), i[n] = e >>> ((3 & n) << 3) & 255;
                    return i
                }
            }
        }, {}], 4: [function (e, n, r) {
            function o(e, n, r) {
                var o = n && r || 0, d = n || [];
                e = e || {};
                var l = e.node || t, c = void 0 !== e.clockseq ? e.clockseq : i;
                if (null == l || null == c) {
                    var v = u();
                    null == l && (l = t = [1 | v[0], v[1], v[2], v[3], v[4], v[5]]), null == c && (c = i = 16383 & (v[6] << 8 | v[7]))
                }
                var p = void 0 !== e.msecs ? e.msecs : (new Date).getTime(), y = void 0 !== e.nsecs ? e.nsecs : s + 1,
                    b = p - a + (y - s) / 1e4;
                if (b < 0 && void 0 === e.clockseq && (c = c + 1 & 16383), (b < 0 || p > a) && void 0 === e.nsecs && (y = 0), y >= 1e4) throw new Error("uuid.v1(): Can't create more than 10M uuids/sec");
                a = p, s = y, i = c, p += 122192928e5;
                var m = (1e4 * (268435455 & p) + y) % 4294967296;
                d[o++] = m >>> 24 & 255, d[o++] = m >>> 16 & 255, d[o++] = m >>> 8 & 255, d[o++] = 255 & m;
                var g = p / 4294967296 * 1e4 & 268435455;
                d[o++] = g >>> 8 & 255, d[o++] = 255 & g, d[o++] = g >>> 24 & 15 | 16, d[o++] = g >>> 16 & 255, d[o++] = c >>> 8 | 128, d[o++] = 255 & c;
                for (var w = 0; w < 6; ++w) d[o + w] = l[w];
                return n ? n : f(d)
            }

            var t, i, u = e("./lib/rng"), f = e("./lib/bytesToUuid"), a = 0, s = 0;
            n.exports = o
        }, {"./lib/bytesToUuid": 2, "./lib/rng": 3}], 5: [function (e, n, r) {
            function o(e, n, r) {
                var o = n && r || 0;
                "string" == typeof e && (n = "binary" === e ? new Array(16) : null, e = null), e = e || {};
                var u = e.random || (e.rng || t)();
                if (u[6] = 15 & u[6] | 64, u[8] = 63 & u[8] | 128, n) for (var f = 0; f < 16; ++f) n[o + f] = u[f];
                return n || i(u)
            }

            var t = e("./lib/rng"), i = e("./lib/bytesToUuid");
            n.exports = o
        }, {"./lib/bytesToUuid": 2, "./lib/rng": 3}]
    }, {}, [1])(1)
});