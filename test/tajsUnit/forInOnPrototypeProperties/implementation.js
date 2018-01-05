function bind() {
    return function wrap() {};
};
function extend(a, b) {
    for (var key in b) {
        a["request"] = bind();
    }
}

function Axios() {}
Axios.prototype.request = 1;

var axios = bind();
extend(axios, Axios.prototype);

module.exports = axios;