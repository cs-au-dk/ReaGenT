
module.exports = {
    foo: function () {
        var bar = Math.random();

        var alwaysTrue = bar <= 0.5 || bar >= 0.5;
        if (alwaysTrue) {
            return {
                foo: alwaysTrue,
                bar: {
                    baz: true
                }
            }
        } else {
            return {
                foo: alwaysTrue,
                bar: {
                    baz: "NOT EVEN A BOOLEAN"
                }
            }
        }
    }
};