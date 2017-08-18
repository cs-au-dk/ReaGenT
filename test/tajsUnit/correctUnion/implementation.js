
module.exports = {
    foo: function () {
        var bar = Math.random();

        var alwaysTrue = bar <= 0.5 || bar >= 0.5;
        if (alwaysTrue) {
            return {
                bar: true
            }
        } else {
            return {
                bar: false
            }
        }
    }
};