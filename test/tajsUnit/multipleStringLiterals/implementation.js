
module.exports = {
    foo: function () {
        var x = Math.random();
        var v;
        if(x === 0.1) {
            v = 'major';
        } else if(x === 0.2) {
            v = 'minor';
        } else {
            v = 'patch';
        }
        return v;
    }
};