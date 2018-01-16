function extend(a, b) {
    for (var key in b) {
        a[key] = b[key];
    }
}

function createInstance() {
    var instance = {};
    extend(instance, {put: function () {}});
    return instance;
}

var axios = createInstance();
module.exports = axios;